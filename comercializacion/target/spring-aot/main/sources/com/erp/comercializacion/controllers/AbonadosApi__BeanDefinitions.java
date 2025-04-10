package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AbonadosApi}.
 */
@Generated
public class AbonadosApi__BeanDefinitions {
  /**
   * Get the bean definition for 'abonadosApi'.
   */
  public static BeanDefinition getAbonadosApiBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AbonadosApi.class);
    InstanceSupplier<AbonadosApi> instanceSupplier = InstanceSupplier.using(AbonadosApi::new);
    instanceSupplier = instanceSupplier.andThen(AbonadosApi__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
