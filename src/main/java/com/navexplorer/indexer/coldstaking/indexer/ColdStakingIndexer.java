package com.navexplorer.indexer.coldstaking.indexer;

import com.navexplorer.indexer.address.indexer.AddressIndexer;
import com.navexplorer.library.address.entity.AddressTransaction;
import com.navexplorer.library.address.entity.AddressTransactionType;
import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.service.BlockTransactionService;
import com.navexplorer.library.coldstaking.entity.ColdStaking;
import com.navexplorer.library.coldstaking.repository.ColdStakingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColdStakingIndexer {
    private static final Logger logger = LoggerFactory.getLogger(ColdStakingIndexer.class);

    @Autowired
    ColdStakingRepository coldStakingRepository;

    @Autowired
    BlockTransactionService blockTransactionService;

    public void indexTransaction(AddressTransaction transaction) {
        logger.info(("Indexing cold staking for height: " + transaction.getHeight()));

        ColdStaking coldStaking = new ColdStaking();
        coldStaking.setHeight(transaction.getHeight());
        coldStaking.setTransaction(transaction.getTransaction());

        if (transaction.getType().equals(AddressTransactionType.STAKING)) {
            coldStaking.setStakingAddress(transaction.getColdStakingAddress());
            coldStaking.setSpendingAddress(transaction.getAddress());
            coldStaking.setStakedSent(transaction.getSent());
            coldStaking.setStakedReceived(transaction.getReceived());
            coldStaking.setStaked(transaction.getReceived() - transaction.getSent());

            coldStakingRepository.save(coldStaking);
            return;
        }

        BlockTransaction blockTransaction = blockTransactionService.getOneByHash(transaction.getTransaction());

    }
}
