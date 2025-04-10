package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link RubrosService}.
 */
@Generated
public class RubrosService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static RubrosService apply(RegisteredBean registeredBean, RubrosService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
