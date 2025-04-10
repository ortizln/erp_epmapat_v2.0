package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link CategoriasService}.
 */
@Generated
public class CategoriasService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static CategoriasService apply(RegisteredBean registeredBean, CategoriasService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
