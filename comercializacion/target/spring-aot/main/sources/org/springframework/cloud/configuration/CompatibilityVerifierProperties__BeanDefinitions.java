package org.springframework.cloud.configuration;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CompatibilityVerifierProperties}.
 */
@Generated
public class CompatibilityVerifierProperties__BeanDefinitions {
  /**
   * Get the bean definition for 'compatibilityVerifierProperties'.
   */
  public static BeanDefinition getCompatibilityVerifierPropertiesBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CompatibilityVerifierProperties.class);
    beanDefinition.setInstanceSupplier(CompatibilityVerifierProperties::new);
    return beanDefinition;
  }
}
