package net.groshev.rest.requests;

/**
 * Created with IntelliJ IDEA.
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */
public class FlyRequestBean {
    private String tth;
    private long size;

    public FlyRequestBean() {
    }

    public FlyRequestBean(String tth, long size) {
        this.tth = tth;
        this.size = size;
    }

    public String getTth() {
        return this.tth;
    }

    public void setTth(final String tth) {
        this.tth = tth;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(final long size) {
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
