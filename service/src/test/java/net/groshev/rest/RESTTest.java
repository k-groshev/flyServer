package net.groshev.rest;

import static com.jayway.restassured.RestAssured.baseURI;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.port;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.zip.DataFormatException;
import net.groshev.rest.utils.JSONUtils;
import net.groshev.rest.utils.compress.CompressionUtils;
import org.apache.commons.io.IOUtils;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */
public class RESTTest {

    private static final Logger log = LoggerFactory.getLogger(RESTTest.class);

    /**
     * ${CLASS} can work.
     * @throws Exception If fails
     */
    @Test
    @Ignore
    public void testRest() throws Exception {

        int maxSize = 100;

        List<Double> results = new ArrayList<>();
        // делаем POST-запрос
        baseURI = "http://127.0.0.1";
        port = 37015;
        log.info("testing server: {}:{}", baseURI, port);

        ExecutorService pool = Executors.newFixedThreadPool(50);
        IntStream.rangeClosed(1, maxSize).boxed().map(i ->
            CompletableFuture.supplyAsync(() -> {
                String fileName = "flylinkdc-extjson-zlib-file-" + i + ".json.zlib";
                testOneRequest(results, fileName);
                return null;
            }, pool)
        ).forEach(future ->{
            try {
                future.get();
            } catch (Exception ex) {
                fail("ex: "+ ex.getClass().getName() + " message:" + ex.getMessage());
            }
        });
        double average = results.stream().mapToDouble(Double::valueOf).average()
            .getAsDouble();
        log.info("avg request ({}) time: {} ms", results.size(), average);

    }

    private Void testOneRequest(final List<Double> results, final String fileName) {
        // получаем компрессированный json для теста
        byte[] request = new byte[0];
        try {
            request = IOUtils.toByteArray(
                this.getClass().getResourceAsStream(fileName));
        } catch (Exception e) {
            fail("file open error");
        }
        // декомпрессируем
        byte[] decompressedRequest = new byte[0];
        try {
            decompressedRequest = CompressionUtils.decompress(request);
        } catch (IOException e) {
            fail("decompression error");
        } catch (DataFormatException e) {
            log.error("decompression error on DataFormatException");
            return null;
        }
        // проверяем на валидность
        assertTrue(JSONUtils.isJSONValid(new String(decompressedRequest)));

        // получаем ответ ()
        long start = System.nanoTime();
        byte[] response =
            given()
                .contentType("text/plain")
                .body(request)
                .when()
                .post("/fly-zget")
                .asByteArray();
        long end = System.nanoTime();
        long duarationNs = end - start;
        results.add(convertToMSecs(duarationNs));
        log.info("duration ={} ms", convertToMSecs(duarationNs));
        // пробуем декомпрессировать ответ
        if (response.length >0) {
            byte[] decompressedResponse = new byte[0];
            try {
                decompressedResponse = CompressionUtils.decompress(response);
            } catch (IOException e) {
                fail("response decompression error");
            } catch (DataFormatException e) {
                fail("response decompression error on DataFormatException");
            }
            // проверяем валидность полученного ответа json
            assertTrue(JSONUtils.isJSONValid(new String(decompressedResponse)));
        }else{
            log.info("response size = 0");
        }
        return null;
    }

    private static double convertToMSecs(long nanos) {
        double coeff = 1000000.0;
        return (double) nanos / coeff;
    }
}
