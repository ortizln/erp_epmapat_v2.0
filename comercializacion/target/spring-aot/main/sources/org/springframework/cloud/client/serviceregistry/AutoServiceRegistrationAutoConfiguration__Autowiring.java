package org.springframework.cloud.client.serviceregistry;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link AutoServiceRegistrationAutoConfiguration}.
 */
@Generated
public class AutoServiceRegistrationAutoConfiguration__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static AutoServiceRegistrationAutoConfiguration apply(RegisteredBean registeredBean,
      AutoServiceRegistrationAutoConfiguration instance) {
    AutowiredFieldValueResolver.forField("autoServiceRegistration").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("properties").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
