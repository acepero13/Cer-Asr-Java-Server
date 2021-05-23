package de.semvox.research.asr.ws.cerence;


import de.semvox.research.asr.tcp.SendableByte;
import de.semvox.research.asr.ws.cerence.states.SendState;
import de.semvox.research.asr.ws.cerence.states.States;
import emma.config.ServerConfiguration;
import emma.io.websocket.protocol.*;
import emma.network.SendableClient;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SenderContext implements CerenceClient, SendableByte {
    private final SendableClient client;
    private final ServerConfiguration config;
    private SendState state = States.initial(this);
    private final Lock lock = new ReentrantLock() {
    };

    public SenderContext(SendableClient client, ServerConfiguration configuration) {
        this.client = client;
        this.config = configuration;
    }

    public void send(String data) {
        state.onAudioFinished();
    }

    public void send(byte[] data) {
        lock.lock();
        state.onAudioChunk(data);
        lock.unlock();
    }

    public void setState(SendState state) {
        lock.lock();
        this.state = state;
        lock.unlock();
    }

    @Override
    public boolean sendHeader() {
        //client.connect();
        new Header(client, config).write();
        new Data(client, config).write();
        new Info(client, config).write();
        return true;
    }

    @Override
    public boolean sendAudio(byte[] audio) {
        new Audio(client, config, audio).write();
        return true;
    }

    @Override
    public boolean sendEnd() {
        new End(client).write();
        return true;
    }
}
