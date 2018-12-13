package com;

import com.navexplorer.indexer.address.indexer.AddressLabelIndexer;
import com.navexplorer.indexer.block.rewinder.BlockRewinder;
import com.navexplorer.indexer.communityfund.indexer.PaymentRequestIndexer;
import com.navexplorer.indexer.communityfund.indexer.ProposalIndexer;
import com.navexplorer.indexer.softfork.SoftForkImporter;
import com.navexplorer.library.navcoin.service.NavcoinService;
import com.navexplorer.indexer.block.indexer.BlockIndexer;
import com.navexplorer.indexer.zeromq.Subscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IndexerApplication implements CommandLineRunner {
    @Autowired
    private BlockRewinder blockRewinder;

    @Autowired
    private BlockIndexer blockIndexer;

    @Autowired
    private SoftForkImporter softForkImporter;

    @Autowired
    private AddressLabelIndexer addressLabelIndexer;

    @Autowired
    private Subscriber subscriber;

    @Autowired
    private NavcoinService navcoinService;

    @Autowired
    private ProposalIndexer proposalIndexer;

    @Autowired
    private PaymentRequestIndexer paymentRequestIndexer;

    public static void main(String[] args) {
        SpringApplication.run(IndexerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Current block height in navcoind: " + navcoinService.getInfo().getBlocks());

        softForkImporter.importSoftForks();
        addressLabelIndexer.indexAddressLabels();

        blockRewinder.rewindToHeight(800L);
        blockRewinder.rewindTop10Blocks();
        blockIndexer.indexAllBlocks();

        proposalIndexer.updateAllProposals();
        paymentRequestIndexer.updateAllPaymentRequests();

        System.out.println("Ready to receive blocks from navcoind...");
        subscriber.run();
    }
}