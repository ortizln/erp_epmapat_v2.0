package com.erp.comercializacion;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ConfigurationClassUtils;

/**
 * Bean definitions for {@link ComercializacionApplication}.
 */
@Generated
public class ComercializacionApplication__BeanDefinitions {
  /**
   * Get the bean definition for 'comercializacionApplication'.
   */
  public static BeanDefinition getComercializacionApplicationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ComercializacionApplication.class);
    beanDefinition.setTargetType(ComercializacionApplication.class);
    ConfigurationClassUtils.initializeConfigurationClass(ComercializacionApplication.class);
    beanDefinition.setInstanceSupplier(ComercializacionApplication$$SpringCGLIB$$0::new);
    return beanDefinition;
  }
}
