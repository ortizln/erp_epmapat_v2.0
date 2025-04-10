package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link CcertificacionesService}.
 */
@Generated
public class CcertificacionesService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static CcertificacionesService apply(RegisteredBean registeredBean,
      CcertificacionesService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
