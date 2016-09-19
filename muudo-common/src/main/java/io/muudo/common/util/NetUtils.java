package io.muudo.common.util;

import java.io.*;

public class NetUtils {
    public static File loadCert(String name) throws IOException {
        InputStream in = NetUtils.class.getResourceAsStream("/certs/" + name);
        File tmpFile = File.createTempFile(name, "");
        tmpFile.deleteOnExit();

        BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile));
        try {
            int b;
            while ((b = in.read()) != -1) {
                writer.write(b);
            }
        } finally {
            writer.close();
        }

        return tmpFile;
    }
}
