package org.springframework.cloud.commons.security;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ResourceServerTokenRelayAutoConfiguration}.
 */
@Generated
public class ResourceServerTokenRelayAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'resourceServerTokenRelayAutoConfiguration'.
   */
  public static BeanDefinition getResourceServerTokenRelayAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ResourceServerTokenRelayAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(ResourceServerTokenRelayAutoConfiguration::new);
    return beanDefinition;
  }

  /**
   * Bean definitions for {@link ResourceServerTokenRelayAutoConfiguration.ResourceServerTokenRelayRegistrationAutoConfiguration}.
   */
  @Generated
  public static class ResourceServerTokenRelayRegistrationAutoConfiguration {
    /**
     * Get the bean definition for 'resourceServerTokenRelayRegistrationAutoConfiguration'.
     */
    public static BeanDefinition getResourceServerTokenRelayRegistrationAutoConfigurationBeanDefinition(
        ) {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(ResourceServerTokenRelayAutoConfiguration.ResourceServerTokenRelayRegistrationAutoConfiguration.class);
      beanDefinition.setInstanceSupplier(ResourceServerTokenRelayAutoConfiguration.ResourceServerTokenRelayRegistrationAutoConfiguration::new);
      return beanDefinition;
    }
  }
}
