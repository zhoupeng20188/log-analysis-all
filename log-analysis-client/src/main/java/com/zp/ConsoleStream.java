package com.zp;

import io.netty.util.CharsetUtil;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class ConsoleStream extends ByteArrayOutputStream {

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private PrintStream oldPrintStream;
    private PrintStream newPrintStream;

    public ConsoleStream(PrintStream oldPrintStream) {
        this.oldPrintStream = oldPrintStream;
        this.newPrintStream = new PrintStream(this);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) {
            throw new OutOfMemoryError();
        }
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }


    private void ensureCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - buf.length > 0) {
            grow(minCapacity);
        }
    }

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = buf.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }

        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            newCapacity = hugeCapacity(minCapacity);
        }

        buf = Arrays.copyOf(buf, newCapacity);
    }

    /**
     * Writes the specified byte to this byte array output stream.
     *
     * @param b the byte to be written.
     */
    @Override
    public synchronized void write(int b) {
        ensureCapacity(count + 1);
        buf[count] = (byte) b;
        count += 1;
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array
     * starting at offset <code>off</code> to this byte array output stream.
     *
     * @param b   the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     */
    @Override
    public synchronized void write(byte b[], int off, int len) {
        String s = new String(b, off, len, CharsetUtil.UTF_8);
        String replace = filterString(s);
        //切换回原输出流输出日志到真控制台
        System.setOut(oldPrintStream);
        System.out.print(s);
        LogStore.addContent(replace);
        System.setOut(newPrintStream);
        if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(count + len);
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    private String filterString(String s) {
        // 过滤掉[32m
        byte b1[] = {27,91,51,50,109};
        // 过滤掉[39m
        byte b2[] = {27,91,51,57,109};
        // 过滤掉[2m
        byte b3[] = {27,91,50,109};
        // 过滤掉[0;39m
        byte b4[] = {27,91,48,59,51,57,109};
        // 过滤掉[35m
        byte b5[] = {27,91,51,53,109};
        // 过滤掉[36m
        byte b6[] = {27,91,51,54,109};
        String s1 = new String(b1);
        String s2 = new String(b2);
        String s3 = new String(b3);
        String s4 = new String(b4);
        String s5 = new String(b5);
        String s6 = new String(b6);
        String replace = s.replace(s1, "")
                .replace(s2, "")
                .replace(s3, "")
                .replace(s4, "")
                .replace(s5, "")
                .replace(s6, "");
        return replace;
    }

}