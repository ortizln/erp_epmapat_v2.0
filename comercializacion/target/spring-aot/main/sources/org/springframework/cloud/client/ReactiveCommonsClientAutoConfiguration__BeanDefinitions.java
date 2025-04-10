package org.springframework.cloud.client;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ReactiveCommonsClientAutoConfiguration}.
 */
@Generated
public class ReactiveCommonsClientAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'reactiveCommonsClientAutoConfiguration'.
   */
  public static BeanDefinition getReactiveCommonsClientAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ReactiveCommonsClientAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(ReactiveCommonsClientAutoConfiguration::new);
    return beanDefinition;
  }
}
