package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link FacturasService}.
 */
@Generated
public class FacturasService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static FacturasService apply(RegisteredBean registeredBean, FacturasService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
