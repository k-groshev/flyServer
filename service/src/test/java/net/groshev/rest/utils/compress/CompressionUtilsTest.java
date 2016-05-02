package net.groshev.rest.utils.compress;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.groshev.rest.requests.PPAArrayRequestBean;
import net.groshev.rest.requests.PPARequestBean;
import net.groshev.rest.requests.PPARequestHeader;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;

/**
 * Created by kgroshev on 27.04.16.
 */
public class CompressionUtilsTest {
    @Test
    public void compress() throws Exception {
String test  = "{     \"array\": [       {             \"size\": \"9524718\",             \"tth\": \"M26LGEU33I43LGZ2RCUVUTSYOYR2OJDRK6TYA4A\"         }     ] }";
        byte[] arr = CompressionUtils.compress(test.getBytes("UTF-8"));
        System.out.println(new String(arr, 0, arr.length, "UTF-8"));

        System.out.println(CompressionUtils.decompress(arr));
    }

    @Test
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
    public void testResponse() throws Exception {
        PPAArrayRequestBean outBean = new PPAArrayRequestBean();
        outBean.setArray(new ArrayList<>());
        outBean.getArray().add(new PPARequestBean("1468506112","Q2CDTLWKUN5LEXMGBQCVYAX2YJCEY4NV3MYFB2I"));
        outBean.getArray().add(new PPARequestBean("1467570176","QJMFHAE6UMDH3MEW4BYJC4DPXFKVNDSKSJOFQQQ"));
        outBean.getArray().add(new PPARequestBean("2132082152","TQUB7Q5G3U3IA4ZS7C2MGWVYC3TCPWWBLT6LSYA"));
        outBean.getArray().add(new PPARequestBean("667374694","UCXORGVZMVN5JMNDXL2WQHXTZRAEWT47643LNRI"));
        PPARequestHeader header = new PPARequestHeader();
        header.setId("1462082043");
        outBean.setHeader(header);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer,outBean);
        String json = writer.toString();
        System.out.println(json);
        String compress = CompressionUtils.compress(json);
        System.out.println(compress);
        String  path = "/Users/kgroshev/java_ee/apps/rest-test/tests/";
        File file = new File(path + "dd.txt");
        OutputStream out = new DeflaterOutputStream(new FileOutputStream(file));
        InputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        CompressionUtils.shovelInToOut(in, out);
        in.close();
        out.close();

    }
}