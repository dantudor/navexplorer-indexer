package com.navexplorer.indexer.block.listener;

import com.navexplorer.indexer.block.event.MissingTransactionEvent;
import com.navexplorer.indexer.block.rewinder.BlockRewinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class MissingTransactionListenerTest {
    @InjectMocks
    private MissingTransactionListener missingTransactionListener;

    @Mock
    private BlockRewinder blockRewinder;

    @Test
    public void it_can_trigger_a_rewind_to_the_missing_block() {
        String hash = "HASH";

        missingTransactionListener.onApplicationEvent(new MissingTransactionEvent(new Object(), hash));

        verify(blockRewinder).rewindToMissingTransaction(hash);
    }
}
