package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link TipotramiteService}.
 */
@Generated
public class TipotramiteService__BeanDefinitions {
  /**
   * Get the bean definition for 'tipotramiteService'.
   */
  public static BeanDefinition getTipotramiteServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(TipotramiteService.class);
    InstanceSupplier<TipotramiteService> instanceSupplier = InstanceSupplier.using(TipotramiteService::new);
    instanceSupplier = instanceSupplier.andThen(TipotramiteService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
