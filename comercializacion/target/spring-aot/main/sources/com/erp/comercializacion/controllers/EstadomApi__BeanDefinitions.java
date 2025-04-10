package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link EstadomApi}.
 */
@Generated
public class EstadomApi__BeanDefinitions {
  /**
   * Get the bean definition for 'estadomApi'.
   */
  public static BeanDefinition getEstadomApiBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(EstadomApi.class);
    InstanceSupplier<EstadomApi> instanceSupplier = InstanceSupplier.using(EstadomApi::new);
    instanceSupplier = instanceSupplier.andThen(EstadomApi__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
