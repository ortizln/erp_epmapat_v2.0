package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link LecturasApi}.
 */
@Generated
public class LecturasApi__BeanDefinitions {
  /**
   * Get the bean definition for 'lecturasApi'.
   */
  public static BeanDefinition getLecturasApiBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(LecturasApi.class);
    InstanceSupplier<LecturasApi> instanceSupplier = InstanceSupplier.using(LecturasApi::new);
    instanceSupplier = instanceSupplier.andThen(LecturasApi__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
