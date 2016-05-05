package net.groshev.rest.beans;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.groshev.rest.utils.json.BeanSerializer;

/**
 * Created with IntelliJ IDEA.
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */
@JsonSerialize()
public class FlyMediaBean {
    private String fly_audio;
    private String fly_audio_br;
    private String fly_video;
    private String fly_xy;

    public String getFly_audio() {
        return this.fly_audio;
    }

    public void setFly_audio(final String fly_audio) {
        this.fly_audio = fly_audio;
    }

    public String getFly_audio_br() {
        return this.fly_audio_br;
    }

    public void setFly_audio_br(final String fly_audio_br) {
        this.fly_audio_br = fly_audio_br;
    }

    public String getFly_video() {
        return this.fly_video;
    }

    public void setFly_video(final String fly_video) {
        this.fly_video = fly_video;
    }

    public String getFly_xy() {
        return this.fly_xy;
    }

    public void setFly_xy(final String fly_xy) {
        this.fly_xy = fly_xy;
    }

    @Override
    public String toString() {
        return "PPAMediaBean{" +
            "fly_audio='" + fly_audio + '\'' +
            ", fly_audio_br='" + fly_audio_br + '\'' +
            ", fly_video='" + fly_video + '\'' +
            ", fly_xy='" + fly_xy + '\'' +
            '}';
    }
}
