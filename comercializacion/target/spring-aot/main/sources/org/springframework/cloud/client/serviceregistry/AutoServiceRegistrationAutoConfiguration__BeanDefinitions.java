package org.springframework.cloud.client.serviceregistry;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AutoServiceRegistrationAutoConfiguration}.
 */
@Generated
public class AutoServiceRegistrationAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'autoServiceRegistrationAutoConfiguration'.
   */
  public static BeanDefinition getAutoServiceRegistrationAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AutoServiceRegistrationAutoConfiguration.class);
    InstanceSupplier<AutoServiceRegistrationAutoConfiguration> instanceSupplier = InstanceSupplier.using(AutoServiceRegistrationAutoConfiguration::new);
    instanceSupplier = instanceSupplier.andThen(AutoServiceRegistrationAutoConfiguration__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
