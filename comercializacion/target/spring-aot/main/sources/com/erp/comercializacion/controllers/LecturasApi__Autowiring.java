package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link LecturasApi}.
 */
@Generated
public class LecturasApi__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static LecturasApi apply(RegisteredBean registeredBean, LecturasApi instance) {
    instance.lecServicio = AutowiredFieldValueResolver.forRequiredField("lecServicio").resolve(registeredBean);
    return instance;
  }
}
