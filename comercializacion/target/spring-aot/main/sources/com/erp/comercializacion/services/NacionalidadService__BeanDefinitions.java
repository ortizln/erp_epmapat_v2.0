package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link NacionalidadService}.
 */
@Generated
public class NacionalidadService__BeanDefinitions {
  /**
   * Get the bean definition for 'nacionalidadService'.
   */
  public static BeanDefinition getNacionalidadServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(NacionalidadService.class);
    InstanceSupplier<NacionalidadService> instanceSupplier = InstanceSupplier.using(NacionalidadService::new);
    instanceSupplier = instanceSupplier.andThen(NacionalidadService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
