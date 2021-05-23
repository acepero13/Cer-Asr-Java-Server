package de.semvox.research.asr.ws.cerence.states;

public interface SendState {
    void onAudioChunk(byte[] data);
    void onAudioFinished();
}
