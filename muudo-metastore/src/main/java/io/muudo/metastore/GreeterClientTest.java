package io.muudo.metastore;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.muudo.GreeterGrpc;
import io.muudo.HelloReply;
import io.muudo.HelloRequest;
import io.muudo.common.util.Wrapper;
import io.muudo.grpc.util.BlockingStreamObserver;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.TimeUnit;

import static io.muudo.GreeterGrpc.GreeterBlockingStub;
import static io.muudo.GreeterGrpc.GreeterStub;

/**
 * Created by richardpark on 9/22/16.
 */
public class GreeterClientTest {

    private static final Logger log = LoggerFactory.getLogger(GreeterClientTest.class);
    private final ManagedChannel channel;
    private final GreeterBlockingStub syncStub;
    private final GreeterStub asyncStub;
    private String host;
    private int port;

    public GreeterClientTest(String host, int port) {
        this.host = host;
        this.port = port;
        this.channel = newPeerClientChannel();

        this.syncStub = GreeterGrpc.newBlockingStub(channel);
        this.asyncStub = GreeterGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public ManagedChannel newPeerClientChannel() {
        NettyChannelBuilder builder = NettyChannelBuilder.forAddress(host, port);
        builder.usePlaintext(true);
        return builder.build();
    }

    public String sayHello(String name) {
        log.info("Will try to greet {} ...", name);
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = syncStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            log.error("RPC failed", e);
            return "";
        }
        log.info("Greeting: " + response.getMessage());
        return response.getMessage();
    }

    public String sayHelloAsync(String name) throws Throwable {
        log.info("Will try to greet {} ...", name);
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();

        BlockingStreamObserver<HelloReply> response = new BlockingStreamObserver<HelloReply>();
        try {
            asyncStub.sayHello(request, response);
        } catch (StatusRuntimeException e) {
            log.warn("RPC failed: {}", e.getStatus());
            return "";
        }
        response.waitForComplete(5, TimeUnit.SECONDS);
        if (response.isError()) {
            throw response.getError();
        }
        if (response.isCompleted()) {
            log.info("Says its done");
        }
        return response.getValue().getMessage();
    }

    public static void main(String[] args) throws Throwable {
        GreeterClientTest test = new GreeterClientTest("localhost", 14900);

        String response = test.sayHello("richard");
        log.info("Reponse {}", response);
        String testAsync = test.sayHelloAsync("richard");
        log.info("Async Reponse {}", testAsync);
    }
}
