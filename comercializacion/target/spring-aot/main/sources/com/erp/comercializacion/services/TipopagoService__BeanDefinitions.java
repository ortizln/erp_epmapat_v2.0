package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link TipopagoService}.
 */
@Generated
public class TipopagoService__BeanDefinitions {
  /**
   * Get the bean definition for 'tipopagoService'.
   */
  public static BeanDefinition getTipopagoServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(TipopagoService.class);
    InstanceSupplier<TipopagoService> instanceSupplier = InstanceSupplier.using(TipopagoService::new);
    instanceSupplier = instanceSupplier.andThen(TipopagoService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
