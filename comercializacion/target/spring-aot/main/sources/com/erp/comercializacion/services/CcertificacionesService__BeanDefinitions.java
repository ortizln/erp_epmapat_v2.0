package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CcertificacionesService}.
 */
@Generated
public class CcertificacionesService__BeanDefinitions {
  /**
   * Get the bean definition for 'ccertificacionesService'.
   */
  public static BeanDefinition getCcertificacionesServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CcertificacionesService.class);
    InstanceSupplier<CcertificacionesService> instanceSupplier = InstanceSupplier.using(CcertificacionesService::new);
    instanceSupplier = instanceSupplier.andThen(CcertificacionesService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
