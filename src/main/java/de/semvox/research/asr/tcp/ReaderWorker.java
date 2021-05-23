package de.semvox.research.asr.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

final class ReaderWorker implements Worker {
    private static final int BUFFER_SIZE = 1280; // TODO: See how to handle this with audio
    private final Consumer<byte[]> consumer;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final InputStream is;

    ReaderWorker(Consumer<byte[]> consumer, InputStream is) {
        this.consumer = consumer;
        this.is = is;
    }


    @Override
    public void stopWorker() {
        isRunning.set(false);
    }

    @Override
    public void run() {
        isRunning.set(true);

        while (isRunning.get()) {
            if (!readFromStream()) {
                stopWorker();
            }
        }
    }

    private boolean readFromStream() {
        try {
            byte[] buff = new byte[BUFFER_SIZE];
            boolean read = is.read(buff) > 0;
            consumer.accept(buff);
            return read;
        } catch (IOException e) {
            return false;
        }
    }
}
