package net.groshev.rest.beans;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */
@JsonSerialize
public class FlyArrayOutBean {
    List<FlyOutBean> array;

    public List<FlyOutBean> getArray() {
        return this.array;
    }

    public void setArray(final List<FlyOutBean> array) {
        this.array = array;
    }
}
