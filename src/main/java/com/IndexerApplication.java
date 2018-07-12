package com;

import com.navexplorer.library.navcoin.service.NavcoinService;
import com.navexplorer.indexer.service.BlockIndexingService;
import com.navexplorer.indexer.service.RewindService;
import com.navexplorer.indexer.service.SignallingService;
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
    private RewindService rewindService;

    @Autowired
    private BlockIndexingService blockIndexingService;

    @Autowired
    private SignallingService signallingService;

    @Autowired
    private Subscriber subscriber;

    @Autowired
    private NavcoinService navcoinService;

    public static void main(String[] args) {
        SpringApplication.run(IndexerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Current block height in navcoind: " + navcoinService.getInfo().getBlocks());

        signallingService.importSignals();

        rewindService.rewindTop10Blocks();

        blockIndexingService.indexBlocks();

        System.out.println("Ready to receive blocks from navcoind...");
        subscriber.run();
    }
}