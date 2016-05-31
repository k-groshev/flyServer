package net.groshev.rest.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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

    @Override
    public String toString() {
        return "PPAOutBean{" +
            "id=" + id +
            ", media=" + media +
            ", size=" + size +
            ", tth='" + tth + '\'' +
            '}';
    }
}
