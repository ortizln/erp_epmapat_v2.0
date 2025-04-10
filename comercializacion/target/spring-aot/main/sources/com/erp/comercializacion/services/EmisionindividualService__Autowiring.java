package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link EmisionindividualService}.
 */
@Generated
public class EmisionindividualService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static EmisionindividualService apply(RegisteredBean registeredBean,
      EmisionindividualService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
