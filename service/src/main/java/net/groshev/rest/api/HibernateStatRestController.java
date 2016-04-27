/*
 * Copyright (c) 2009 - 2016 groshev.net
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by the groshev.net.
 * 4. Neither the name of the groshev.net nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY groshev.net ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL groshev.net BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.groshev.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.groshev.rest.service.HibernateStatService;
import net.groshev.rest.beans.ErrorBean;
import net.groshev.rest.beans.HibernateStatBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by KGroshev on 08.12.2015.
 */
@RestController
@RequestMapping(value = "/api/v1/adm/hibernate")
@Api(value = "/hibernate", description = "Operations about hibernate status")
public class HibernateStatRestController extends BaseRestController {
    @Qualifier("hibernateStatServiceImpl")
    @Autowired
    private HibernateStatService hibernateStatService;

    @RequestMapping(value = "/status",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(
        value = "Get hibernate stat info", notes = "Returns bean with current hibernate statistic",
        response = HibernateStatBean.class
/*            authorizations = {
                    @Authorization(value = "api_key"),
                    @Authorization(value = "petstore_auth", scopes = {
                            @AuthorizationScope(scope = "write:pets", description = ""),
                            @AuthorizationScope(scope = "read:pets", description = "")
                    })}*/
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = HibernateStatBean.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = ErrorBean.class),
        @ApiResponse(code = 404, message = "Not Found", response = ErrorBean.class)}
    )
    @ResponseBody
    public Object getStat() {
        if (hibernateStatService.getStatistics() == null) {
            return response404();
        }
        return hibernateStatService.getStatistics();
    }
}
