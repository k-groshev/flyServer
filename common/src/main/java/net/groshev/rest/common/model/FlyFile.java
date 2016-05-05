package net.groshev.rest.common.model;

/**
 * REATE TABLE fly_file (  id             integer primary key AUTOINCREMENT not null,
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

    private long count_plus;
    private long count_minus;
    private long count_fake;
    private long count_download;
    private long count_upload;
    private long count_query;
    private long count_media;
    private long count_antivirus;
    private long first_date;
    private String last_date;

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
        return "FlyFile{" +
            "id=" + id +
            ", tth='" + tth + '\'' +
            ", file_size=" + file_size +
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
            ", fly_audio='" + fly_audio + '\'' +
            ", fly_audio_br='" + fly_audio_br + '\'' +
            ", fly_video='" + fly_video + '\'' +
            ", fly_xy='" + fly_xy + '\'' +
            '}';
    }
}
