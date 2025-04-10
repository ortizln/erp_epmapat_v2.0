package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ClientesService}.
 */
@Generated
public class ClientesService__BeanDefinitions {
  /**
   * Get the bean definition for 'clientesService'.
   */
  public static BeanDefinition getClientesServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ClientesService.class);
    InstanceSupplier<ClientesService> instanceSupplier = InstanceSupplier.using(ClientesService::new);
    instanceSupplier = instanceSupplier.andThen(ClientesService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
