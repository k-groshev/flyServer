package net.groshev.rest.service.impl;

import net.groshev.rest.beans.FlyArrayOutBean;
import net.groshev.rest.domain.repository.FlyRepository;
import net.groshev.rest.requests.FlyArrayRequestBean;
import net.groshev.rest.requests.FlyRequestBean;
import net.groshev.rest.service.FlyDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */
@Service
public class FlyDBServiceImpl implements FlyDBService {

    public static final Logger LOGGER = LoggerFactory.getLogger(FlyDBServiceImpl.class);

    @Autowired
    @Qualifier("flyCassandraRepository")
    private FlyRepository repository;

    @Override
    public FlyArrayOutBean processByKey(final FlyArrayRequestBean bean) {
        // ищем те, которые есть в базе
        FlyArrayOutBean outBean = repository.find(bean);
        // для найденых делаем update счетчиков
        updateByKey(outBean);
        //для ненайденных делаем вставку
        insertByKey(bean, outBean);

        return outBean;
    }

    private void updateByKey(final FlyArrayOutBean bean) {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        final long timeoutMs = 100;

        CompletableFuture<Void> future
                = CompletableFuture.supplyAsync(() -> repository.update(bean), pool);
        try {
            future.get();
        } catch (Exception ex) {
            LOGGER.debug("ex:" + ex.getClass().getName() + " message:" + ex.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private void insertByKey(final FlyArrayRequestBean beanIn, final FlyArrayOutBean beanOut) {

        List<String> outCollection = new ArrayList<>();
        if (beanOut != null && beanOut.getArray() != null) {
            outCollection = beanOut.getArray().stream()
                    .map(e -> e.getTth() + "~" + e.getSize())
                    .collect(Collectors.toList());

        }

        final List<String> finalOutCollection = outCollection;
        List<String> col = beanIn.getArray().stream()
                .map(e -> e.getTth() + "~" + e.getSize())
                .filter(e -> !finalOutCollection.contains(e))
                .collect(Collectors.toList());
        if (col.isEmpty()) {
            return;
        }

        List<FlyRequestBean> collect = col.stream()
                .map(r -> new FlyRequestBean(r.substring(0, r.indexOf("~")), Long.decode(r.substring(r.indexOf("~") + 1))))
                .collect(Collectors.toList());

        ExecutorService pool = Executors.newFixedThreadPool(collect.size());
        final long timeoutMs = 100;
        collect.parallelStream()
                .map(e ->
                        CompletableFuture.supplyAsync(() -> repository.insert(e), pool)
                )
                .forEach(future -> {
                    try {
                        future.get();
                    } catch (Exception ex) {
                        LOGGER.debug("ex:" + ex.getClass().getName() + " message:" + ex.getMessage());
                    }
                });
        pool.shutdown();
    }

}
