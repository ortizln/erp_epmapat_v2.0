package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link EmisionindividualService}.
 */
@Generated
public class EmisionindividualService__BeanDefinitions {
  /**
   * Get the bean definition for 'emisionindividualService'.
   */
  public static BeanDefinition getEmisionindividualServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(EmisionindividualService.class);
    InstanceSupplier<EmisionindividualService> instanceSupplier = InstanceSupplier.using(EmisionindividualService::new);
    instanceSupplier = instanceSupplier.andThen(EmisionindividualService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
