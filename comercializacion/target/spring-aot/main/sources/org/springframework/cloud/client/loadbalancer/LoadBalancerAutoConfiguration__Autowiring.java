package org.springframework.cloud.client.loadbalancer;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link LoadBalancerAutoConfiguration}.
 */
@Generated
public class LoadBalancerAutoConfiguration__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static LoadBalancerAutoConfiguration apply(RegisteredBean registeredBean,
      LoadBalancerAutoConfiguration instance) {
    AutowiredFieldValueResolver.forField("restTemplates").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forField("transformers").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
