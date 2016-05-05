package net.groshev.rest.requests;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */
public class FlyArrayRequestBean {
    List<FlyRequestBean> array;
    FlyRequestHeader header;

    public FlyArrayRequestBean() {
    }

    public FlyArrayRequestBean(List<FlyRequestBean> array, FlyRequestHeader header) {
        this.array = array;
        this.header = header;
    }

    public List<FlyRequestBean> getArray() {
        return this.array;
    }

    public void setArray(final List<FlyRequestBean> array) {
        this.array = array;
    }

    public FlyRequestHeader getHeader() {
        return this.header;
    }

    public void setHeader(FlyRequestHeader header) {
        this.header = header;
    }
}
