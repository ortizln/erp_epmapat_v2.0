package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link EmisionesService}.
 */
@Generated
public class EmisionesService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static EmisionesService apply(RegisteredBean registeredBean, EmisionesService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
