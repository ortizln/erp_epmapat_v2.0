package org.springframework.cloud.netflix.eureka;

import java.lang.SuppressWarnings;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link RestTemplateTimeoutProperties}.
 */
@Generated
public class RestTemplateTimeoutProperties__BeanDefinitions {
  /**
   * Get the bean definition for 'restTemplateTimeoutProperties'.
   */
  @SuppressWarnings("removal")
  public static BeanDefinition getRestTemplateTimeoutPropertiesBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(RestTemplateTimeoutProperties.class);
    beanDefinition.setInstanceSupplier(RestTemplateTimeoutProperties::new);
    return beanDefinition;
  }
}
