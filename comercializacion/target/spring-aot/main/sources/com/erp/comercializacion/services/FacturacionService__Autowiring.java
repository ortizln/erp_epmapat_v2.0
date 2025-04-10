package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link FacturacionService}.
 */
@Generated
public class FacturacionService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static FacturacionService apply(RegisteredBean registeredBean,
      FacturacionService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
