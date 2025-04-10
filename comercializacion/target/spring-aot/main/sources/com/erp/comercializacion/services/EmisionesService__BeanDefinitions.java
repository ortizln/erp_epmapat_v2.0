package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link EmisionesService}.
 */
@Generated
public class EmisionesService__BeanDefinitions {
  /**
   * Get the bean definition for 'emisionesService'.
   */
  public static BeanDefinition getEmisionesServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(EmisionesService.class);
    InstanceSupplier<EmisionesService> instanceSupplier = InstanceSupplier.using(EmisionesService::new);
    instanceSupplier = instanceSupplier.andThen(EmisionesService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
