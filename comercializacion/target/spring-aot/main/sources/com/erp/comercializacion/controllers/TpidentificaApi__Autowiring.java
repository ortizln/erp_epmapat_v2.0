package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link TpidentificaApi}.
 */
@Generated
public class TpidentificaApi__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static TpidentificaApi apply(RegisteredBean registeredBean, TpidentificaApi instance) {
    AutowiredFieldValueResolver.forRequiredField("TpidentificaServicio").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
