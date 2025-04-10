package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link CtramitesService}.
 */
@Generated
public class CtramitesService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static CtramitesService apply(RegisteredBean registeredBean, CtramitesService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
