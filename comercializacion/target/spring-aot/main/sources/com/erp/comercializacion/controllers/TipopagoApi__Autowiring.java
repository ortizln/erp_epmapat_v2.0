package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link TipopagoApi}.
 */
@Generated
public class TipopagoApi__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static TipopagoApi apply(RegisteredBean registeredBean, TipopagoApi instance) {
    AutowiredFieldValueResolver.forRequiredField("TipopagoServicio").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
