package org.springframework.cloud.loadbalancer.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerEagerLoadProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.loadbalancer.support.LoadBalancerEagerContextInitializer;
import org.springframework.core.env.Environment;

/**
 * Bean definitions for {@link LoadBalancerAutoConfiguration}.
 */
@Generated
public class LoadBalancerAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'loadBalancerAutoConfiguration'.
   */
  public static BeanDefinition getLoadBalancerAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(LoadBalancerAutoConfiguration::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'zoneConfig'.
   */
  private static BeanInstanceSupplier<LoadBalancerZoneConfig> getZoneConfigInstanceSupplier() {
    return BeanInstanceSupplier.<LoadBalancerZoneConfig>forFactoryMethod(LoadBalancerAutoConfiguration.class, "zoneConfig", Environment.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.loadbalancer.config.LoadBalancerAutoConfiguration", LoadBalancerAutoConfiguration.class).zoneConfig(args.get(0)));
  }

  /**
   * Get the bean definition for 'zoneConfig'.
   */
  public static BeanDefinition getZoneConfigBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerZoneConfig.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.loadbalancer.config.LoadBalancerAutoConfiguration");
    beanDefinition.setInstanceSupplier(getZoneConfigInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Use AOT child context management initialization
   */
  private static LoadBalancerClientFactory loadBalancerClientFactoryAddChildContextInitializer(
      RegisteredBean registeredBean, LoadBalancerClientFactory instance) {
    Map<String, Object> initializers = new HashMap<>();
    return instance.withApplicationContextInitializers(initializers);
  }

  /**
   * Get the bean instance supplier for 'loadBalancerClientFactory'.
   */
  private static BeanInstanceSupplier<LoadBalancerClientFactory> getLoadBalancerClientFactoryInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<LoadBalancerClientFactory>forFactoryMethod(LoadBalancerAutoConfiguration.class, "loadBalancerClientFactory", LoadBalancerClientsProperties.class, ObjectProvider.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.loadbalancer.config.LoadBalancerAutoConfiguration", LoadBalancerAutoConfiguration.class).loadBalancerClientFactory(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'loadBalancerClientFactory'.
   */
  public static BeanDefinition getLoadBalancerClientFactoryBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerClientFactory.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.loadbalancer.config.LoadBalancerAutoConfiguration");
    InstanceSupplier<LoadBalancerClientFactory> instanceSupplier = getLoadBalancerClientFactoryInstanceSupplier();
    instanceSupplier = instanceSupplier.andThen(LoadBalancerAutoConfiguration__BeanDefinitions::loadBalancerClientFactoryAddChildContextInitializer);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'loadBalancerEagerContextInitializer'.
   */
  private static BeanInstanceSupplier<LoadBalancerEagerContextInitializer> getLoadBalancerEagerContextInitializerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<LoadBalancerEagerContextInitializer>forFactoryMethod(LoadBalancerAutoConfiguration.class, "loadBalancerEagerContextInitializer", LoadBalancerClientFactory.class, LoadBalancerEagerLoadProperties.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.loadbalancer.config.LoadBalancerAutoConfiguration", LoadBalancerAutoConfiguration.class).loadBalancerEagerContextInitializer(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'loadBalancerEagerContextInitializer'.
   */
  public static BeanDefinition getLoadBalancerEagerContextInitializerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerEagerContextInitializer.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.loadbalancer.config.LoadBalancerAutoConfiguration");
    beanDefinition.setInstanceSupplier(getLoadBalancerEagerContextInitializerInstanceSupplier());
    return beanDefinition;
  }
}
