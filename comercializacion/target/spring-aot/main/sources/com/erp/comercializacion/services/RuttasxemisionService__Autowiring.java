package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link RuttasxemisionService}.
 */
@Generated
public class RuttasxemisionService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static RuttasxemisionService apply(RegisteredBean registeredBean,
      RuttasxemisionService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
