package org.springframework.cloud.config.client;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ConfigClientAutoConfiguration}.
 */
@Generated
public class ConfigClientAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'configClientAutoConfiguration'.
   */
  public static BeanDefinition getConfigClientAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigClientAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(ConfigClientAutoConfiguration::new);
    return beanDefinition;
  }
}
