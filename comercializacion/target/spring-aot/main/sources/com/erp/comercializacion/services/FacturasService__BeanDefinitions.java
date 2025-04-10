package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link FacturasService}.
 */
@Generated
public class FacturasService__BeanDefinitions {
  /**
   * Get the bean definition for 'facturasService'.
   */
  public static BeanDefinition getFacturasServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(FacturasService.class);
    InstanceSupplier<FacturasService> instanceSupplier = InstanceSupplier.using(FacturasService::new);
    instanceSupplier = instanceSupplier.andThen(FacturasService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
