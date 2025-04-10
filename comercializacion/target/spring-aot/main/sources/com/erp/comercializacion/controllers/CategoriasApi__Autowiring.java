package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link CategoriasApi}.
 */
@Generated
public class CategoriasApi__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static CategoriasApi apply(RegisteredBean registeredBean, CategoriasApi instance) {
    AutowiredFieldValueResolver.forRequiredField("cateServicio").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
