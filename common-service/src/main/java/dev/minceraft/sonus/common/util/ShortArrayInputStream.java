package dev.minceraft.sonus.common.util;
// Created by booky10 in Sonus (01:28 17.11.2025)

import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

@NullMarked
public final class ShortArrayInputStream extends InputStream {

    private final short[] buf;
    private final int length;
    private int pos;
    private int mark;

    public ShortArrayInputStream(short[] buf) {
        this.buf = buf;
        this.length = buf.length * 2;
    }

    public ShortArrayInputStream(short[] buf, int offset, int length) {
        this.buf = buf;
        this.pos = offset * 2;
        this.length = Math.min(offset + length, buf.length) * 2;
        this.mark = offset * 2;
    }

    @Override
    public synchronized int read() {
        if (this.pos >= this.length) {
            return -1;
        }
        short s = this.buf[this.pos >> 1];
        // read the least byte first
        return (this.pos++ & 1) == 0 ? (s & 0xFF) : ((s >> 8) & 0xFF);
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) {
        Objects.checkFromIndexSize(off, len, b.length);
        if (this.pos >= this.length) {
            return -1;
        }
        int avail = this.length - this.pos;
        if (len > avail) {
            len = avail;
        }
        if (len <= 0) {
            return 0;
        }
        int i = 0;
        // set odd byte
        if ((this.pos & 1) != 0) {
            short s = this.buf[this.pos++ >> 1];
            b[off + i++] = (byte) ((s >> 8) & 0xFF);
        }
        // copy remaining shorts
        while (i < len) {
            short s = this.buf[this.pos >> 1];
            b[off + i++] = (byte) (s & 0xFF);
            if (i < len) {
                b[off + i++] = (byte) ((s >> 8) & 0xFF);
                this.pos += 2;
            } else {
                this.pos++;
            }
        }
        return i;
    }

    @Override
    public synchronized byte[] readAllBytes() {
        byte[] result = new byte[this.length - this.pos];
        int i = 0;
        // set odd byte
        if ((this.pos & 1) != 0) {
            short s = this.buf[this.pos++ >> 1];
            result[i++] = (byte) ((s >> 8) & 0xFF);
        }
        // copy remaining shorts
        while (this.pos < this.length) {
            short s = this.buf[this.pos >> 1];
            result[i++] = (byte) (s & 0xFF);
            result[i++] = (byte) ((s >> 8) & 0xFF);
            this.pos += 2;
        }
        return result;
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) {
        int n = this.read(b, off, len);
        return n == -1 ? 0 : n;
    }

    @Override
    public synchronized long transferTo(OutputStream out) throws IOException {
        int len = this.length - this.pos;
        if (len > 0) {
            // write odd byte
            int nwritten = 0;
            if ((this.pos & 1) != 0) {
                short s = this.buf[this.pos++ >> 1];
                out.write((s >> 8) & 0xFF);
                nwritten++;
            }
            byte[] arr = new byte[2];
            while (nwritten < len) {
                short s = this.buf[this.pos >> 1];
                arr[0] = (byte) (s & 0xFF);
                arr[1] = (byte) ((s >> 8) & 0xFF);

                out.write(arr);
                this.pos += 2;
                nwritten += 2;
            }
            assert this.pos == this.length;
        }
        return len;
    }

    @Override
    public synchronized long skip(long n) {
        long skipped = this.length - this.pos;
        if (n < skipped) {
            skipped = n < 0 ? 0 : n;
        }
        this.pos += (int) skipped;
        return skipped;
    }

    @Override
    public synchronized int available() {
        return this.length - this.pos;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readlimit) {
        this.mark = this.pos;
    }

    @Override
    public synchronized void reset() {
        this.pos = this.mark;
    }

    @Override
    public void close() {
    }
}
