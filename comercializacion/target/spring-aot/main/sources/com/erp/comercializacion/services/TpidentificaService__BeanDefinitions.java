package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link TpidentificaService}.
 */
@Generated
public class TpidentificaService__BeanDefinitions {
  /**
   * Get the bean definition for 'tpidentificaService'.
   */
  public static BeanDefinition getTpidentificaServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(TpidentificaService.class);
    InstanceSupplier<TpidentificaService> instanceSupplier = InstanceSupplier.using(TpidentificaService::new);
    instanceSupplier = instanceSupplier.andThen(TpidentificaService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
