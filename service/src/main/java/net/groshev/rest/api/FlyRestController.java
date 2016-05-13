package net.groshev.rest.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.groshev.rest.beans.FlyArrayOutBean;
import net.groshev.rest.beans.FlyMediaBean;
import net.groshev.rest.beans.FlyOutBean;
import net.groshev.rest.requests.FlyArrayRequestBean;
import net.groshev.rest.requests.FlyRequestBean;
import net.groshev.rest.service.FlyDBService;
import net.groshev.rest.utils.compress.CompressionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

@RestController
public class FlyRestController extends BaseRestController {

    @Autowired
    private FlyDBService dbService;

    private static final Logger LOGGER = LoggerFactory.getLogger(FlyRestController.class);
    @RequestMapping(value = "/fly-zget",
            method = RequestMethod.POST)
    @ResponseBody
    public Object getBean(HttpServletRequest request) throws IOException {
        final long start = System.nanoTime();
        String w = getRequestBody(request);
        FlyArrayRequestBean requestBeans = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            requestBeans = mapper.readValue(w.getBytes("UTF-8"), FlyArrayRequestBean.class);
        } catch (IOException e) {
            return response500(e.getMessage());
        }

        // обрабатываем
        FlyArrayOutBean bean = dbService.processByKey(requestBeans);

        // криптуем выход
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer,bean);
        String json = writer.toString();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream out = new DeflaterOutputStream(baos, new Deflater(Deflater.BEST_COMPRESSION));
        InputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        try {
            CompressionUtils.shovelInToOut(in, out);
        } catch (IOException e) {
            return  response500(e.getMessage());
        }
        in.close();
        out.close();
        baos.close();
        final long end = System.nanoTime() - start;
        LOGGER.debug("--> rest result got in: " + end / 1000000.0 + "ms");
        return new ResponseEntity<byte[]>(baos.toByteArray(), HttpStatus.OK);
    }

    private String getRequestBody (final HttpServletRequest request){

        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = requestWrapper.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[1024];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) != -1) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
        } catch (IOException ex) {
            LOGGER.error("Error reading the request payload", ex);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException iox) {
                    // ignore
                }
            }
        }

        return stringBuilder.toString();
    }

}
