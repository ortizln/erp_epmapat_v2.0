package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link SuspensionesService}.
 */
@Generated
public class SuspensionesService__BeanDefinitions {
  /**
   * Get the bean definition for 'suspensionesService'.
   */
  public static BeanDefinition getSuspensionesServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(SuspensionesService.class);
    InstanceSupplier<SuspensionesService> instanceSupplier = InstanceSupplier.using(SuspensionesService::new);
    instanceSupplier = instanceSupplier.andThen(SuspensionesService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
