package net.groshev.rest.domain.model;

/**
 * CREATE TABLE fly_file (  id             integer primary key AUTOINCREMENT not null,
 * tth            char(39) not null,
 * file_size      NUMBER not null,
 * count_plus     NUMBER default 0 not null,
 * count_minus    NUMBER default 0 not null,
 * count_fake     NUMBER default 0 not null,
 * count_download NUMBER default 0 not null,
 * count_upload   NUMBER default 0 not null,
 * count_query    NUMBER default 1 not null,
 * first_date     int64 not null,
 * last_date      in64,
 * fly_audio text,
 * fly_audio_br text,
 * fly_video text,
 * fly_xy text,
 * count_media NUMBER,
 * count_antivirus NUMBER default 0 not null);
 * CREATE UNIQUE INDEX iu_fly_file_tth ON fly_file(TTH, FILE_SIZE)
 */
public class FlyFile {
    private long id;
    private String tth;
    private long file_size;

    private long first_date;
    private Long last_date;

    private FlyFileCounters counters;

    private String fly_audio;
    private String fly_audio_br;
    private String fly_video;
    private String fly_xy;

    public long getId() {
        return this.id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getTth() {
        return this.tth;
    }

    public void setTth(final String tth) {
        this.tth = tth;
    }

    public long getFile_size() {
        return this.file_size;
    }

    public void setFile_size(final long file_size) {
        this.file_size = file_size;
    }

    public long getFirst_date() {
        return this.first_date;
    }

    public void setFirst_date(final long first_date) {
        this.first_date = first_date;
    }

    public Long getLast_date() {
        return this.last_date;
    }

    public void setLast_date(final Long last_date) {
        this.last_date = last_date;
    }

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

    public FlyFileCounters getCounters() {
        return this.counters;
    }

    public void setCounters(final FlyFileCounters counters) {
        this.counters = counters;
    }

    @Override
    public String toString() {
        return "FlyFile{" +
            "id=" + id +
            ", tth='" + tth + '\'' +
            ", file_size=" + file_size +
            ", first_date=" + first_date +
            ", last_date='" + last_date + '\'' +
            ", fly_audio='" + fly_audio + '\'' +
            ", fly_audio_br='" + fly_audio_br + '\'' +
            ", fly_video='" + fly_video + '\'' +
            ", fly_xy='" + fly_xy + '\'' +
            '}';
    }
}
