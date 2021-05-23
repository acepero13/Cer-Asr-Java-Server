package de.semvox.research.asr.ws.utils;

import emma.network.ConnectionListener;
import emma.network.SendableClient;

public class FakeClient implements SendableClient {
    public FakeClient(ConnectionListener l) {
    }

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void send(byte[] data) {

    }

    @Override
    public void send(short[] data) {

    }

    @Override
    public void send(String toSend) {

    }

    @Override
    public void flush() {

    }
}