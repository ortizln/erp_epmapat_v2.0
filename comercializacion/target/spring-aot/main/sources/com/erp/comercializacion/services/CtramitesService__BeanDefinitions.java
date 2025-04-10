package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CtramitesService}.
 */
@Generated
public class CtramitesService__BeanDefinitions {
  /**
   * Get the bean definition for 'ctramitesService'.
   */
  public static BeanDefinition getCtramitesServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CtramitesService.class);
    InstanceSupplier<CtramitesService> instanceSupplier = InstanceSupplier.using(CtramitesService::new);
    instanceSupplier = instanceSupplier.andThen(CtramitesService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
