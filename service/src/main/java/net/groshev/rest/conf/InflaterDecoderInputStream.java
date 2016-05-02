package net.groshev.rest.conf;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

/**
 * Created by kgroshev on 02.05.16.
 */
public class InflaterDecoderInputStream extends ServletInputStream {

    final private InputStream is;

    public InflaterDecoderInputStream(InputStream is) {
        this.is = new InflaterInputStream(is);
    }

    @Override
    public int readLine(byte[] b, int off, int len) throws IOException {
        return is.read(b, off, len);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }

    @Override
    public int available() throws IOException {
        return is.available();
    }

    @Override
    public void close() throws IOException {
        is.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        is.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return is.markSupported();
    }

    @Override
    public int read() throws IOException {
        return is.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return is.read(b, off, len);
    }

    @Override
    public int read(byte[] b) throws IOException {
        return is.read(b);
    }

    @Override
    public synchronized void reset() throws IOException {
        is.reset();
    }

    @Override
    public long skip(long n) throws IOException {
        return is.skip(n);
    }
}

