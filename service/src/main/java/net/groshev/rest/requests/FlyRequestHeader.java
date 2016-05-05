package net.groshev.rest.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by kgroshev on 01.05.16.
 */
public class FlyRequestHeader {

    @JsonProperty("ID")
    private String id;

    public FlyRequestHeader() {
    }

    public FlyRequestHeader(String id) {

        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
