package com.navexplorer.indexer.block.factory;

import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.entity.Input;
import com.navexplorer.library.block.entity.Output;
import com.navexplorer.library.block.service.BlockTransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.navcoin.response.Transaction;
import org.navcoin.response.transaction.Vin;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class InputFactoryTest {
    @InjectMocks
    private InputFactory inputFactory;

    @Mock
    private BlockTransactionService blockTransactionService;

    @Test
    public void it_can_create_inputs() {
        Transaction apiTransaction = new Transaction();

        Vin vin1 = new Vin();
        vin1.setAddress("VIN1 ADDRESS");
        vin1.setTxid("VIN1 TX ID");
        vin1.setVout(1);

        BlockTransaction previousTransaction1 = new BlockTransaction();
        previousTransaction1.setHash(vin1.getTxid());
        previousTransaction1.setHeight(500);
        Output output1 = new Output();
        output1.setAmount(10000.0);
        output1.setIndex(1);
        previousTransaction1.setOutputs(Arrays.asList(output1));

        apiTransaction.setVin((Vin[]) Arrays.asList(vin1).toArray());

        when(blockTransactionService.getOneByHash(vin1.getTxid())).thenReturn(previousTransaction1);

        List<Input> inputs = inputFactory.createInputs(apiTransaction);

        assertThat(inputs.get(0).getAddresses()).isEqualTo(Arrays.asList(vin1.getAddress()));
        assertThat(inputs.get(0).getAmount()).isEqualTo(output1.getAmount());
        assertThat(inputs.get(0).getPreviousOutput()).isEqualTo(vin1.getTxid());
        assertThat(inputs.get(0).getPreviousOutputBlock()).isEqualTo(previousTransaction1.getHeight());
        assertThat(inputs.get(0).getIndex()).isEqualTo(vin1.getVout());
    }
}
