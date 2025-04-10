package org.springframework.cloud.client.serviceregistry;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AutoServiceRegistrationConfiguration}.
 */
@Generated
public class AutoServiceRegistrationConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'autoServiceRegistrationConfiguration'.
   */
  public static BeanDefinition getAutoServiceRegistrationConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AutoServiceRegistrationConfiguration.class);
    beanDefinition.setInstanceSupplier(AutoServiceRegistrationConfiguration::new);
    return beanDefinition;
  }
}
