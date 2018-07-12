package com.navexplorer.indexer.zeromq;

import java.util.Formatter;

public class Converter {
    public static String bin2hex(byte[] bytes) {
        Formatter f = new Formatter();
        try {
            for (byte c : bytes) {
                f.format("%02X", c);
            }
            return f.toString().toLowerCase();
        } finally {
            f.close();
        }
    }
}
