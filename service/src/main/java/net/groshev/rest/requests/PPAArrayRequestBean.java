package net.groshev.rest.requests;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */
public class PPAArrayRequestBean {
    List<PPARequestBean> array;
    PPARequestHeader header;

    public PPAArrayRequestBean() {
    }

    public PPAArrayRequestBean(List<PPARequestBean> array, PPARequestHeader header) {
        this.array = array;
        this.header = header;
    }

    public List<PPARequestBean> getArray() {
        return this.array;
    }

    public void setArray(final List<PPARequestBean> array) {
        this.array = array;
    }

    public PPARequestHeader getHeader() {
        return this.header;
    }

    public void setHeader(PPARequestHeader header) {
        this.header = header;
    }
}
