package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link UsoitemsService}.
 */
@Generated
public class UsoitemsService__BeanDefinitions {
  /**
   * Get the bean definition for 'usoitemsService'.
   */
  public static BeanDefinition getUsoitemsServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(UsoitemsService.class);
    InstanceSupplier<UsoitemsService> instanceSupplier = InstanceSupplier.using(UsoitemsService::new);
    instanceSupplier = instanceSupplier.andThen(UsoitemsService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
