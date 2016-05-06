/*
 * Copyright (c) 2009 - 2016 groshev.net
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by the groshev.net.
 * 4. Neither the name of the groshev.net nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY groshev.net ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL groshev.net BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.groshev.rest.beans;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Map;
import org.hibernate.stat.CollectionStatistics;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.NaturalIdCacheStatistics;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;

/**
 * Created by KGroshev on 08.12.2015.
 */
@JsonSerialize
public class HibernateStatBean {

    private Map<String, QueryStatistics> queryStat;
    private Map<String, EntityStatistics> entityStat;
    private Map<String, CollectionStatistics> collectionStat;
    private Map<String, SecondLevelCacheStatistics> secondLevelStat;
    private Map<String, NaturalIdCacheStatistics> naturalIdCacheStat;
    private String startDate;
    private Statistics commonStat;
    private String dbString;
    private String dbmsString;

    public Map<String, QueryStatistics> getQueryStat() {
        return queryStat;
    }

    public void setQueryStat(Map<String, QueryStatistics> queryStat) {
        this.queryStat = queryStat;
    }

    public Map<String, EntityStatistics> getEntityStat() {
        return entityStat;
    }

    public void setEntityStat(Map<String, EntityStatistics> entityStat) {
        this.entityStat = entityStat;
    }

    public Map<String, CollectionStatistics> getCollectionStat() {
        return collectionStat;
    }

    public void setCollectionStat(Map<String, CollectionStatistics> collectionStat) {
        this.collectionStat = collectionStat;
    }

    public Map<String, SecondLevelCacheStatistics> getSecondLevelStat() {
        return secondLevelStat;
    }

    public void setSecondLevelStat(Map<String, SecondLevelCacheStatistics> secondLevelStat) {
        this.secondLevelStat = secondLevelStat;
    }

    public Map<String, NaturalIdCacheStatistics> getNaturalIdCacheStat() {
        return naturalIdCacheStat;
    }

    public void setNaturalIdCacheStat(Map<String, NaturalIdCacheStatistics> naturalIdCacheStat) {
        this.naturalIdCacheStat = naturalIdCacheStat;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Statistics getCommonStat() {
        return commonStat;
    }

    public void setCommonStat(Statistics commonStat) {
        this.commonStat = commonStat;
    }

    public String getDbString() {
        return dbString;
    }

    public void setDbString(String dbString) {
        this.dbString = dbString;
    }

    public String getDbmsString() {
        return dbmsString;
    }

    public void setDbmsString(String dbmsString) {
        this.dbmsString = dbmsString;
    }
}
