package io.muudo.metastore;

import com.google.common.util.concurrent.MoreExecutors;
import com.lexicalscope.jewel.cli.CliFactory;
import io.grpc.Server;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.muudo.common.util.Utils;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MuudoServer {
    private static final Logger log = LoggerFactory.getLogger(MuudoServer.class);
    public static final int MAX_MESSAGE_SIZE = 16 * 1024 * 1024;

    private Server server;
    private ExecutorService executor;
    private final ServerOptions options;

    public static void main(String[] args) throws Exception {
        ServerOptions options = CliFactory.parseArguments(ServerOptions.class, args);
        final MuudoServer server = new MuudoServer(options);

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

    public MuudoServer(ServerOptions options) {
        this.options = options;

        Utils.validateBetween("Server Port", options.getPort(), 1, 65536);
        if (options.useTLS()) {
            log.info("Using tls");
            Utils.fileExists("Pem", options.pemFilePath());
            Utils.fileExists("Key", options.keyFilePath());
        }
    }

    public void start() throws Exception {
        executor = Executors.newFixedThreadPool(options.numThreads());

        SslContext sslContext = null;
        if (options.useTLS()) {
            sslContext = GrpcSslContexts.forServer(
                    new File(options.pemFilePath()), new File(options.keyFilePath())).build();
        }
        server = NettyServerBuilder.forPort(options.getPort())
                .sslContext(sslContext)
                .maxMessageSize(MAX_MESSAGE_SIZE)
                .addService(new GreeterImpl())
                .executor(executor)
                .build().start();
    }

    public void stop() throws Exception {
        server.shutdownNow();
        if (!server.awaitTermination(5, TimeUnit.SECONDS)) {
            System.err.println("Timed out waiting for server shutdown");
        }
        MoreExecutors.shutdownAndAwaitTermination(executor, 5, TimeUnit.SECONDS);
    }

    public int getPort() {
        return options.getPort();
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
