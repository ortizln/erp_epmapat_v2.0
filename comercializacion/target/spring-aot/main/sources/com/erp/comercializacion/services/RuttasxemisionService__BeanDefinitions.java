package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link RuttasxemisionService}.
 */
@Generated
public class RuttasxemisionService__BeanDefinitions {
  /**
   * Get the bean definition for 'ruttasxemisionService'.
   */
  public static BeanDefinition getRuttasxemisionServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(RuttasxemisionService.class);
    InstanceSupplier<RuttasxemisionService> instanceSupplier = InstanceSupplier.using(RuttasxemisionService::new);
    instanceSupplier = instanceSupplier.andThen(RuttasxemisionService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
