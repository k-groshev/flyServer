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

    public List<PPARequestBean> getArray() {
        return this.array;
    }

    public void setArray(final List<PPARequestBean> array) {
        this.array = array;
    }
}
