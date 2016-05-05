package net.groshev.rest.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.groshev.rest.utils.json.BeanSerializer;

/**
 * {
 * "array" : [
 * {
 * "media" : {
 * "fly_audio" : "43mn 17s | MPEG , 192 Kbps, 2 channels",
 * "fly_audio_br" : 192,
 * "fly_video" : "MPEG-4 , 1 816 Kbps, 16:9, 23.976 fps",
 * "fly_xy" : "720x400"
 * },
 * "size" : "367742976",
 * "tth" : "MHRRU45RGNCNROAEVAYKJEJ4IM52ZQOT6A6DZYA"
 * }
 * ]
 * }
 */
@JsonSerialize
public class FlyOutBean {
    @JsonIgnore
    long id;

    private FlyMediaBean media;
    private long size;
    private String tth;

    @JsonIgnore
    private long count_plus;
    @JsonIgnore
    private long count_minus;
    @JsonIgnore
    private long count_fake;
    @JsonIgnore
    private long count_download;
    @JsonIgnore
    private long count_upload;
    @JsonIgnore
    private long count_query;
    @JsonIgnore
    private long count_media;
    @JsonIgnore
    private long count_antivirus;
    @JsonIgnore
    private long first_date;
    @JsonIgnore
    private String last_date;

    public long getId() {
        return this.id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public FlyMediaBean getMedia() {
        return this.media;
    }

    public void setMedia(final FlyMediaBean media) {
        this.media = media;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(final long size) {
        this.size = size;
    }

    public String getTth() {
        return this.tth;
    }

    public void setTth(final String tth) {
        this.tth = tth;
    }

    public long getCount_plus() {
        return this.count_plus;
    }

    public void setCount_plus(final long count_plus) {
        this.count_plus = count_plus;
    }

    public long getCount_minus() {
        return this.count_minus;
    }

    public void setCount_minus(final long count_minus) {
        this.count_minus = count_minus;
    }

    public long getCount_fake() {
        return this.count_fake;
    }

    public void setCount_fake(final long count_fake) {
        this.count_fake = count_fake;
    }

    public long getCount_download() {
        return this.count_download;
    }

    public void setCount_download(final long count_download) {
        this.count_download = count_download;
    }

    public long getCount_upload() {
        return this.count_upload;
    }

    public void setCount_upload(final long count_upload) {
        this.count_upload = count_upload;
    }

    public long getCount_query() {
        return this.count_query;
    }

    public void setCount_query(final long count_query) {
        this.count_query = count_query;
    }

    public long getCount_media() {
        return this.count_media;
    }

    public void setCount_media(final long count_media) {
        this.count_media = count_media;
    }

    public long getCount_antivirus() {
        return this.count_antivirus;
    }

    public void setCount_antivirus(final long count_antivirus) {
        this.count_antivirus = count_antivirus;
    }

    public long getFirst_date() {
        return this.first_date;
    }

    public void setFirst_date(final long first_date) {
        this.first_date = first_date;
    }

    public String getLast_date() {
        return this.last_date;
    }

    public void setLast_date(final String last_date) {
        this.last_date = last_date;
    }

    @Override
    public String toString() {
        return "PPAOutBean{" +
            "id=" + id +
            ", media=" + media +
            ", size=" + size +
            ", tth='" + tth + '\'' +
            ", count_plus=" + count_plus +
            ", count_minus=" + count_minus +
            ", count_fake=" + count_fake +
            ", count_download=" + count_download +
            ", count_upload=" + count_upload +
            ", count_query=" + count_query +
            ", count_media=" + count_media +
            ", count_antivirus=" + count_antivirus +
            ", first_date=" + first_date +
            ", last_date='" + last_date + '\'' +
            '}';
    }
}
