package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link RutasService}.
 */
@Generated
public class RutasService__BeanDefinitions {
  /**
   * Get the bean definition for 'rutasService'.
   */
  public static BeanDefinition getRutasServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(RutasService.class);
    InstanceSupplier<RutasService> instanceSupplier = InstanceSupplier.using(RutasService::new);
    instanceSupplier = instanceSupplier.andThen(RutasService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
