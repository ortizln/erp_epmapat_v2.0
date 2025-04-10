package org.springframework.cloud.client.serviceregistry;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AutoServiceRegistrationProperties}.
 */
@Generated
public class AutoServiceRegistrationProperties__BeanDefinitions {
  /**
   * Get the bean definition for 'autoServiceRegistrationProperties'.
   */
  public static BeanDefinition getAutoServiceRegistrationPropertiesBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AutoServiceRegistrationProperties.class);
    beanDefinition.setInstanceSupplier(AutoServiceRegistrationProperties::new);
    return beanDefinition;
  }
}
