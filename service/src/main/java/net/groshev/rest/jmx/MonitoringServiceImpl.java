package net.groshev.rest.jmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.*;
import org.springframework.stereotype.Component;

/**
 * пример управления свойствами spring-приложения через JMX
 */
@ManagedResource(objectName = "net.groshev:name=MonitoringServiceImpl")
@Component
public class MonitoringServiceImpl implements MonitoringService {
    public static final Logger LOGGER = LoggerFactory.getLogger(MonitoringServiceImpl.class);

    private boolean isDbServicesEnabled = true;

    @ManagedAttribute(description = "DBServices configurator")
    public boolean isDbServicesEnabled() {
        return isDbServicesEnabled;
    }

    @ManagedOperation(description = "set Db services on/off")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "dbServicesEnabled", description = "state")
    })
    public void setDbServicesEnabled(boolean dbServicesEnabled) {
        LOGGER.info("DBServices " + (isDbServicesEnabled ? "enabled" : "disabled"));
        isDbServicesEnabled = dbServicesEnabled;
    }
}
