package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link RubrosService}.
 */
@Generated
public class RubrosService__BeanDefinitions {
  /**
   * Get the bean definition for 'rubrosService'.
   */
  public static BeanDefinition getRubrosServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(RubrosService.class);
    InstanceSupplier<RubrosService> instanceSupplier = InstanceSupplier.using(RubrosService::new);
    instanceSupplier = instanceSupplier.andThen(RubrosService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
