package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link EstadomService}.
 */
@Generated
public class EstadomService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static EstadomService apply(RegisteredBean registeredBean, EstadomService instance) {
    instance.dao = AutowiredFieldValueResolver.forRequiredField("dao").resolve(registeredBean);
    return instance;
  }
}
