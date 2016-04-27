package net.groshev.rest.requests;

/**
 * Created with IntelliJ IDEA.
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */
public class PPARequestBean {
    private String tth;
    private String size;

    public String getTth() {
        return this.tth;
    }

    public void setTth(final String tth) {
        this.tth = tth;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(final String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "PPARequestBean{" +
            "tth='" + tth + '\'' +
            ", size='" + size + '\'' +
            '}';
    }
}
