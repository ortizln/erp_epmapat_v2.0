package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link ClientesApi}.
 */
@Generated
public class ClientesApi__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static ClientesApi apply(RegisteredBean registeredBean, ClientesApi instance) {
    AutowiredFieldValueResolver.forRequiredField("cliServicio").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
