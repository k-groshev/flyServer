package net.groshev.rest.requests;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */
public class FlyArrayRequestBean {
    private List<FlyRequestBean> array;
    private FlyRequestHeader header;
    private long cache;
    private long different_counter;

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

    public long getCache() {
        return this.cache;
    }

    public void setCache(final long cache) {
        this.cache = cache;
    }

    public long getDifferent_counter() {
        return this.different_counter;
    }

    public void setDifferent_counter(final long different_counter) {
        this.different_counter = different_counter;
    }
}
