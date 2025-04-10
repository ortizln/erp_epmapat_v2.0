package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link TpidentificaService}.
 */
@Generated
public class TpidentificaService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static TpidentificaService apply(RegisteredBean registeredBean,
      TpidentificaService instance) {
    instance.dao = AutowiredFieldValueResolver.forRequiredField("dao").resolve(registeredBean);
    return instance;
  }
}
