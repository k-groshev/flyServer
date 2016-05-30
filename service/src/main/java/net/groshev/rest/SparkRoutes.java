package net.groshev.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.groshev.rest.beans.FlyArrayOutBean;
import net.groshev.rest.domain.repository.FlyRepository;
import net.groshev.rest.domain.repository.impl.FlyCassandraRepository;
import net.groshev.rest.requests.FlyArrayRequestBean;
import net.groshev.rest.service.FlyDBService;
import net.groshev.rest.service.impl.FlyDBServiceImpl;
import net.groshev.rest.utils.compress.CompressionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import static spark.Spark.halt;

/**
 * Created by kgroshev on 24.05.16.
 */
public class SparkRoutes {
    public static final Logger LOGGER = LoggerFactory.getLogger(SparkRoutes.class);
    public static final double coeff = 1000000.0;

    public static void main(String[] args) {
        final ObjectMapper mapper = new ObjectMapper();
        final FlyRepository repository = new FlyCassandraRepository();

        final FlyDBService dbService = new FlyDBServiceImpl();
        dbService.setRepository(repository);

        Spark.port(37015);
        Spark.post("/fly-zget", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                long gStart = System.nanoTime();
                byte[] w = CompressionUtils.decompress(request.bodyAsBytes());
                long gDecompress = System.nanoTime();
                double msDecompress = (gDecompress-gStart)/ coeff;
                FlyArrayRequestBean requestBeans = null;
                try {
                    requestBeans = mapper.readValue(w, FlyArrayRequestBean.class);
                } catch (IOException e) {
                    halt(500, e.getMessage());
                }
                long count = requestBeans.getArray().size();
                long gMapping = System.nanoTime();
                double msMapping = (gMapping - gDecompress)/ coeff;

                // обрабатываем
                FlyArrayOutBean bean = dbService.processByKey(requestBeans);
                long gProcess = System.nanoTime();
                double msProcess = (gProcess - gMapping)/ coeff;


                // криптуем выход
                StringWriter writer = new StringWriter();
                mapper.writeValue(writer, bean);
                String json = writer.toString();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                OutputStream out = new DeflaterOutputStream(baos, new Deflater(Deflater.BEST_COMPRESSION));
                InputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
                try {
                    CompressionUtils.shovelInToOut(in, out);
                } catch (IOException e) {
                    halt(500, e.getMessage());
                }
                in.close();
                out.close();
                baos.close();
                long gEnd = System.nanoTime();
                double msCrypt = (gEnd - gProcess)/ coeff;
                double msAll = (gEnd - gStart)/ coeff;
                //  final long end = System.nanoTime() - start;
                LOGGER.debug("--> {} got in {}/{}/{}/{} = {} ms",
                        count,
                        msDecompress,
                        msMapping,
                        msProcess,
                        msCrypt,
                        msAll);
                return baos.toByteArray();
            }
        });
    }
}
