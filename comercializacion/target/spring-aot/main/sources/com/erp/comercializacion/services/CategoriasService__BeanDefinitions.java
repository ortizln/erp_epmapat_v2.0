package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CategoriasService}.
 */
@Generated
public class CategoriasService__BeanDefinitions {
  /**
   * Get the bean definition for 'categoriasService'.
   */
  public static BeanDefinition getCategoriasServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CategoriasService.class);
    InstanceSupplier<CategoriasService> instanceSupplier = InstanceSupplier.using(CategoriasService::new);
    instanceSupplier = instanceSupplier.andThen(CategoriasService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
