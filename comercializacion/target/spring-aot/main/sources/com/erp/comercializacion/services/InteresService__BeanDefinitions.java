package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link InteresService}.
 */
@Generated
public class InteresService__BeanDefinitions {
  /**
   * Get the bean definition for 'interesService'.
   */
  public static BeanDefinition getInteresServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(InteresService.class);
    InstanceSupplier<InteresService> instanceSupplier = InstanceSupplier.using(InteresService::new);
    instanceSupplier = instanceSupplier.andThen(InteresService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
