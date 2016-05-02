package net.groshev.rest.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.groshev.rest.beans.PPAArrayOutBean;
import net.groshev.rest.beans.PPAMediaBean;
import net.groshev.rest.beans.PPAOutBean;
import net.groshev.rest.conf.ZlibFilter;
import net.groshev.rest.requests.PPAArrayRequestBean;
import net.groshev.rest.requests.PPARequestBean;
import net.groshev.rest.utils.compress.CompressionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

@RestController
public class PPARestController extends BaseRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PPARestController.class);
    @RequestMapping(value = "/fly-zget",
            method = RequestMethod.POST)
    @ResponseBody
    public Object getBean(HttpServletRequest request) throws IOException {
        String w = getRequestBody(request);
        PPAArrayRequestBean requestBeans = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            requestBeans = mapper.readValue(w.getBytes("UTF-8"), PPAArrayRequestBean.class);
        } catch (IOException e) {
            return response500(e.getMessage());
        }

        PPAArrayOutBean bean = new PPAArrayOutBean();
        bean.setArray(new ArrayList<>());

        // обрабатываем

        for (PPARequestBean requestBean : requestBeans.getArray()) {
            System.out.println("got bean: " + requestBean.toString());

            PPAMediaBean mediaBean = new PPAMediaBean();
            mediaBean.setFly_audio("43mn 17s | MPEG , 192 Kbps, 2 channels");
            mediaBean.setFly_audio_br(192);
            mediaBean.setFly_video("MPEG-4 , 1 816 Kbps, 16:9, 23.976 fps");
            mediaBean.setFly_xy("720x400");

            PPAOutBean outBean = new PPAOutBean();
            outBean.setMedia(mediaBean);
            outBean.setSize(requestBean.getSize());
            outBean.setTth(requestBean.getTth());
            bean.getArray().add(outBean);
        }
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
