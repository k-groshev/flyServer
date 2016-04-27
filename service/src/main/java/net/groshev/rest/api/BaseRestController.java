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

import net.groshev.rest.beans.ErrorBean;
import net.groshev.rest.beans.SuccessBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by mt on 25.03.2015.
 */
public class BaseRestController {
    protected ResponseEntity<SuccessBean> response200() {
        return new ResponseEntity<SuccessBean>(new SuccessBean(true), HttpStatus.OK);
    }

    protected ResponseEntity response404() {
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    protected ResponseEntity<ErrorBean> response401() {
        return response401(null);
    }

    protected ResponseEntity<ErrorBean> response401(String error) {
        ErrorBean errorBean = new ErrorBean(StringUtils.defaultIfBlank(error, "unauthorized"));
        errorBean.setCode(3);
        errorBean.setType("error");
        return new ResponseEntity<ErrorBean>(errorBean, HttpStatus.UNAUTHORIZED);
    }

    protected ResponseEntity<ErrorBean> response400() {
        return response400(null);
    }

    protected ResponseEntity<ErrorBean> response400(String error) {
        ErrorBean errorBean = new ErrorBean(StringUtils.defaultIfBlank(error, "bad request"));
        errorBean.setCode(2);
        errorBean.setType("error");
        return new ResponseEntity<ErrorBean>(errorBean, HttpStatus.BAD_REQUEST);
    }

    protected ResponseEntity<ErrorBean> response400(String error, ErrorBean errorBean) {
        errorBean.setCode(1);
        errorBean.setType("error");
        errorBean.setMessage(error);
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }

    protected ResponseEntity<ErrorBean> response429(String error) {
        ErrorBean errorBean = new ErrorBean(StringUtils.defaultIfBlank(error, "too many requests"));
        errorBean.setCode(4);
        errorBean.setType("error");
        return new ResponseEntity<ErrorBean>(errorBean, HttpStatus.TOO_MANY_REQUESTS);
    }

    protected ResponseEntity<ErrorBean> response500(String error) {
        ErrorBean errorBean = new ErrorBean(StringUtils.defaultIfBlank(error, "unknown"));
        errorBean.setCode(5);
        errorBean.setType("error");
        return new ResponseEntity<ErrorBean>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected ResponseEntity<ErrorBean> response500(ErrorBean errorBean) {
        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}