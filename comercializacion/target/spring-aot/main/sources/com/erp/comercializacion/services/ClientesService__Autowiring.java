package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link ClientesService}.
 */
@Generated
public class ClientesService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static ClientesService apply(RegisteredBean registeredBean, ClientesService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
