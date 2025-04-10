package org.springframework.cloud.loadbalancer.config;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager;
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheProperties;

/**
 * Bean definitions for {@link LoadBalancerCacheAutoConfiguration}.
 */
@Generated
public class LoadBalancerCacheAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'loadBalancerCacheAutoConfiguration'.
   */
  public static BeanDefinition getLoadBalancerCacheAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerCacheAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(LoadBalancerCacheAutoConfiguration::new);
    return beanDefinition;
  }

  /**
   * Bean definitions for {@link LoadBalancerCacheAutoConfiguration.LoadBalancerCacheManagerWarnConfiguration}.
   */
  @Generated
  public static class LoadBalancerCacheManagerWarnConfiguration {
    /**
     * Get the bean definition for 'loadBalancerCacheManagerWarnConfiguration'.
     */
    public static BeanDefinition getLoadBalancerCacheManagerWarnConfigurationBeanDefinition() {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerCacheAutoConfiguration.LoadBalancerCacheManagerWarnConfiguration.class);
      beanDefinition.setInstanceSupplier(LoadBalancerCacheAutoConfiguration.LoadBalancerCacheManagerWarnConfiguration::new);
      return beanDefinition;
    }

    /**
     * Get the bean instance supplier for 'caffeineWarnLogger'.
     */
    private static BeanInstanceSupplier<LoadBalancerCacheAutoConfiguration.LoadBalancerCaffeineWarnLogger> getCaffeineWarnLoggerInstanceSupplier(
        ) {
      return BeanInstanceSupplier.<LoadBalancerCacheAutoConfiguration.LoadBalancerCaffeineWarnLogger>forFactoryMethod(LoadBalancerCacheAutoConfiguration.LoadBalancerCacheManagerWarnConfiguration.class, "caffeineWarnLogger")
              .withGenerator((registeredBean) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.loadbalancer.config.LoadBalancerCacheAutoConfiguration$LoadBalancerCacheManagerWarnConfiguration", LoadBalancerCacheAutoConfiguration.LoadBalancerCacheManagerWarnConfiguration.class).caffeineWarnLogger());
    }

    /**
     * Get the bean definition for 'caffeineWarnLogger'.
     */
    public static BeanDefinition getCaffeineWarnLoggerBeanDefinition() {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerCacheAutoConfiguration.LoadBalancerCaffeineWarnLogger.class);
      beanDefinition.setFactoryBeanName("org.springframework.cloud.loadbalancer.config.LoadBalancerCacheAutoConfiguration$LoadBalancerCacheManagerWarnConfiguration");
      beanDefinition.setInstanceSupplier(getCaffeineWarnLoggerInstanceSupplier());
      return beanDefinition;
    }
  }

  /**
   * Bean definitions for {@link LoadBalancerCacheAutoConfiguration.DefaultLoadBalancerCacheManagerConfiguration}.
   */
  @Generated
  public static class DefaultLoadBalancerCacheManagerConfiguration {
    /**
     * Get the bean definition for 'defaultLoadBalancerCacheManagerConfiguration'.
     */
    public static BeanDefinition getDefaultLoadBalancerCacheManagerConfigurationBeanDefinition() {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerCacheAutoConfiguration.DefaultLoadBalancerCacheManagerConfiguration.class);
      beanDefinition.setInstanceSupplier(LoadBalancerCacheAutoConfiguration.DefaultLoadBalancerCacheManagerConfiguration::new);
      return beanDefinition;
    }

    /**
     * Get the bean instance supplier for 'defaultLoadBalancerCacheManager'.
     */
    private static BeanInstanceSupplier<LoadBalancerCacheManager> getDefaultLoadBalancerCacheManagerInstanceSupplier(
        ) {
      return BeanInstanceSupplier.<LoadBalancerCacheManager>forFactoryMethod(LoadBalancerCacheAutoConfiguration.DefaultLoadBalancerCacheManagerConfiguration.class, "defaultLoadBalancerCacheManager", LoadBalancerCacheProperties.class)
              .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.loadbalancer.config.LoadBalancerCacheAutoConfiguration$DefaultLoadBalancerCacheManagerConfiguration", LoadBalancerCacheAutoConfiguration.DefaultLoadBalancerCacheManagerConfiguration.class).defaultLoadBalancerCacheManager(args.get(0)));
    }

    /**
     * Get the bean definition for 'defaultLoadBalancerCacheManager'.
     */
    public static BeanDefinition getDefaultLoadBalancerCacheManagerBeanDefinition() {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerCacheManager.class);
      beanDefinition.setAutowireCandidate(false);
      beanDefinition.setFactoryBeanName("org.springframework.cloud.loadbalancer.config.LoadBalancerCacheAutoConfiguration$DefaultLoadBalancerCacheManagerConfiguration");
      beanDefinition.setInstanceSupplier(getDefaultLoadBalancerCacheManagerInstanceSupplier());
      return beanDefinition;
    }
  }
}
