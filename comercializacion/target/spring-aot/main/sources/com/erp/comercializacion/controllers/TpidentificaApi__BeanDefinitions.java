package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link TpidentificaApi}.
 */
@Generated
public class TpidentificaApi__BeanDefinitions {
  /**
   * Get the bean definition for 'tpidentificaApi'.
   */
  public static BeanDefinition getTpidentificaApiBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(TpidentificaApi.class);
    InstanceSupplier<TpidentificaApi> instanceSupplier = InstanceSupplier.using(TpidentificaApi::new);
    instanceSupplier = instanceSupplier.andThen(TpidentificaApi__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
