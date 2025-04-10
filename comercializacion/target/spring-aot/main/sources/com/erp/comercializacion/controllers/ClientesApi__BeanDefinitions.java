package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ClientesApi}.
 */
@Generated
public class ClientesApi__BeanDefinitions {
  /**
   * Get the bean definition for 'clientesApi'.
   */
  public static BeanDefinition getClientesApiBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ClientesApi.class);
    InstanceSupplier<ClientesApi> instanceSupplier = InstanceSupplier.using(ClientesApi::new);
    instanceSupplier = instanceSupplier.andThen(ClientesApi__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
