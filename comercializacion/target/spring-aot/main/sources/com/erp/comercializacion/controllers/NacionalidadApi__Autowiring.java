package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link NacionalidadApi}.
 */
@Generated
public class NacionalidadApi__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static NacionalidadApi apply(RegisteredBean registeredBean, NacionalidadApi instance) {
    AutowiredFieldValueResolver.forRequiredField("nacService").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
