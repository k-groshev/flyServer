package net.groshev.rest.domain.model;

/**
 * Created by kgroshev on 30.05.16.
 */
public class FlyFileCounters {

    private String tth;
    private long file_size;

    private long count_plus;
    private long count_minus;
    private long count_fake;
    private long count_download;
    private long count_upload;
    private long count_query;
    private long count_media;
    private long count_antivirus;

    public String getTth() {
        return tth;
    }

    public void setTth(String tth) {
        this.tth = tth;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }

    public long getCount_plus() {
        return count_plus;
    }

    public void setCount_plus(long count_plus) {
        this.count_plus = count_plus;
    }

    public long getCount_minus() {
        return count_minus;
    }

    public void setCount_minus(long count_minus) {
        this.count_minus = count_minus;
    }

    public long getCount_fake() {
        return count_fake;
    }

    public void setCount_fake(long count_fake) {
        this.count_fake = count_fake;
    }

    public long getCount_download() {
        return count_download;
    }

    public void setCount_download(long count_download) {
        this.count_download = count_download;
    }

    public long getCount_upload() {
        return count_upload;
    }

    public void setCount_upload(long count_upload) {
        this.count_upload = count_upload;
    }

    public long getCount_query() {
        return count_query;
    }

    public void setCount_query(long count_query) {
        this.count_query = count_query;
    }

    public long getCount_media() {
        return count_media;
    }

    public void setCount_media(long count_media) {
        this.count_media = count_media;
    }

    public long getCount_antivirus() {
        return count_antivirus;
    }

    public void setCount_antivirus(long count_antivirus) {
        this.count_antivirus = count_antivirus;
    }

    @Override
    public String toString() {
        return "FlyFileCounters{" +
                "tth='" + tth + '\'' +
                ", file_size=" + file_size +
                ", count_plus=" + count_plus +
                ", count_minus=" + count_minus +
                ", count_fake=" + count_fake +
                ", count_download=" + count_download +
                ", count_upload=" + count_upload +
                ", count_query=" + count_query +
                ", count_media=" + count_media +
                ", count_antivirus=" + count_antivirus +
                '}';
    }
}
