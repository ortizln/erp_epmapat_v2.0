package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link RutasService}.
 */
@Generated
public class RutasService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static RutasService apply(RegisteredBean registeredBean, RutasService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
