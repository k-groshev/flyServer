package net.groshev.rest.mappers;

import net.groshev.rest.beans.FlyMediaBean;
import net.groshev.rest.beans.FlyOutBean;
import net.groshev.rest.common.model.FlyFile;

/**
 * Created with IntelliJ IDEA.
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */
public class FlyOutBeanMapper implements Mapper<FlyFile, FlyOutBean> {
    @Override
    public FlyOutBean map(final FlyFile object) {
        FlyMediaBean mediaBean = new FlyMediaBean();
        mediaBean.setFly_audio(object.getFly_audio());
        mediaBean.setFly_audio_br(object.getFly_audio_br());
        mediaBean.setFly_video(object.getFly_video());
        mediaBean.setFly_xy(object.getFly_xy());

        FlyOutBean outBean = new FlyOutBean();
        outBean.setMedia(mediaBean);

        outBean.setId(object.getId());
        outBean.setTth(object.getTth());
        outBean.setSize(object.getFile_size());

        outBean.setFirst_date(object.getFirst_date());
        outBean.setLast_date(object.getLast_date());

        outBean.setCount_plus(object.getCount_plus());
        outBean.setCount_minus(object.getCount_minus());
        outBean.setCount_fake(object.getCount_fake());
        outBean.setCount_download(object.getCount_download());
        outBean.setCount_upload(object.getCount_upload());
        outBean.setCount_query(object.getCount_query());
        outBean.setCount_media(object.getCount_media());
        outBean.setCount_antivirus(object.getCount_antivirus());

        return outBean;
    }

    @Override
    public FlyFile convert(final FlyOutBean object) {
        throw new UnsupportedOperationException("#convert()");

    }
}
