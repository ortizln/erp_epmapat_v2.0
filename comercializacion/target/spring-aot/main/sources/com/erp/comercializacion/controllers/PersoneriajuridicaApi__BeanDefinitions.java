package com.erp.comercializacion.controllers;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link PersoneriajuridicaApi}.
 */
@Generated
public class PersoneriajuridicaApi__BeanDefinitions {
  /**
   * Get the bean definition for 'personeriajuridicaApi'.
   */
  public static BeanDefinition getPersoneriajuridicaApiBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(PersoneriajuridicaApi.class);
    InstanceSupplier<PersoneriajuridicaApi> instanceSupplier = InstanceSupplier.using(PersoneriajuridicaApi::new);
    instanceSupplier = instanceSupplier.andThen(PersoneriajuridicaApi__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
