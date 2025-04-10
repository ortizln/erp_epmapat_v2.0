package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link RutasApi}.
 */
@Generated
public class RutasApi__BeanDefinitions {
  /**
   * Get the bean definition for 'rutasApi'.
   */
  public static BeanDefinition getRutasApiBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(RutasApi.class);
    InstanceSupplier<RutasApi> instanceSupplier = InstanceSupplier.using(RutasApi::new);
    instanceSupplier = instanceSupplier.andThen(RutasApi__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
