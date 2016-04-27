package net.groshev.rest.jmx;

import org.javasimon.SimonManager;
import org.javasimon.jmx.SimonManagerMXBean;
import org.javasimon.jmx.SimonManagerMXBeanImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * Created by kgroshev on 05.04.16.
 */
@Component
public class SimonJmxRegistration {
    @Autowired
    private MBeanServer mbeanServer;

    private ObjectName objectName;

    /**
     * Registers the statistics MBean that wraps a SimonMXBean.
     *
     * @throws JMException if anything fails..
     * @see SimonJmxRegistration#unregister()
     */
    public void register() throws JMException {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName name = new ObjectName("org.javasimon.jmx:type=Simon");
            if (mbs.isRegistered(name)) {
                mbs.unregisterMBean(name);
            }
            SimonManagerMXBean simon = new SimonManagerMXBeanImpl(SimonManager.manager());
            mbs.registerMBean(simon, name);
            System.out.println("SimonManagerMXBean registered under name: "+name);
        } catch (JMException e) {
            System.out.println("SimonManagerMXBean registration failed!\n"+e);
        }
    }

    /**
     * Unregisters the MBean that was registered.
     *
     * @throws JMException if the de-registration fails
     * @see SimonJmxRegistration#register()
     */
    public void unregister() throws JMException {
        //mbeanServer.unregisterMBean(objectName);

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName name = new ObjectName("org.javasimon.jmx:type=Simon");
            if (mbs.isRegistered(name)) {
                mbs.unregisterMBean(name);
            }
            System.out.println("SimonManagerMXBean was unregistered");
        } catch (JMException e) {
            System.out.println("SimonManagerMXBean unregistration failed!\n"+e);
        }
    }
}
