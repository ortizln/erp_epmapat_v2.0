package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link NovedadesService}.
 */
@Generated
public class NovedadesService__BeanDefinitions {
  /**
   * Get the bean definition for 'novedadesService'.
   */
  public static BeanDefinition getNovedadesServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(NovedadesService.class);
    InstanceSupplier<NovedadesService> instanceSupplier = InstanceSupplier.using(NovedadesService::new);
    instanceSupplier = instanceSupplier.andThen(NovedadesService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
