package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link TipopagoService}.
 */
@Generated
public class TipopagoService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static TipopagoService apply(RegisteredBean registeredBean, TipopagoService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
