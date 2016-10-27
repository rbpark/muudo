package io.muudo.metastore.configuration;

import com.lexicalscope.jewel.cli.Option;

public interface CommandLineOptions {
    @Option(shortName="c", longName = "conf", defaultValue="conf/server.yaml")
    String getConfigFile();
}
