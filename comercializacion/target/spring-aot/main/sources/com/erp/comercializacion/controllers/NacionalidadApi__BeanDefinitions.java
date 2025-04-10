package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link NacionalidadApi}.
 */
@Generated
public class NacionalidadApi__BeanDefinitions {
  /**
   * Get the bean definition for 'nacionalidadApi'.
   */
  public static BeanDefinition getNacionalidadApiBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(NacionalidadApi.class);
    InstanceSupplier<NacionalidadApi> instanceSupplier = InstanceSupplier.using(NacionalidadApi::new);
    instanceSupplier = instanceSupplier.andThen(NacionalidadApi__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
