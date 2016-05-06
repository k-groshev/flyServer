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
    private long only_counter;

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

    public long getOnly_counter() {
        return this.only_counter;
    }

    public void setOnly_counter(final long only_counter) {
        this.only_counter = only_counter;
    }

    @Override
    public String toString() {
        return "PPARequestBean{" +
            "tth='" + tth + '\'' +
            ", size='" + size + '\'' +
            '}';
    }
}
