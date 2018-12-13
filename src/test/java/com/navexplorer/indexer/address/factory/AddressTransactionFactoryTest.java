package com.navexplorer.indexer.address.factory;

import com.navexplorer.library.address.entity.AddressTransaction;
import com.navexplorer.library.address.entity.AddressTransactionType;
import com.navexplorer.library.block.entity.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
public class AddressTransactionFactoryTest {
    @InjectMocks
    private AddressTransactionFactory addressTransactionFactory;

    @Test
    public void it_will_return_null_when_an_empty_tx_is_created() {
        BlockTransaction blockTransaction = new BlockTransaction();
        blockTransaction.setType(BlockTransactionType.EMPTY);

        assertThat(addressTransactionFactory.create("Address", blockTransaction)).isNull();
    }

    @Test
    public void it_will_return_null_when_nothing_is_sent_or_received() {
        BlockTransaction blockTransaction = new BlockTransaction();
        blockTransaction.setType(BlockTransactionType.EMPTY);
        String address = "TEST ADDRESS";

        assertThat(addressTransactionFactory.create(address, blockTransaction)).isNull();
    }

    @Test
    public void it_can_create_a_staking_transaction() {
        BlockTransaction blockTransaction = new BlockTransaction();
        blockTransaction.setType(BlockTransactionType.STAKING);
        String address = "TEST ADDRESS";

        Output output = new Output();
        output.setAddresses(Arrays.asList(address));
        output.setAmount(100.0);
        output.setType(OutputType.PUBKEYHASH);
        blockTransaction.setOutputs(Arrays.asList(output));

        AddressTransaction transaction = addressTransactionFactory.create(address, blockTransaction);
        assertThat(transaction.getType()).isEqualTo(AddressTransactionType.STAKING);
    }

    @Test
    public void it_can_create_a_receiving_transaction() {
        BlockTransaction blockTransaction = new BlockTransaction();
        blockTransaction.setType(BlockTransactionType.SPEND);
        String address = "TEST ADDRESS";

        Input input = new Input();
        input.setAddresses(Arrays.asList(address));
        input.setAmount(50.0);
        blockTransaction.setInputs(Arrays.asList(input));

        Output output = new Output();
        output.setAddresses(Arrays.asList(address));
        output.setAmount(100.0);
        output.setType(OutputType.PUBKEYHASH);
        blockTransaction.setOutputs(Arrays.asList(output));

        AddressTransaction transaction = addressTransactionFactory.create(address, blockTransaction);
        assertThat(transaction.getType()).isEqualTo(AddressTransactionType.RECEIVE);
    }

    @Test
    public void it_can_create_a_sending_transaction() {
        BlockTransaction blockTransaction = new BlockTransaction();
        blockTransaction.setType(BlockTransactionType.SPEND);
        String address = "TEST ADDRESS";

        Input input = new Input();
        input.setAddresses(Arrays.asList(address));
        input.setAmount(100.0);
        blockTransaction.setInputs(Arrays.asList(input));

        Output output = new Output();
        output.setAddresses(Arrays.asList(address));
        output.setAmount(50.0);
        output.setType(OutputType.PUBKEYHASH);
        blockTransaction.setOutputs(Arrays.asList(output));

        AddressTransaction transaction = addressTransactionFactory.create(address, blockTransaction);
        assertThat(transaction.getType()).isEqualTo(AddressTransactionType.SEND);
    }

    @Test
    public void it_can_account_for_multiple_inputs() {
        BlockTransaction blockTransaction = new BlockTransaction();
        blockTransaction.setType(BlockTransactionType.SPEND);
        String address = "TEST ADDRESS";

        Input input1 = new Input();
        input1.setAddresses(Arrays.asList(address));
        input1.setAmount(100.0);

        Input input2 = new Input();
        input2.setAddresses(Arrays.asList(address));
        input2.setAmount(75.0);

        Input input3 = new Input();
        input3.setAddresses(Arrays.asList("OTHER ADDRESS"));
        input3.setAmount(25.0);

        Output output1 = new Output();
        output1.setAddresses(Arrays.asList(address));
        output1.setAmount(25.0);
        output1.setType(OutputType.PUBKEYHASH);

        blockTransaction.setInputs(Arrays.asList(input1, input2, input3));
        blockTransaction.setOutputs(Arrays.asList(output1));
        AddressTransaction transaction = addressTransactionFactory.create(address, blockTransaction);
        assertThat(transaction.getSent()).isEqualTo(input1.getAmount() + input2.getAmount());
    }

    @Test
    public void it_can_account_for_multiple_outputs() {
        BlockTransaction blockTransaction = new BlockTransaction();
        blockTransaction.setType(BlockTransactionType.SPEND);
        String address = "TEST ADDRESS";

        Output output1 = new Output();
        output1.setAddresses(Arrays.asList(address));
        output1.setAmount(100.0);
        output1.setType(OutputType.PUBKEYHASH);

        Output output2 = new Output();
        output2.setAddresses(Arrays.asList(address));
        output2.setAmount(75.0);
        output2.setType(OutputType.PUBKEYHASH);

        Output output3 = new Output();
        output3.setAddresses(Arrays.asList("OTHER ADDRESS"));
        output3.setAmount(25.0);
        output3.setType(OutputType.PUBKEYHASH);

        blockTransaction.setOutputs(Arrays.asList(output1, output2, output3));
        AddressTransaction transaction = addressTransactionFactory.create(address, blockTransaction);
        assertThat(transaction.getReceived()).isEqualTo(output1.getAmount() + output2.getAmount());
    }
}
