package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link AbonadosApi}.
 */
@Generated
public class AbonadosApi__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static AbonadosApi apply(RegisteredBean registeredBean, AbonadosApi instance) {
    AutowiredFieldValueResolver.forRequiredField("aboServicio").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
