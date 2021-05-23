package de.semvox.research.asr.ws.cerence;

public interface CerenceClient {
    boolean sendHeader();

    boolean sendAudio(byte[] audio);

    boolean sendEnd();


}
