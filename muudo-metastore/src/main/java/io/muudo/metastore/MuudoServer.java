package io.muudo.metastore;

import com.google.common.util.concurrent.MoreExecutors;
import com.lexicalscope.jewel.cli.CliFactory;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.muudo.common.config.Configuration;
import io.muudo.common.util.Utils;
import io.muudo.metastore.configuration.CommandLineOptions;
import io.muudo.metastore.configuration.MuudoServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MuudoServer {
    private static final Logger log = LoggerFactory.getLogger(MuudoServer.class);
    public static final int MAX_MESSAGE_SIZE = 16 * 1024 * 1024;

    private Configuration configuration;
    private MuudoServerConfig serverConfig;
    private Server server;
    private ExecutorService executor;

    public static void main(String[] args) throws Exception {
        CommandLineOptions options = CliFactory.parseArguments(CommandLineOptions.class, args);
        Utils.validateFileExists("ServerConfig", options.getConfigFile());
        Configuration conf = Configuration.loadFromYamlFile(new File(options.getConfigFile()));

        final MuudoServer server = new MuudoServer(conf);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("Shutting down");
                    server.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        server.start();
        log.info("Server started on port {}", server.getPort());
        server.blockUntilShutdown();
    }

    public MuudoServer(Configuration configuration) {
        this.configuration = configuration;
        this.serverConfig = configuration.as(MuudoServerConfig.class);

        Utils.validateBetween("Server Port", serverConfig.getPort(), 1, 65536);
    }

    public void start() throws Exception {
        executor = Executors.newFixedThreadPool(serverConfig.getNumThreads());

        server = ServerBuilder.forPort(serverConfig.getPort())
                .addService(new GreeterImpl())
                .executor(executor)
                .build()
                .start();
    }

    public void stop() throws Exception {
        log.info("Stopping the server");
        if (server != null) {
            server.shutdownNow();
            if (!server.awaitTermination(5, TimeUnit.SECONDS)) {
                log.error("Timed out waiting for server shutdown");
            }
            MoreExecutors.shutdownAndAwaitTermination(executor, 5, TimeUnit.SECONDS);
        }
    }

    public int getPort() {
        return serverConfig.getPort();
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
