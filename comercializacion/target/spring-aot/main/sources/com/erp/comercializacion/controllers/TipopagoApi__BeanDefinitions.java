package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link TipopagoApi}.
 */
@Generated
public class TipopagoApi__BeanDefinitions {
  /**
   * Get the bean definition for 'tipopagoApi'.
   */
  public static BeanDefinition getTipopagoApiBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(TipopagoApi.class);
    InstanceSupplier<TipopagoApi> instanceSupplier = InstanceSupplier.using(TipopagoApi::new);
    instanceSupplier = instanceSupplier.andThen(TipopagoApi__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
