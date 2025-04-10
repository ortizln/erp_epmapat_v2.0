package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CategoriasApi}.
 */
@Generated
public class CategoriasApi__BeanDefinitions {
  /**
   * Get the bean definition for 'categoriasApi'.
   */
  public static BeanDefinition getCategoriasApiBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CategoriasApi.class);
    InstanceSupplier<CategoriasApi> instanceSupplier = InstanceSupplier.using(CategoriasApi::new);
    instanceSupplier = instanceSupplier.andThen(CategoriasApi__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
