package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CatalogoitemsService}.
 */
@Generated
public class CatalogoitemsService__BeanDefinitions {
  /**
   * Get the bean definition for 'catalogoitemsService'.
   */
  public static BeanDefinition getCatalogoitemsServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CatalogoitemsService.class);
    InstanceSupplier<CatalogoitemsService> instanceSupplier = InstanceSupplier.using(CatalogoitemsService::new);
    instanceSupplier = instanceSupplier.andThen(CatalogoitemsService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
