package org.springframework.cloud.client;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CommonsClientAutoConfiguration}.
 */
@Generated
public class CommonsClientAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'commonsClientAutoConfiguration'.
   */
  public static BeanDefinition getCommonsClientAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CommonsClientAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(CommonsClientAutoConfiguration::new);
    return beanDefinition;
  }
}
