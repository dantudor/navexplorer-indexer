package com.navexplorer.indexer.address.indexer;

import com.navexplorer.library.block.entity.BlockTransaction;
import com.navexplorer.library.block.entity.Input;
import com.navexplorer.library.block.entity.Output;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

public class AddressExtractorTest {
    @Test
    public void it_can_extract_all_inputs_from_a_transaction() {
        BlockTransaction transaction = new BlockTransaction();

        Input input1 = new Input();
        input1.setAddresses(Arrays.asList("ADDRESS_1", "ADDRESS_2"));

        Input input2 = new Input();
        input2.setAddresses(Arrays.asList("ADDRESS_3"));

        transaction.setInputs(Arrays.asList(input1, input1, input2));

        AddressExtractor extractor = new AddressExtractor();
        Set<String> addresses = extractor.getAllAddressesFromBlockTransaction(transaction);

        assertThat(addresses.size()).isEqualTo(3);
        assertThat(addresses.contains("ADDRESS_1")).isTrue();
        assertThat(addresses.contains("ADDRESS_2")).isTrue();
        assertThat(addresses.contains("ADDRESS_3")).isTrue();
    }

    @Test
    public void it_can_extract_all_outputs_from_a_transaction() {
        BlockTransaction transaction = new BlockTransaction();

        Output output1 = new Output();
        output1.setAddresses(Arrays.asList("ADDRESS_1", "ADDRESS_2"));

        Output output2 = new Output();
        output2.setAddresses(Arrays.asList("ADDRESS_3"));

        transaction.setOutputs(Arrays.asList(output1, output1, output2));

        AddressExtractor extractor = new AddressExtractor();
        Set<String> addresses = extractor.getAllAddressesFromBlockTransaction(transaction);

        assertThat(addresses.size()).isEqualTo(3);
        assertThat(addresses.contains("ADDRESS_1")).isTrue();
        assertThat(addresses.contains("ADDRESS_2")).isTrue();
        assertThat(addresses.contains("ADDRESS_3")).isTrue();
    }
}
