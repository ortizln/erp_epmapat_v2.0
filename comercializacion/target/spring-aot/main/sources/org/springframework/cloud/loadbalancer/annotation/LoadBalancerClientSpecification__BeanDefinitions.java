package org.springframework.cloud.loadbalancer.annotation;

import java.lang.Class;
import java.lang.String;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link LoadBalancerClientSpecification}.
 */
@Generated
public class LoadBalancerClientSpecification__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'default.org.springframework.cloud.loadbalancer.config.LoadBalancerAutoConfiguration.LoadBalancerClientSpecification'.
   */
  private static BeanInstanceSupplier<LoadBalancerClientSpecification> getLoadBalancerClientSpecificationInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<LoadBalancerClientSpecification>forConstructor(String.class, Class[].class)
            .withGenerator((registeredBean, args) -> new LoadBalancerClientSpecification(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'loadBalancerClientSpecification'.
   */
  public static BeanDefinition getLoadBalancerClientSpecificationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerClientSpecification.class);
    beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, "default.org.springframework.cloud.loadbalancer.config.LoadBalancerAutoConfiguration");
    beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(1, new Class[] {});
    beanDefinition.setInstanceSupplier(getLoadBalancerClientSpecificationInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'default.org.springframework.cloud.loadbalancer.config.BlockingLoadBalancerClientAutoConfiguration.LoadBalancerClientSpecification'.
   */
  private static BeanInstanceSupplier<LoadBalancerClientSpecification> getLoadBalancerClientSpecificationInstanceSupplier1(
      ) {
    return BeanInstanceSupplier.<LoadBalancerClientSpecification>forConstructor(String.class, Class[].class)
            .withGenerator((registeredBean, args) -> new LoadBalancerClientSpecification(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'loadBalancerClientSpecification'.
   */
  public static BeanDefinition getLoadBalancerClientSpecificationBeanDefinition1() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(LoadBalancerClientSpecification.class);
    beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, "default.org.springframework.cloud.loadbalancer.config.BlockingLoadBalancerClientAutoConfiguration");
    beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(1, new Class[] {});
    beanDefinition.setInstanceSupplier(getLoadBalancerClientSpecificationInstanceSupplier1());
    return beanDefinition;
  }
}
