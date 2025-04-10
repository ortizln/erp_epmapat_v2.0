package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link EstadomApi}.
 */
@Generated
public class EstadomApi__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static EstadomApi apply(RegisteredBean registeredBean, EstadomApi instance) {
    instance.EstmServicio = AutowiredFieldValueResolver.forRequiredField("EstmServicio").resolve(registeredBean);
    return instance;
  }
}
