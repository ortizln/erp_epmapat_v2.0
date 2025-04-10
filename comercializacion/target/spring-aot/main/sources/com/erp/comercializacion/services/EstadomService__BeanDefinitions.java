package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link EstadomService}.
 */
@Generated
public class EstadomService__BeanDefinitions {
  /**
   * Get the bean definition for 'estadomService'.
   */
  public static BeanDefinition getEstadomServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(EstadomService.class);
    InstanceSupplier<EstadomService> instanceSupplier = InstanceSupplier.using(EstadomService::new);
    instanceSupplier = instanceSupplier.andThen(EstadomService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
