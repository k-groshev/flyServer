package net.groshev.rest.utils.compress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.*;

public class CompressionUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CompressionUtils.class);

    public static byte[] compress(byte[] data) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        LOG.debug("Original: " + data.length / 1024 + " Kb");
        LOG.debug("Compressed: " + output.length / 1024 + " Kb");
        return output;
    }

    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        LOG.debug("Original: " + data.length);
        LOG.debug("Compressed: " + output.length);
        return output;
    }


    /**
     * Compresses a file with zlib compression.
     */
    public static void compressFile(File raw, File compressed)
            throws IOException
    {
        InputStream in = new FileInputStream(raw);
        OutputStream out =
                new DeflaterOutputStream(new FileOutputStream(compressed));
        shovelInToOut(in, out);
        in.close();
        out.close();
    }

    /**
     * Decompresses a zlib compressed file.
     */
    public static void decompressFile(File compressed, File raw)
            throws IOException
    {
        InputStream in =
                new InflaterInputStream(new FileInputStream(compressed));
        OutputStream out = new FileOutputStream(raw);
        shovelInToOut(in, out);
        in.close();
        out.close();
    }

    /**
     * Shovels all data from an input stream to an output stream.
     */
    public static void shovelInToOut(InputStream in, OutputStream out)
            throws IOException
    {
        byte[] buffer = new byte[1000];
        int len;
        while((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
    }

    public static String compress(String s) {
        Deflater def = new Deflater(Deflater.BEST_COMPRESSION);
        byte[] sbytes = s.getBytes(StandardCharsets.UTF_8);
        def.setInput(sbytes);
        def.finish();
        byte[] buffer = new byte[sbytes.length];
        int n = def.deflate(buffer);
        return new String(buffer, 0, n, StandardCharsets.ISO_8859_1)
                + "*" + sbytes.length;
    }

    public static String decompress(String s) {
        int pos = s.lastIndexOf('*');
        int len = Integer.parseInt(s.substring(pos + 1));
        s = s.substring(0, pos);

        Inflater inf = new Inflater();
        byte[] buffer = s.getBytes(StandardCharsets.ISO_8859_1);
        byte[] decomp = new byte[len];
        inf.setInput(buffer);
        try {
            inf.inflate(decomp, 0, len);
            inf.end();
        } catch (DataFormatException e) {
            throw new IllegalArgumentException(e);
        }
        return new String(decomp, StandardCharsets.UTF_8);
    }
}