package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link UbicacionmService}.
 */
@Generated
public class UbicacionmService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static UbicacionmService apply(RegisteredBean registeredBean, UbicacionmService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
