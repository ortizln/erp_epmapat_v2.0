package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link FacturacionService}.
 */
@Generated
public class FacturacionService__BeanDefinitions {
  /**
   * Get the bean definition for 'facturacionService'.
   */
  public static BeanDefinition getFacturacionServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(FacturacionService.class);
    InstanceSupplier<FacturacionService> instanceSupplier = InstanceSupplier.using(FacturacionService::new);
    instanceSupplier = instanceSupplier.andThen(FacturacionService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
