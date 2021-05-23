package de.semvox.research.asr.tcp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

final class WriterWorker implements Worker, Consumer<byte[]> {
    private final BlockingQueue<byte[]> queue;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final SendableByte sender;

    WriterWorker(SendableByte sender) {
        this.queue = new LinkedBlockingQueue<>();
        this.sender = sender;
    }

    @Override
    public void run() {
        isRunning.set(true);
        while (isRunning.get()) {
            tryToSend();
        }
    }

    private void tryToSend() {
        try {
            byte[] data = queue.take();
            sender.send(data);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    @Override
    public synchronized void stopWorker() {
        isRunning.set(false);
    }

    @Override
    public void accept(byte[] data) {
        if (!queue.offer(data)) {
            stopWorker();
        }
    }
}
