package io.muudo.metastore.persistence;

import io.muudo.common.io.EncodingUtils;
import io.muudo.common.util.Except;
import io.muudo.common.util.Utils;
import io.muudo.metastore.proto.OperationProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class CommitLogger {
    private static final byte[] MAGIC_BYTES = {0, 77, 85, 85};
    private static final byte[] VERSION = {0, 0};
    private static final Logger log = LoggerFactory.getLogger(CommitLogger.class);
    private static final String LOG_FILE_SUFFIX = ".log";

    private BlockingQueue<Future<Operation>> operationQueue;
    private final Path directory;
    private final String name;
    private int opQueueSize = 100;
    private long maxLogSize;
    private Path currentLog;
    private AtomicLong txnNum = new AtomicLong();

    private CommitLogger(Path directory, String name, long maxLogSize) {
        this.directory = directory;
        this.name = name;
        this.maxLogSize = maxLogSize;
    }

    public Path getDirectory() {
        return directory;
    }

    protected synchronized void commitToLog(Operation operation) throws IOException {
        OperationProto opProto = toProtobuff(operation);

        byte[] opArray = opProto.toByteArray();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        buffer.write(MAGIC_BYTES);
        buffer.write(VERSION);
        buffer.write(EncodingUtils.encodeIntegerToByteArray(opArray.length));
        buffer.write(opArray);

        Files.write(currentLog, buffer.toByteArray(), StandardOpenOption.APPEND);
    }

    /*package*/ OperationProto toProtobuff(Operation operation) throws IOException {
        // Create the Protobuff version of the operation
        OperationProto.Builder protoBuilder = OperationProto.newBuilder();
        if (operation instanceof AddOperation) {
            AddOperation addOp = (AddOperation)operation;
            protoBuilder.setType(OperationProto.Type.ADD);
        }
        else if (operation instanceof ModifyOperation) {
            ModifyOperation modifyOp = (ModifyOperation)operation;
            protoBuilder.setType(OperationProto.Type.MODIFY);
        }
        else if (operation instanceof MoveOperation) {
            MoveOperation moveOp = (MoveOperation)operation;
            protoBuilder.setType(OperationProto.Type.MOVE);
        }
        else if (operation instanceof ReplaceOperation) {
            ReplaceOperation replaceOp = (ReplaceOperation)operation;
            protoBuilder.setType(OperationProto.Type.REPLACE);
        }
        else if (operation instanceof DeleteOperation) {
            DeleteOperation deleteOp = (DeleteOperation)operation;
            protoBuilder.setType(OperationProto.Type.DELETE);
        }
        else {
            throw CommitLoggerException.of("Operation of type %s not recognized.", operation.getClass().getName());
        }

        return protoBuilder.build();
    }

    public void bootstrap() throws IOException {
        // Create directory if it doesn't exist. Or throw an exception.
        log.info("Bootstrapping commit log {} from dir {}", name, directory);

        if (!Files.exists(directory)) {
            log.info("Bootstrap log {} directory {} does not exist. Will attempt to create.", name, directory);
            Files.createDirectories(directory);
        }

        // Validate that this is a directory, not a file.
        if (!Files.isDirectory(directory)) {
            throw Except.newIllegalArgument("Commit log %s path %s is not a directory", directory, name);
        }

        // Find latest log file.
        // Log format will be <name>.<txnnum>.log
        List<Path> logFiles = Files.find(directory,
                1,
                (p, a) -> {
                    String path = p.getFileName().toString();
                    return path.startsWith(name) && path.endsWith(LOG_FILE_SUFFIX);
                })
                .sorted()
                .collect(Collectors.toList());

        if (logFiles.isEmpty()) {
            log.info("No log files found in {}", directory.getFileName());
            currentLog = createLogFile(0);
        }
        else {
            // Get last log as current path.
            currentLog = logFiles.get(logFiles.size() - 1);
        }
    }

    private Path createLogFile(int txnNum) throws IOException {
        Path path = directory.resolve(String.format("%s-%d.log", name, txnNum));

        log.info("Creating new log file {}", path);
        return Files.createFile(path);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String directory;
        private String name;
        private long maxLogFileSize = 1024 * 1024 * 100;

        public Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder directory(String directory) {
            this.directory = directory;
            return this;
        }

        public Builder maxLogFileSize(long byteLength) {
            maxLogFileSize = byteLength;
            return this;
        }

        public CommitLogger build() {
            Utils.validateNotNull("CommitLog name", name);
            Utils.validateNotNull("CommitLog directory", directory);

            Path logDirectory = FileSystems.getDefault().getPath(directory);
            CommitLogger commitLogger = new CommitLogger(logDirectory, name, maxLogFileSize);
            return commitLogger;
        }
    }
}
