package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link LecturasService}.
 */
@Generated
public class LecturasService__BeanDefinitions {
  /**
   * Get the bean definition for 'lecturasService'.
   */
  public static BeanDefinition getLecturasServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(LecturasService.class);
    InstanceSupplier<LecturasService> instanceSupplier = InstanceSupplier.using(LecturasService::new);
    instanceSupplier = instanceSupplier.andThen(LecturasService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
