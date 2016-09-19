package io.muudo.metastore;

import com.lexicalscope.jewel.cli.Option;

public interface ServerOptions {
    @Option(shortName="p", longName="nodeport", defaultValue="8778")
    int getPort();

    @Option(shortName="tls", defaultValue="false")
    boolean useTLS();

    @Option(shortName="pemFile", defaultToNull=true)
    String pemFilePath();

    @Option(shortName="keyFile", defaultToNull=true)
    String keyFilePath();

    @Option(shortName="thread", defaultValue="10")
    int numThreads();

    @Option(shortName="wp", longName="webport", defaultValue="80")
    int getWebPort();
}
