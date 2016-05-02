package net.groshev.rest.conf;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * Created by kgroshev on 02.05.16.
 */
public class ZlibHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private byte[] rawData;
    private HttpServletRequest request;

    private static final Logger LOGGER = LoggerFactory.getLogger(ZlibHttpServletRequestWrapper.class);
    private ServletInputStream inputStream;


    public ZlibHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        LOGGER.trace("getInputStream() entry"); //$NON-NLS-1$
        LOGGER.trace("Wrapping ServletInputStream with Inflater"); //$NON-NLS-1$
/*        if (rawData == null) {
            rawData = IOUtils.toByteArray(this.request.getReader());
            this.inputStream = new InflaterDecoderInputStream(new ByteArrayInputStream(rawData));
        }*/
        this.inputStream = new InflaterDecoderInputStream(request.getInputStream());
        LOGGER.trace("getInputStream() exit - returning {}", this.inputStream); //$NON-NLS-1$
        return inputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (rawData == null) {
            rawData = IOUtils.toByteArray(this.request.getReader());
            this.inputStream = new InflaterDecoderInputStream(new ByteArrayInputStream(rawData));
        }
        Reader sourceReader = new InputStreamReader(inputStream);
        return new BufferedReader(sourceReader);
    }

    @Override
    public String getContentType() {
        return super.getContentType();
    }

}
