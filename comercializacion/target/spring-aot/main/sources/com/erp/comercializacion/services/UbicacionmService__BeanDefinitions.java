package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link UbicacionmService}.
 */
@Generated
public class UbicacionmService__BeanDefinitions {
  /**
   * Get the bean definition for 'ubicacionmService'.
   */
  public static BeanDefinition getUbicacionmServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(UbicacionmService.class);
    InstanceSupplier<UbicacionmService> instanceSupplier = InstanceSupplier.using(UbicacionmService::new);
    instanceSupplier = instanceSupplier.andThen(UbicacionmService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
