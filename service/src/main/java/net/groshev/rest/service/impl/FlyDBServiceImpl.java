package net.groshev.rest.service.impl;

import net.groshev.rest.beans.FlyArrayOutBean;
import net.groshev.rest.domain.repository.FlyRepository;
import net.groshev.rest.requests.FlyArrayRequestBean;
import net.groshev.rest.requests.FlyRequestBean;
import net.groshev.rest.service.FlyDBService;
import net.groshev.rest.utils.CassandraUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class FlyDBServiceImpl implements FlyDBService {

    public static final Logger LOGGER = LoggerFactory.getLogger(FlyDBServiceImpl.class);

    private FlyRepository repository;

    @Override
    public void setRepository(FlyRepository repository) {
        this.repository = repository;
    }

    @Override
    public FlyArrayOutBean processByKey(final FlyArrayRequestBean bean) {
        ExecutorService pool = Executors.newFixedThreadPool(bean.getArray().size() + 2);
        long start = System.nanoTime();
        // ищем те, которые есть в базе
        FlyArrayOutBean outBean = repository.find(bean);
        long findPoint = System.nanoTime();
        LOGGER.info("find [{}] cost: {} ms", bean.getArray().size(),
                CassandraUtils.convertToMSecs(findPoint - start));
        // для найденых делаем update счетчиков
        updateByKey(outBean, pool);
        long updatePoint = System.nanoTime();
        LOGGER.info("update cost: {} ms", CassandraUtils.convertToMSecs(updatePoint - findPoint));
        //для ненайденных делаем вставку
        insertByKey(bean, outBean);
        long insertPoint = System.nanoTime();
        LOGGER.info("insert cost: {} ms", CassandraUtils.convertToMSecs(insertPoint - updatePoint));
        return outBean;
    }

    private void updateByKey(final FlyArrayOutBean bean, final ExecutorService pool) {

        // TODO: надо распараллелить update и попробовать


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
