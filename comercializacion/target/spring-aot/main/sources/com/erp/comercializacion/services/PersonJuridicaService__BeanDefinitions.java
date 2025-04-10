package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link PersonJuridicaService}.
 */
@Generated
public class PersonJuridicaService__BeanDefinitions {
  /**
   * Get the bean definition for 'personJuridicaService'.
   */
  public static BeanDefinition getPersonJuridicaServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(PersonJuridicaService.class);
    InstanceSupplier<PersonJuridicaService> instanceSupplier = InstanceSupplier.using(PersonJuridicaService::new);
    instanceSupplier = instanceSupplier.andThen(PersonJuridicaService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
