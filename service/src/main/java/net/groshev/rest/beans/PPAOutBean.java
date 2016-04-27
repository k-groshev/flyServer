package net.groshev.rest.beans;

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
@JsonSerialize(using = BeanSerializer.class)
public class PPAOutBean {
    PPAMediaBean media;
    String size;
    String tth;

    public PPAMediaBean getMedia() {
        return this.media;
    }

    public void setMedia(final PPAMediaBean media) {
        this.media = media;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(final String size) {
        this.size = size;
    }

    public String getTth() {
        return this.tth;
    }

    public void setTth(final String tth) {
        this.tth = tth;
    }
}
