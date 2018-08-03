package com.navexplorer.indexer.block.factory;

import com.navexplorer.library.block.entity.Output;
import com.navexplorer.library.block.entity.OutputType;
import org.junit.Test;
import org.navcoin.response.Transaction;
import org.navcoin.response.transaction.ScriptPubKey;
import org.navcoin.response.transaction.Vout;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class OutputFactoryTest {
    @Test
    public void it_can_create_outputs() {
        Transaction apiTransaction = new Transaction();

        Vout vout1 = new Vout();
        vout1.setN(0L);
        vout1.setScriptPubKey(new ScriptPubKey());
        vout1.getScriptPubKey().setAddresses(Arrays.asList("ADDRESS 1"));
        vout1.getScriptPubKey().setType("pubkey");
        vout1.setValueSat(1000000.0);

        Vout vout2 = new Vout();
        vout2.setN(1L);
        vout2.setScriptPubKey(new ScriptPubKey());
        vout2.getScriptPubKey().setAddresses(Arrays.asList("ADDRESS 2", "ADDRESS 3"));
        vout2.getScriptPubKey().setType("pubkeyhash");
        vout2.setValueSat(50000.0);

        Vout vout3 = new Vout();
        vout3.setN(2L);
        vout3.setScriptPubKey(new ScriptPubKey());
        vout3.getScriptPubKey().setAddresses(Arrays.asList());
        vout3.getScriptPubKey().setType("cfund_contribution");
        vout3.setValueSat(0.25);

        apiTransaction.setVout((Vout[]) Arrays.asList(vout1, vout2, vout3).toArray());

        List<Output> outputs = new OutputFactory().createOutputs(apiTransaction);

        assertThat(outputs.get(0).getIndex()).isEqualTo(vout1.getN().intValue());
        assertThat(outputs.get(0).getType()).isEqualTo(OutputType.fromValue(vout1.getScriptPubKey().getType()));
        assertThat(outputs.get(0).getAmount()).isEqualTo(vout1.getValueSat());
        assertThat(outputs.get(0).getAddresses()).isEqualTo(vout1.getScriptPubKey().getAddresses());

        assertThat(outputs.get(1).getIndex()).isEqualTo(vout2.getN().intValue());
        assertThat(outputs.get(1).getType()).isEqualTo(OutputType.fromValue(vout2.getScriptPubKey().getType()));
        assertThat(outputs.get(1).getAmount()).isEqualTo(vout2.getValueSat());
        assertThat(outputs.get(1).getAddresses()).isEqualTo(vout2.getScriptPubKey().getAddresses());

        assertThat(outputs.get(2).getIndex()).isEqualTo(vout3.getN().intValue());
        assertThat(outputs.get(2).getType()).isEqualTo(OutputType.fromValue(vout3.getScriptPubKey().getType()));
        assertThat(outputs.get(2).getAmount()).isEqualTo(vout3.getValueSat());
        assertThat(outputs.get(2).getAddresses()).isEqualTo(Arrays.asList("Community Fund"));
    }
}
