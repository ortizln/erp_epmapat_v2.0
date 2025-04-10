package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link TptramiteService}.
 */
@Generated
public class TptramiteService__BeanDefinitions {
  /**
   * Get the bean definition for 'tptramiteService'.
   */
  public static BeanDefinition getTptramiteServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(TptramiteService.class);
    InstanceSupplier<TptramiteService> instanceSupplier = InstanceSupplier.using(TptramiteService::new);
    instanceSupplier = instanceSupplier.andThen(TptramiteService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
