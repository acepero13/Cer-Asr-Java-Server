package de.semvox.research.asr.ws.cerence.states;

import de.semvox.research.asr.ws.cerence.SenderContext;

public abstract class States implements SendState {
    protected final SenderContext context;

    public static SendState initial(SenderContext context) {
        return new InitState(context);
    }

    private States(SenderContext context) {
        this.context = context;
    }

    private static class InitState extends States {

        public InitState(SenderContext context) {
            super(context);
        }

        @Override
        public void onAudioChunk(byte[] data) {
            HeaderState st = new HeaderState(context);
            context.setState(st);
            st.onAudioChunk(data);
        }

        @Override
        public void onAudioFinished() {
            // Do nothing
        }
    }

    private static class HeaderState extends States {
        public HeaderState(SenderContext context) {
            super(context);
        }

        @Override
        public void onAudioChunk(byte[] data) {
            context.setState(new AudioState(context));
            context.sendHeader();
            context.sendAudio(data);
        }

        @Override
        public void onAudioFinished() {
            context.setState(new InitState(context));
        }


    }

    private static class AudioState extends States {
        public AudioState(SenderContext context) {
            super(context);
        }

        @Override
        public void onAudioChunk(byte[] data) {
            context.setState(new AudioState(context));
            context.sendAudio(data);
        }

        @Override
        public void onAudioFinished() {
            context.setState(new InitState(context));
        }
    }
}
