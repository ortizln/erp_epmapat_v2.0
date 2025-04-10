package org.springframework.cloud.client.serviceregistry;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ServiceRegistryAutoConfiguration}.
 */
@Generated
public class ServiceRegistryAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'serviceRegistryAutoConfiguration'.
   */
  public static BeanDefinition getServiceRegistryAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ServiceRegistryAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(ServiceRegistryAutoConfiguration::new);
    return beanDefinition;
  }
}
