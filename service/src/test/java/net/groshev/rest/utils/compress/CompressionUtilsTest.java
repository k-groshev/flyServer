package net.groshev.rest.utils.compress;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.groshev.rest.requests.FlyArrayRequestBean;
import net.groshev.rest.requests.FlyRequestBean;
import net.groshev.rest.requests.FlyRequestHeader;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kgroshev on 27.04.16.
 */
public class CompressionUtilsTest {
    public static final Logger LOGGER = LoggerFactory.getLogger(CompressionUtilsTest.class);
    
    @Test
    public void compress() throws Exception {
String test  = "{     \"array\": [       {             \"size\": \"9524718\",             \"tth\": \"M26LGEU33I43LGZ2RCUVUTSYOYR2OJDRK6TYA4A\"         }     ] }";
        byte[] arr = CompressionUtils.compress(test.getBytes("UTF-8"));
        LOGGER.debug(new String(arr, 0, arr.length, "UTF-8"));

        LOGGER.debug(new String(CompressionUtils.decompress(arr)));
    }

    @Test
    @Ignore
    public void testZlib() throws Exception {
        String  path = "/Users/kgroshev/java_ee/apps/rest-test/tests/";
        File compressed = new File(path+"flylinkdc-extjson-zlib-file-9.json.zlib");
        CompressionUtils.decompressFile(compressed, new File(path+"decompressed.txt"));

    }
/*    @Test
    @Ignore
    public void testZlib2() throws Exception {
        String  path = "/Users/kgroshev/java_ee/apps/rest-test/tests/";
        File compressed = new File(path+"fly-zget");
        CompressionUtils.decompressFile(compressed, new File(path+"decompressed.txt"));
    }*/


    @Test
    @Ignore
    public void testResponse() throws Exception {
        FlyArrayRequestBean outBean = new FlyArrayRequestBean();
        outBean.setArray(new ArrayList<>());
        outBean.getArray().add(new FlyRequestBean("Q2CDTLWKUN5LEXMGBQCVYAX2YJCEY4NV3MYFB2I", 1468506112L));
        outBean.getArray().add(new FlyRequestBean("QJMFHAE6UMDH3MEW4BYJC4DPXFKVNDSKSJOFQQQ",1467570176L));
        outBean.getArray().add(new FlyRequestBean("TQUB7Q5G3U3IA4ZS7C2MGWVYC3TCPWWBLT6LSYA", 2132082152L));
        outBean.getArray().add(new FlyRequestBean("UCXORGVZMVN5JMNDXL2WQHXTZRAEWT47643LNRI",667374694L));
        FlyRequestHeader header = new FlyRequestHeader();
        header.setId("1462082043");
        outBean.setHeader(header);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer,outBean);
        String json = writer.toString();
        LOGGER.debug(json);
        String compress = CompressionUtils.compress(json);
        LOGGER.debug(compress);
        String  path = "/Users/kgroshev/java_ee/apps/rest-test/tests/";
        File file = new File(path + "dd.txt");
        OutputStream out = new DeflaterOutputStream(new FileOutputStream(file));
        InputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        CompressionUtils.shovelInToOut(in, out);
        in.close();
        out.close();

    }
}