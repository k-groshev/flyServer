package net.groshev.rest.service;

import net.groshev.rest.beans.FlyArrayOutBean;
import net.groshev.rest.domain.repository.FlyRepository;
import net.groshev.rest.requests.FlyArrayRequestBean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */
public interface FlyDBService {

    void setRepository(FlyRepository repository);

    FlyArrayOutBean processByKey(FlyArrayRequestBean bean);
}
