package net.groshev.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.ArrayList;
import net.groshev.rest.beans.ErrorBean;
import net.groshev.rest.beans.HibernateStatBean;
import net.groshev.rest.beans.PPAArrayOutBean;
import net.groshev.rest.beans.PPAMediaBean;
import net.groshev.rest.beans.PPAOutBean;
import net.groshev.rest.requests.PPAArrayRequestBean;
import net.groshev.rest.requests.PPARequestBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/ppa")
@Api(value = "/ppa", description = "PPA test rest controller")
public class PPARestController extends BaseRestController {


    @RequestMapping(value = "/test",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(
        value = "Get media record", notes = "Returns bean with media records",
        response = PPAOutBean.class
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = HibernateStatBean.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = ErrorBean.class),
        @ApiResponse(code = 404, message = "Not Found", response = ErrorBean.class)}
    )
    @ResponseBody
    public Object getBean(@RequestBody final PPAArrayRequestBean requestBeans) {

        PPAArrayOutBean bean = new PPAArrayOutBean();
        bean.setArray(new ArrayList<>());

        for (PPARequestBean requestBean : requestBeans.getArray()) {
            System.out.println("got bean: "+requestBean.toString());

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

        return bean;
    }

}
