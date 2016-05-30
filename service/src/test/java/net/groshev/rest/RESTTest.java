package net.groshev.rest;

import com.jayway.restassured.response.Response;
import net.groshev.rest.utils.JSONUtils;
import net.groshev.rest.utils.compress.CompressionUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.DataFormatException;

import static com.jayway.restassured.RestAssured.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */
public class RESTTest {

    private static final Logger log = LoggerFactory.getLogger(RESTTest.class);

    private static double convertToMSecs(long nanos) {
        double coeff = 1000000.0;
        return (double) nanos / coeff;
    }

    /**
     * ${CLASS} can work.
     *
     * @throws Exception If fails
     */
    @Test
    @Ignore
    public void testRest() throws Exception {

        int maxSize = 100;

        List<Double> results = new ArrayList<>();
        // делаем POST-запрос
        baseURI = "http://192.168.10.11";
        port = 37015;
        log.info("testing server: {}:{}", baseURI, port);


        ExecutorService pool = Executors.newFixedThreadPool(50);
        List<Callable<Void>> callables = IntStream.rangeClosed(1, maxSize).boxed()
                .map(i -> {
                    String fileName = "flylinkdc-extjson-zlib-file-" + i + ".json.zlib";
                    return (Callable<Void>) () -> testOneRequest(results, fileName);
                })
                .collect(Collectors.toList());

        try {
            pool.invokeAll(callables)
                    .stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception ex) {
                            log.debug("ex:" + ex.getClass().getName() + " message:" + ex.getMessage());
                            throw new IllegalStateException(ex);
                        }
                    })
                    .forEach(e -> System.out.println("+"));
        } catch (InterruptedException ex) {
            log.debug("ex:" + ex.getClass().getName() + " message:" + ex.getMessage());
        } finally {
            pool.shutdown();
        }


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
            log.error("request skipped, not compressed");
            return null;
        }
        // проверяем на валидность
        assertTrue(JSONUtils.isJSONValid(new String(decompressedRequest)));

        // получаем ответ ()
        long start = System.nanoTime();
        Response validatableResponse =
                given()
                        .contentType("text/plain")
                        .body(request)
                        .when()
                        .post("/fly-zget");
        long end = System.nanoTime();
        byte[] response = validatableResponse.asByteArray();
        long duarationNs = end - start;
        results.add(convertToMSecs(duarationNs));
        log.info("[{}]duration ={} ms", validatableResponse.getStatusCode(), convertToMSecs(duarationNs));
        if (validatableResponse.getStatusCode() != 200) {
            log.info("status: {}, {}", validatableResponse.getStatusCode(), validatableResponse.getBody().prettyPrint());
        } else {
            // пробуем декомпрессировать ответ
            if (response.length > 0) {
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
            } else {
                log.info("response size = 0");
            }
        }
        return null;
    }
}
