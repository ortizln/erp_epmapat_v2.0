package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link UbicacionmApi}.
 */
@Generated
public class UbicacionmApi__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static UbicacionmApi apply(RegisteredBean registeredBean, UbicacionmApi instance) {
    AutowiredFieldValueResolver.forRequiredField("ubimServicio").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
