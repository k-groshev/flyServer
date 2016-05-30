package net.groshev.rest.domain.repository;

import net.groshev.rest.beans.FlyArrayOutBean;
import net.groshev.rest.beans.FlyOutBean;
import net.groshev.rest.requests.FlyArrayRequestBean;
import net.groshev.rest.requests.FlyRequestBean;

/**
 * Created with IntelliJ IDEA.
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */
public interface FlyRepository {

    Void insert(final FlyRequestBean bean);

    Void update(final FlyArrayOutBean bean);

    Void update(final FlyOutBean bean);

    FlyArrayOutBean find(final FlyArrayRequestBean bean);
}
