package org.springframework.cloud.client.loadbalancer;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link LoadBalancerClientsProperties}.
 */
@Generated
public class LoadBalancerClientsProperties__BeanDefinitions {
  /**
   * Get the bean definition for 'loadBalancerClientsProperties'.
   */
  public static BeanDefinition getLoadBalancerClientsPropertiesBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerClientsProperties.class);
    beanDefinition.setInstanceSupplier(LoadBalancerClientsProperties::new);
    return beanDefinition;
  }
}
