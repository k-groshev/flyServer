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
import net.groshev.rest.beans.ErrorBean;
import net.groshev.rest.beans.GitRepositoryStateBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/adm/git")
@Api(value = "/git", description = "Operations about git build scope")
public class GitStatusRestController extends BaseRestController {

    @Qualifier("gitRepositoryStateBean")
    @Autowired
    private GitRepositoryStateBean gitRepositoryStateBean;

    /**
     * Вернуть статус сборки.
     * @response.200.doc Информация статусе сборки.
     * @response.200.example {@link GitRepositoryStateBean#EXAMPLE}
     * @response.404.doc Возвращается если данных не обнаружено.
     */
    @RequestMapping(value = "/status",
        method = GET,
        produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(
        value = "Get git build info", notes = "Returns bean with git build time scope",
        response = GitRepositoryStateBean.class
/*            authorizations = {
                    @Authorization(value = "api_key"),
                    @Authorization(value = "petstore_auth", scopes = {
                            @AuthorizationScope(scope = "write:pets", description = ""),
                            @AuthorizationScope(scope = "read:pets", description = "")
                    })}*/
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = GitRepositoryStateBean.class),
        @ApiResponse(code = 404, message = "Git info not found", response = ErrorBean.class)}
    )
    @ResponseBody
    public Object getBuildStatus() {
        if (gitRepositoryStateBean == null) {
            return response404();
        }
        return gitRepositoryStateBean;
    }
}
