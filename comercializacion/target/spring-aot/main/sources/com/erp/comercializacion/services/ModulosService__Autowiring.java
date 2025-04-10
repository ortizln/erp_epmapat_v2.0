package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link ModulosService}.
 */
@Generated
public class ModulosService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static ModulosService apply(RegisteredBean registeredBean, ModulosService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
