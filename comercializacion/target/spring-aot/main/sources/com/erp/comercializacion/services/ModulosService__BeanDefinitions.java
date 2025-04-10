package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ModulosService}.
 */
@Generated
public class ModulosService__BeanDefinitions {
  /**
   * Get the bean definition for 'modulosService'.
   */
  public static BeanDefinition getModulosServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ModulosService.class);
    InstanceSupplier<ModulosService> instanceSupplier = InstanceSupplier.using(ModulosService::new);
    instanceSupplier = instanceSupplier.andThen(ModulosService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
