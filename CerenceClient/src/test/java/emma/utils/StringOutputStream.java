package emma.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class StringOutputStream extends OutputStream {
    private final StringBuilder builder = new StringBuilder();
    @Override
    public void write(int i) throws IOException {
        builder.append(i);
    }

    @Override
    public void write(byte[] b) throws IOException {
        builder.append(Arrays.toString(b));
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        builder.append(Arrays.toString(b));
    }
}
