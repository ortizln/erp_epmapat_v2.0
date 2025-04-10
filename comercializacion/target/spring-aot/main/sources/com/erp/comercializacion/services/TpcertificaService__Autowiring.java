package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link TpcertificaService}.
 */
@Generated
public class TpcertificaService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static TpcertificaService apply(RegisteredBean registeredBean,
      TpcertificaService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
