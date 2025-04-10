package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AguatramiteService}.
 */
@Generated
public class AguatramiteService__BeanDefinitions {
  /**
   * Get the bean definition for 'aguatramiteService'.
   */
  public static BeanDefinition getAguatramiteServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AguatramiteService.class);
    InstanceSupplier<AguatramiteService> instanceSupplier = InstanceSupplier.using(AguatramiteService::new);
    instanceSupplier = instanceSupplier.andThen(AguatramiteService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
