package com.navexplorer.indexer.block.factory;

import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.entity.BlockTransactionType;
import com.navexplorer.library.block.entity.Input;
import com.navexplorer.library.block.entity.Output;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.navcoin.response.Transaction;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class BlockTransactionFactoryTest {
    @InjectMocks
    private BlockTransactionFactory blockTransactionFactory;

    @Mock
    private InputFactory inputFactory;

    @Mock
    private OutputFactory outputFactory;

    @Test
    public void it_can_create_a_block_transaction() {
        Transaction apiTransaction = new Transaction();
        apiTransaction.setHeight(10);

        List<Input> inputs = new ArrayList<>();
        List<Output> outputs = new ArrayList<>();

        when(inputFactory.createInputs(apiTransaction)).thenReturn(inputs);
        when(outputFactory.createOutputs(apiTransaction)).thenReturn(outputs);

        BlockTransaction blockTransaction = blockTransactionFactory.createTransaction(apiTransaction);

        assertThat(blockTransaction.getHash()).isEqualTo(apiTransaction.getTxid());
        assertThat(blockTransaction.getTime()).isEqualTo(new Date(apiTransaction.getTime() * 1000));
        assertThat(blockTransaction.getHeight()).isEqualTo(apiTransaction.getHeight());
        assertThat(blockTransaction.getBlockHash()).isEqualTo(apiTransaction.getBlockhash());
        assertThat(blockTransaction.getInputs()).isEqualTo(inputs);
        assertThat(blockTransaction.getOutputs()).isEqualTo(outputs);

        assertThat(blockTransaction.getType()).isEqualTo(BlockTransactionType.EMPTY);
        assertThat(blockTransaction.getFees()).isEqualTo(0.0);
        assertThat(blockTransaction.getStake()).isEqualTo(0.0);
    }

    @Test
    public void it_can_apply_fees() {
        Transaction apiTransaction = new Transaction();
        apiTransaction.setHeight(10);

        Input input1 = new Input();
        input1.setAmount(100.0);
        List<Input> inputs = Arrays.asList(input1, input1);

        Output output1 = new Output();
        output1.setAmount(5.0);
        List<Output> outputs = Arrays.asList(output1, output1);

        when(inputFactory.createInputs(apiTransaction)).thenReturn(inputs);
        when(outputFactory.createOutputs(apiTransaction)).thenReturn(outputs);

        BlockTransaction blockTransaction = blockTransactionFactory.createTransaction(apiTransaction);

        assertThat(blockTransaction.getFees()).isEqualTo(190.0);
        assertThat(blockTransaction.getType()).isEqualTo(BlockTransactionType.SPEND);
    }

    @Test
    public void it_can_apply_a_staking_type() {
        Transaction apiTransaction = new Transaction();
        apiTransaction.setHeight(10);

        Input input1 = new Input();
        input1.setAmount(5.0);
        List<Input> inputs = Arrays.asList(input1, input1);

        Output output1 = new Output();
        output1.setAddresses(Arrays.asList("Not community fund"));
        output1.setAmount(100.0);
        List<Output> outputs = Arrays.asList(output1, output1);

        when(inputFactory.createInputs(apiTransaction)).thenReturn(inputs);
        when(outputFactory.createOutputs(apiTransaction)).thenReturn(outputs);

        BlockTransaction blockTransaction = blockTransactionFactory.createTransaction(apiTransaction);

        assertThat(blockTransaction.getType()).isEqualTo(BlockTransactionType.STAKING);
        assertThat(blockTransaction.getStake()).isEqualTo(190.0);
    }
}
