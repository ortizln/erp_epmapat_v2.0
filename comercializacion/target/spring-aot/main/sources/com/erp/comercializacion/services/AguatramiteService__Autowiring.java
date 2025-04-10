package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link AguatramiteService}.
 */
@Generated
public class AguatramiteService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static AguatramiteService apply(RegisteredBean registeredBean,
      AguatramiteService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
