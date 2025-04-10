package com.erp.comercializacion.services;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AbonadosSevice}.
 */
@Generated
public class AbonadosSevice__BeanDefinitions {
  /**
   * Get the bean definition for 'abonadosSevice'.
   */
  public static BeanDefinition getAbonadosSeviceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AbonadosSevice.class);
    InstanceSupplier<AbonadosSevice> instanceSupplier = InstanceSupplier.using(AbonadosSevice::new);
    instanceSupplier = instanceSupplier.andThen(AbonadosSevice__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
