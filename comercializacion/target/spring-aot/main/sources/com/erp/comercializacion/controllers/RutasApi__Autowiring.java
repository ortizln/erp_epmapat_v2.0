package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link RutasApi}.
 */
@Generated
public class RutasApi__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static RutasApi apply(RegisteredBean registeredBean, RutasApi instance) {
    AutowiredFieldValueResolver.forRequiredField("rutServicio").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
