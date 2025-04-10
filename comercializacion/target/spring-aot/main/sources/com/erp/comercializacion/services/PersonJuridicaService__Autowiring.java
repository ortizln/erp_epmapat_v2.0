package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link PersonJuridicaService}.
 */
@Generated
public class PersonJuridicaService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static PersonJuridicaService apply(RegisteredBean registeredBean,
      PersonJuridicaService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
