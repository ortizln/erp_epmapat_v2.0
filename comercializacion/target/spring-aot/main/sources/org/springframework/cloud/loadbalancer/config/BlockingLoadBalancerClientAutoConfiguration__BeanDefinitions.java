package org.springframework.cloud.loadbalancer.config;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.blocking.XForwardedHeadersTransformer;
import org.springframework.cloud.loadbalancer.core.LoadBalancerServiceInstanceCookieTransformer;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;

/**
 * Bean definitions for {@link BlockingLoadBalancerClientAutoConfiguration}.
 */
@Generated
public class BlockingLoadBalancerClientAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'blockingLoadBalancerClientAutoConfiguration'.
   */
  public static BeanDefinition getBlockingLoadBalancerClientAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(BlockingLoadBalancerClientAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(BlockingLoadBalancerClientAutoConfiguration::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'blockingLoadBalancerClient'.
   */
  private static BeanInstanceSupplier<LoadBalancerClient> getBlockingLoadBalancerClientInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<LoadBalancerClient>forFactoryMethod(BlockingLoadBalancerClientAutoConfiguration.class, "blockingLoadBalancerClient", LoadBalancerClientFactory.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.loadbalancer.config.BlockingLoadBalancerClientAutoConfiguration", BlockingLoadBalancerClientAutoConfiguration.class).blockingLoadBalancerClient(args.get(0)));
  }

  /**
   * Get the bean definition for 'blockingLoadBalancerClient'.
   */
  public static BeanDefinition getBlockingLoadBalancerClientBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerClient.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.loadbalancer.config.BlockingLoadBalancerClientAutoConfiguration");
    beanDefinition.setInstanceSupplier(getBlockingLoadBalancerClientInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'loadBalancerServiceInstanceCookieTransformer'.
   */
  private static BeanInstanceSupplier<LoadBalancerServiceInstanceCookieTransformer> getLoadBalancerServiceInstanceCookieTransformerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<LoadBalancerServiceInstanceCookieTransformer>forFactoryMethod(BlockingLoadBalancerClientAutoConfiguration.class, "loadBalancerServiceInstanceCookieTransformer", LoadBalancerClientFactory.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.loadbalancer.config.BlockingLoadBalancerClientAutoConfiguration", BlockingLoadBalancerClientAutoConfiguration.class).loadBalancerServiceInstanceCookieTransformer(args.get(0)));
  }

  /**
   * Get the bean definition for 'loadBalancerServiceInstanceCookieTransformer'.
   */
  public static BeanDefinition getLoadBalancerServiceInstanceCookieTransformerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerServiceInstanceCookieTransformer.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.loadbalancer.config.BlockingLoadBalancerClientAutoConfiguration");
    beanDefinition.setInstanceSupplier(getLoadBalancerServiceInstanceCookieTransformerInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'xForwarderHeadersTransformer'.
   */
  private static BeanInstanceSupplier<XForwardedHeadersTransformer> getXForwarderHeadersTransformerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<XForwardedHeadersTransformer>forFactoryMethod(BlockingLoadBalancerClientAutoConfiguration.class, "xForwarderHeadersTransformer", LoadBalancerClientFactory.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.loadbalancer.config.BlockingLoadBalancerClientAutoConfiguration", BlockingLoadBalancerClientAutoConfiguration.class).xForwarderHeadersTransformer(args.get(0)));
  }

  /**
   * Get the bean definition for 'xForwarderHeadersTransformer'.
   */
  public static BeanDefinition getXForwarderHeadersTransformerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(XForwardedHeadersTransformer.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.loadbalancer.config.BlockingLoadBalancerClientAutoConfiguration");
    beanDefinition.setInstanceSupplier(getXForwarderHeadersTransformerInstanceSupplier());
    return beanDefinition;
  }
}
