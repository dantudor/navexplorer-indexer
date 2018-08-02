package com.navexplorer.indexer.block.factory;

import com.navexplorer.library.block.entity.OutputType;
import com.navexplorer.library.block.entity.Output;
import org.navcoin.response.Transaction;
import org.navcoin.response.transaction.Vout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OutputFactory {
    public static List<Output> createOutputs(Transaction apiTransaction) {
        List<Output> outputs = new ArrayList<>();
        Arrays.stream(apiTransaction.getVout()).forEach(o -> outputs.add(createOutput(o)));

        return outputs;
    }

    private static Output createOutput(Vout vout) {
        Output output = new Output();
        output.setIndex(vout.getN().intValue());

        if (vout.getScriptPubKey() != null && vout.getScriptPubKey().getAddresses() != null) {
            output.setType(OutputType.fromValue(vout.getScriptPubKey().getType()));
            output.setAmount(vout.getValueSat());

            if (vout.getScriptPubKey().getType().equals("cfund_contribution")) {
                output.getAddresses().add("Community Fund");
            } else {
                output.setAddresses(vout.getScriptPubKey().getAddresses());
            }
        }

        return output;
    }
}
