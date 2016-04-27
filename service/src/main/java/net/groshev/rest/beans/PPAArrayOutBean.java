package net.groshev.rest.beans;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import net.groshev.rest.utils.json.BeanSerializer;

/**
 * Created with IntelliJ IDEA.
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */
@JsonSerialize(using = BeanSerializer.class)
public class PPAArrayOutBean {
    List<PPAOutBean> array;

    public List<PPAOutBean> getArray() {
        return this.array;
    }

    public void setArray(final List<PPAOutBean> array) {
        this.array = array;
    }
}
