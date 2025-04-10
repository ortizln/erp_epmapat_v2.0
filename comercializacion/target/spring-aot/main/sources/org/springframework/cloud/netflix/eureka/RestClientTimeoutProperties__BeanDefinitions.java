package org.springframework.cloud.netflix.eureka;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link RestClientTimeoutProperties}.
 */
@Generated
public class RestClientTimeoutProperties__BeanDefinitions {
  /**
   * Get the bean definition for 'restClientTimeoutProperties'.
   */
  public static BeanDefinition getRestClientTimeoutPropertiesBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(RestClientTimeoutProperties.class);
    beanDefinition.setInstanceSupplier(RestClientTimeoutProperties::new);
    return beanDefinition;
  }
}
