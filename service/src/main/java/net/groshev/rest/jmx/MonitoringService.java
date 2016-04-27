package net.groshev.rest.jmx;

/**
 * Created by kgroshev on 05.04.16.
 */
public interface MonitoringService {

    public boolean isDbServicesEnabled();

    public void setDbServicesEnabled(boolean dbServicesEnabled);
}
