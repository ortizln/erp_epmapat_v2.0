package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link InteresesApi}.
 */
@Generated
public class InteresesApi__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static InteresesApi apply(RegisteredBean registeredBean, InteresesApi instance) {
    AutowiredFieldValueResolver.forRequiredField("inteServicio").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
