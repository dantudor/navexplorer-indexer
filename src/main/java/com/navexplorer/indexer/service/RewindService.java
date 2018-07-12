package com.navexplorer.indexer.service;

import com.navexplorer.library.address.entity.Address;
import com.navexplorer.library.address.entity.AddressTransaction;
import com.navexplorer.library.address.repository.AddressTransactionRepository;
import com.navexplorer.library.address.service.AddressService;
import com.navexplorer.library.address.service.AddressTransactionService;
import com.navexplorer.library.block.entity.Block;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.repository.BlockRepository;
import com.navexplorer.library.block.repository.BlockTransactionRepository;
import com.navexplorer.library.block.service.BlockTransactionService;
import com.navexplorer.library.configuration.service.ConfigurationService;
import com.navexplorer.library.navcoin.service.NavcoinService;
import org.navcoin.response.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RewindService {
    private static final Logger logger = LoggerFactory.getLogger(RewindService.class);

    @Autowired
    ConfigurationService configurationService;

    @Autowired
    BlockTransactionService blockTransactionService;

    @Autowired
    BlockTransactionRepository blockTransactionRepository;

    @Autowired
    BlockRepository blockRepository;

    @Autowired
    AddressTransactionService addressTransactionService;

    @Autowired
    AddressTransactionRepository addressTransactionRepository;

    @Autowired
    AddressService addressService;

    @Autowired
    NavcoinService navcoinService;

    public void rewindTop10Blocks() {
        if (!isActive()) {
            logger.info("Block indexing is not active");
            return;
        }

        logger.info("Rewinding top 10 blocks");
        blockRepository.findTop10ByOrderByHeightDesc().forEach(this::rewindBlock);
    }

    public void rewindToMissingTransaction(String hash) {
        Transaction transaction = navcoinService.getTransactionByHash(hash);
        
        if (transaction != null) {
            rewindToHeight(transaction.getHeight().longValue());
        }
    }

    public void rewindToHeight(Long height) {
        if (!isActive()) {
            logger.info("Block indexing is not active");
            return;
        }

        logger.info("Rewinding to height " + height);
        Block block = blockRepository.findFirstByOrderByHeightDesc();
        if (block == null) {
            logger.error("No blocks found");
        } else if (block.getHeight() > height) {
            rewindBlock(block);
            rewindToHeight(height);
        }
    }

    public void rewindBlock(Block block) {
        if (!isActive()) {
            logger.info("Block indexing is not active");
            return;
        }

        logger.info(String.format("Rewinding block %s", block.getHeight()));

        blockTransactionService.getByHeight(block.getHeight()).forEach(this::rewindTransaction);

        // @Todo WE NEED TO REWIND SIGNALING IN ACTIVE SOFT FORKS!!!!!
        blockRepository.delete(block);
    }

    private void rewindTransaction(BlockTransaction transaction) {
        logger.info(String.format("Rewinding transaction %s", transaction.getHash()));

        addressTransactionService.getTransactionAddresses(transaction.getHash()).forEach(this::rewindAddressTransaction);
        blockTransactionRepository.delete(transaction);
    }

    private void rewindAddressTransaction(AddressTransaction addressTransaction) {
        logger.info(String.format("Rewinding address %s", addressTransaction.getAddress()));
        Address address = addressService.getAddress(addressTransaction.getAddress());

        switch (addressTransaction.getType()) {
            case COLD_STAKING:
                address.setColdStakedCount(address.getColdStakedCount() - 1);
                address.setColdStakedSent(address.getColdStakedSent() - addressTransaction.getSent());
                break;
            case STAKING:
                address.setStakedCount(address.getStakedCount() - 1);
                address.setStakedSent(address.getStakedSent() - addressTransaction.getSent());
                address.setStaked(address.getStaked() - addressTransaction.getAmount());
                break;
            case SEND:
            case RECEIVE:
                if (addressTransaction.isReceived()) {
                    address.setReceivedCount(address.getReceivedCount() - 1);
                    address.setReceived(address.getReceived() - addressTransaction.getReceived());
                } else if (addressTransaction.isSent()) {
                    address.setSentCount(address.getSentCount() - 1);
                    address.setSent(address.getSent() - addressTransaction.getSent());
                }
                break;
            case COMMUNITY_FUND:
                address.setReceivedCount(address.getReceivedCount() - 1);
                address.setReceived(address.getReceived() - addressTransaction.getReceived());
                break;
        }
        address.setBalance(address.getReceived() + address.getStaked() - address.getSent());

        // delete the transaction
        addressTransactionRepository.delete(addressTransaction);

        // reset the address block index
        AddressTransaction lastTransaction = addressTransactionService.getLastTransactionsForAddress(address.getHash());
        address.setBlockIndex(lastTransaction == null ? 0 : lastTransaction.getHeight());
        addressService.save(address);
    }

    private Boolean isActive() {
        return (Boolean) configurationService.getBlockIndexActive().getValue();
    }
}
