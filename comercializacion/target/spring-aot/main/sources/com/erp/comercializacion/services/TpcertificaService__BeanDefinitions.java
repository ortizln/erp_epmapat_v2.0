package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link TpcertificaService}.
 */
@Generated
public class TpcertificaService__BeanDefinitions {
  /**
   * Get the bean definition for 'tpcertificaService'.
   */
  public static BeanDefinition getTpcertificaServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(TpcertificaService.class);
    InstanceSupplier<TpcertificaService> instanceSupplier = InstanceSupplier.using(TpcertificaService::new);
    instanceSupplier = instanceSupplier.andThen(TpcertificaService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
