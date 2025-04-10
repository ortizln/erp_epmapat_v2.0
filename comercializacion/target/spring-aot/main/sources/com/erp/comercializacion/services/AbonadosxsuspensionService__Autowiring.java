package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link AbonadosxsuspensionService}.
 */
@Generated
public class AbonadosxsuspensionService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static AbonadosxsuspensionService apply(RegisteredBean registeredBean,
      AbonadosxsuspensionService instance) {
    AutowiredFieldValueResolver.forRequiredField("aboxsuspensionR").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
