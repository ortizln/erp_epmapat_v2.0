package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link InteresService}.
 */
@Generated
public class InteresService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static InteresService apply(RegisteredBean registeredBean, InteresService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("dao_lecturas").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
