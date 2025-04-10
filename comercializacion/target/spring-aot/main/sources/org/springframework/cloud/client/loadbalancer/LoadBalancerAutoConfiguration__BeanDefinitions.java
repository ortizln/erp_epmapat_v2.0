package org.springframework.cloud.client.loadbalancer;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;

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
    InstanceSupplier<LoadBalancerAutoConfiguration> instanceSupplier = InstanceSupplier.using(LoadBalancerAutoConfiguration::new);
    instanceSupplier = instanceSupplier.andThen(LoadBalancerAutoConfiguration__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'loadBalancedRestTemplateInitializerDeprecated'.
   */
  private static BeanInstanceSupplier<SmartInitializingSingleton> getLoadBalancedRestTemplateInitializerDeprecatedInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<SmartInitializingSingleton>forFactoryMethod(LoadBalancerAutoConfiguration.class, "loadBalancedRestTemplateInitializerDeprecated", ObjectProvider.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration", LoadBalancerAutoConfiguration.class).loadBalancedRestTemplateInitializerDeprecated(args.get(0)));
  }

  /**
   * Get the bean definition for 'loadBalancedRestTemplateInitializerDeprecated'.
   */
  public static BeanDefinition getLoadBalancedRestTemplateInitializerDeprecatedBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(SmartInitializingSingleton.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration");
    beanDefinition.setInstanceSupplier(getLoadBalancedRestTemplateInitializerDeprecatedInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'loadBalancerRequestFactory'.
   */
  private static BeanInstanceSupplier<LoadBalancerRequestFactory> getLoadBalancerRequestFactoryInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<LoadBalancerRequestFactory>forFactoryMethod(LoadBalancerAutoConfiguration.class, "loadBalancerRequestFactory", LoadBalancerClient.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration", LoadBalancerAutoConfiguration.class).loadBalancerRequestFactory(args.get(0)));
  }

  /**
   * Get the bean definition for 'loadBalancerRequestFactory'.
   */
  public static BeanDefinition getLoadBalancerRequestFactoryBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerRequestFactory.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration");
    beanDefinition.setInstanceSupplier(getLoadBalancerRequestFactoryInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Bean definitions for {@link LoadBalancerAutoConfiguration.LoadBalancerInterceptorConfig}.
   */
  @Generated
  public static class LoadBalancerInterceptorConfig {
    /**
     * Get the bean definition for 'loadBalancerInterceptorConfig'.
     */
    public static BeanDefinition getLoadBalancerInterceptorConfigBeanDefinition() {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerAutoConfiguration.LoadBalancerInterceptorConfig.class);
      beanDefinition.setInstanceSupplier(LoadBalancerAutoConfiguration.LoadBalancerInterceptorConfig::new);
      return beanDefinition;
    }

    /**
     * Get the bean instance supplier for 'loadBalancerInterceptor'.
     */
    private static BeanInstanceSupplier<LoadBalancerInterceptor> getLoadBalancerInterceptorInstanceSupplier(
        ) {
      return BeanInstanceSupplier.<LoadBalancerInterceptor>forFactoryMethod(LoadBalancerAutoConfiguration.LoadBalancerInterceptorConfig.class, "loadBalancerInterceptor", LoadBalancerClient.class, LoadBalancerRequestFactory.class)
              .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration$LoadBalancerInterceptorConfig", LoadBalancerAutoConfiguration.LoadBalancerInterceptorConfig.class).loadBalancerInterceptor(args.get(0), args.get(1)));
    }

    /**
     * Get the bean definition for 'loadBalancerInterceptor'.
     */
    public static BeanDefinition getLoadBalancerInterceptorBeanDefinition() {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerInterceptor.class);
      beanDefinition.setFactoryBeanName("org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration$LoadBalancerInterceptorConfig");
      beanDefinition.setInstanceSupplier(getLoadBalancerInterceptorInstanceSupplier());
      return beanDefinition;
    }

    /**
     * Get the bean instance supplier for 'restTemplateCustomizer'.
     */
    private static BeanInstanceSupplier<RestTemplateCustomizer> getRestTemplateCustomizerInstanceSupplier(
        ) {
      return BeanInstanceSupplier.<RestTemplateCustomizer>forFactoryMethod(LoadBalancerAutoConfiguration.LoadBalancerInterceptorConfig.class, "restTemplateCustomizer", LoadBalancerInterceptor.class)
              .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration$LoadBalancerInterceptorConfig", LoadBalancerAutoConfiguration.LoadBalancerInterceptorConfig.class).restTemplateCustomizer(args.get(0)));
    }

    /**
     * Get the bean definition for 'restTemplateCustomizer'.
     */
    public static BeanDefinition getRestTemplateCustomizerBeanDefinition() {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(RestTemplateCustomizer.class);
      beanDefinition.setFactoryBeanName("org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration$LoadBalancerInterceptorConfig");
      beanDefinition.setInstanceSupplier(getRestTemplateCustomizerInstanceSupplier());
      return beanDefinition;
    }
  }

  /**
   * Bean definitions for {@link LoadBalancerAutoConfiguration.DeferringLoadBalancerInterceptorConfig}.
   */
  @Generated
  public static class DeferringLoadBalancerInterceptorConfig {
    /**
     * Get the bean definition for 'deferringLoadBalancerInterceptorConfig'.
     */
    public static BeanDefinition getDeferringLoadBalancerInterceptorConfigBeanDefinition() {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerAutoConfiguration.DeferringLoadBalancerInterceptorConfig.class);
      beanDefinition.setInstanceSupplier(LoadBalancerAutoConfiguration.DeferringLoadBalancerInterceptorConfig::new);
      return beanDefinition;
    }

    /**
     * Get the bean instance supplier for 'deferringLoadBalancerInterceptor'.
     */
    private static BeanInstanceSupplier<DeferringLoadBalancerInterceptor> getDeferringLoadBalancerInterceptorInstanceSupplier(
        ) {
      return BeanInstanceSupplier.<DeferringLoadBalancerInterceptor>forFactoryMethod(LoadBalancerAutoConfiguration.DeferringLoadBalancerInterceptorConfig.class, "deferringLoadBalancerInterceptor", ObjectProvider.class)
              .withGenerator((registeredBean, args) -> LoadBalancerAutoConfiguration.DeferringLoadBalancerInterceptorConfig.deferringLoadBalancerInterceptor(args.get(0)));
    }

    /**
     * Get the bean definition for 'deferringLoadBalancerInterceptor'.
     */
    public static BeanDefinition getDeferringLoadBalancerInterceptorBeanDefinition() {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerAutoConfiguration.DeferringLoadBalancerInterceptorConfig.class);
      beanDefinition.setTargetType(DeferringLoadBalancerInterceptor.class);
      beanDefinition.setInstanceSupplier(getDeferringLoadBalancerInterceptorInstanceSupplier());
      return beanDefinition;
    }

    /**
     * Get the bean instance supplier for 'lbRestClientPostProcessor'.
     */
    private static BeanInstanceSupplier<LoadBalancerRestClientBuilderBeanPostProcessor> getLbRestClientPostProcessorInstanceSupplier(
        ) {
      return BeanInstanceSupplier.<LoadBalancerRestClientBuilderBeanPostProcessor>forFactoryMethod(LoadBalancerAutoConfiguration.DeferringLoadBalancerInterceptorConfig.class, "lbRestClientPostProcessor", ObjectProvider.class, ApplicationContext.class)
              .withGenerator((registeredBean, args) -> LoadBalancerAutoConfiguration.DeferringLoadBalancerInterceptorConfig.lbRestClientPostProcessor(args.get(0), args.get(1)));
    }

    /**
     * Get the bean definition for 'lbRestClientPostProcessor'.
     */
    public static BeanDefinition getLbRestClientPostProcessorBeanDefinition() {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerAutoConfiguration.DeferringLoadBalancerInterceptorConfig.class);
      beanDefinition.setTargetType(ResolvableType.forClassWithGenerics(LoadBalancerRestClientBuilderBeanPostProcessor.class, DeferringLoadBalancerInterceptor.class));
      beanDefinition.setInstanceSupplier(getLbRestClientPostProcessorInstanceSupplier());
      return beanDefinition;
    }

    /**
     * Get the bean instance supplier for 'lbRestTemplateBuilderPostProcessor'.
     */
    private static BeanInstanceSupplier<LoadBalancerRestTemplateBuilderBeanPostProcessor> getLbRestTemplateBuilderPostProcessorInstanceSupplier(
        ) {
      return BeanInstanceSupplier.<LoadBalancerRestTemplateBuilderBeanPostProcessor>forFactoryMethod(LoadBalancerAutoConfiguration.DeferringLoadBalancerInterceptorConfig.class, "lbRestTemplateBuilderPostProcessor", ObjectProvider.class, ApplicationContext.class)
              .withGenerator((registeredBean, args) -> LoadBalancerAutoConfiguration.DeferringLoadBalancerInterceptorConfig.lbRestTemplateBuilderPostProcessor(args.get(0), args.get(1)));
    }

    /**
     * Get the bean definition for 'lbRestTemplateBuilderPostProcessor'.
     */
    public static BeanDefinition getLbRestTemplateBuilderPostProcessorBeanDefinition() {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerAutoConfiguration.DeferringLoadBalancerInterceptorConfig.class);
      beanDefinition.setTargetType(ResolvableType.forClassWithGenerics(LoadBalancerRestTemplateBuilderBeanPostProcessor.class, DeferringLoadBalancerInterceptor.class));
      beanDefinition.setInstanceSupplier(getLbRestTemplateBuilderPostProcessorInstanceSupplier());
      return beanDefinition;
    }
  }
}
