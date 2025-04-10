package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link TipotramiteService}.
 */
@Generated
public class TipotramiteService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static TipotramiteService apply(RegisteredBean registeredBean,
      TipotramiteService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
