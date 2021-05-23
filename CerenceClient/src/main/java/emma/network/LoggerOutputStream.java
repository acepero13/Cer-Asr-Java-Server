package emma.network;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LoggerOutputStream extends OutputStream {
    private final OutputStream out;
    private final FileOutputStream fos;

    public LoggerOutputStream(OutputStream outputStream) {
        FileOutputStream fos1;
        this.out = outputStream;
        try {
            fos1 = new FileOutputStream("/tmp/cerence2.bin");
        } catch (FileNotFoundException e) {
            fos1 = null;
        }
        this.fos = fos1;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        fos.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
        fos.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        fos.write(b, off, len);
    }
}
