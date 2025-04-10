package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link UbicacionmApi}.
 */
@Generated
public class UbicacionmApi__BeanDefinitions {
  /**
   * Get the bean definition for 'ubicacionmApi'.
   */
  public static BeanDefinition getUbicacionmApiBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(UbicacionmApi.class);
    InstanceSupplier<UbicacionmApi> instanceSupplier = InstanceSupplier.using(UbicacionmApi::new);
    instanceSupplier = instanceSupplier.andThen(UbicacionmApi__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
