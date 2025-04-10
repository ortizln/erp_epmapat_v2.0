package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AbonadosxsuspensionService}.
 */
@Generated
public class AbonadosxsuspensionService__BeanDefinitions {
  /**
   * Get the bean definition for 'abonadosxsuspensionService'.
   */
  public static BeanDefinition getAbonadosxsuspensionServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AbonadosxsuspensionService.class);
    InstanceSupplier<AbonadosxsuspensionService> instanceSupplier = InstanceSupplier.using(AbonadosxsuspensionService::new);
    instanceSupplier = instanceSupplier.andThen(AbonadosxsuspensionService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
