package org.springframework.cloud.client.loadbalancer;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cloud.commons.config.DefaultsBindHandlerAdvisor;

/**
 * Bean definitions for {@link LoadBalancerDefaultMappingsProviderAutoConfiguration}.
 */
@Generated
public class LoadBalancerDefaultMappingsProviderAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'loadBalancerDefaultMappingsProviderAutoConfiguration'.
   */
  public static BeanDefinition getLoadBalancerDefaultMappingsProviderAutoConfigurationBeanDefinition(
      ) {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerDefaultMappingsProviderAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(LoadBalancerDefaultMappingsProviderAutoConfiguration::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'loadBalancerClientsDefaultsMappingsProvider'.
   */
  private static BeanInstanceSupplier<DefaultsBindHandlerAdvisor.MappingsProvider> getLoadBalancerClientsDefaultsMappingsProviderInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<DefaultsBindHandlerAdvisor.MappingsProvider>forFactoryMethod(LoadBalancerDefaultMappingsProviderAutoConfiguration.class, "loadBalancerClientsDefaultsMappingsProvider")
            .withGenerator((registeredBean) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.client.loadbalancer.LoadBalancerDefaultMappingsProviderAutoConfiguration", LoadBalancerDefaultMappingsProviderAutoConfiguration.class).loadBalancerClientsDefaultsMappingsProvider());
  }

  /**
   * Get the bean definition for 'loadBalancerClientsDefaultsMappingsProvider'.
   */
  public static BeanDefinition getLoadBalancerClientsDefaultsMappingsProviderBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(DefaultsBindHandlerAdvisor.MappingsProvider.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.client.loadbalancer.LoadBalancerDefaultMappingsProviderAutoConfiguration");
    beanDefinition.setInstanceSupplier(getLoadBalancerClientsDefaultsMappingsProviderInstanceSupplier());
    return beanDefinition;
  }
}
