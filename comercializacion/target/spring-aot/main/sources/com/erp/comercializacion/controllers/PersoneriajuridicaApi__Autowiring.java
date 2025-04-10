package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link PersoneriajuridicaApi}.
 */
@Generated
public class PersoneriajuridicaApi__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static PersoneriajuridicaApi apply(RegisteredBean registeredBean,
      PersoneriajuridicaApi instance) {
    AutowiredFieldValueResolver.forRequiredField("pjService").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
