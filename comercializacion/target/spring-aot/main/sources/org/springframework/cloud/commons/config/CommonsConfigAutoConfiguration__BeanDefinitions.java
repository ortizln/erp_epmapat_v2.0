package org.springframework.cloud.commons.config;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CommonsConfigAutoConfiguration}.
 */
@Generated
public class CommonsConfigAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'commonsConfigAutoConfiguration'.
   */
  public static BeanDefinition getCommonsConfigAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CommonsConfigAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(CommonsConfigAutoConfiguration::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'defaultsBindHandlerAdvisor'.
   */
  private static BeanInstanceSupplier<DefaultsBindHandlerAdvisor> getDefaultsBindHandlerAdvisorInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<DefaultsBindHandlerAdvisor>forFactoryMethod(CommonsConfigAutoConfiguration.class, "defaultsBindHandlerAdvisor", DefaultsBindHandlerAdvisor.MappingsProvider[].class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.commons.config.CommonsConfigAutoConfiguration", CommonsConfigAutoConfiguration.class).defaultsBindHandlerAdvisor(args.get(0)));
  }

  /**
   * Get the bean definition for 'defaultsBindHandlerAdvisor'.
   */
  public static BeanDefinition getDefaultsBindHandlerAdvisorBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(DefaultsBindHandlerAdvisor.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.commons.config.CommonsConfigAutoConfiguration");
    beanDefinition.setInstanceSupplier(getDefaultsBindHandlerAdvisorInstanceSupplier());
    return beanDefinition;
  }
}
