package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link InteresesApi}.
 */
@Generated
public class InteresesApi__BeanDefinitions {
  /**
   * Get the bean definition for 'interesesApi'.
   */
  public static BeanDefinition getInteresesApiBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(InteresesApi.class);
    InstanceSupplier<InteresesApi> instanceSupplier = InstanceSupplier.using(InteresesApi::new);
    instanceSupplier = instanceSupplier.andThen(InteresesApi__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
