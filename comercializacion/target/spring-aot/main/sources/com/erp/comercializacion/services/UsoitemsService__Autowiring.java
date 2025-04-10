package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link UsoitemsService}.
 */
@Generated
public class UsoitemsService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static UsoitemsService apply(RegisteredBean registeredBean, UsoitemsService instance) {
    AutowiredFieldValueResolver.forRequiredField("dao").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
