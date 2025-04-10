package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link SuspensionesService}.
 */
@Generated
public class SuspensionesService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static SuspensionesService apply(RegisteredBean registeredBean,
      SuspensionesService instance) {
    AutowiredFieldValueResolver.forRequiredField("suspensionesR").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
