package org.springframework.cloud.autoconfigure;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cloud.context.properties.ConfigurationPropertiesBeans;
import org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder;

/**
 * Bean definitions for {@link ConfigurationPropertiesRebinderAutoConfiguration}.
 */
@Generated
public class ConfigurationPropertiesRebinderAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'configurationPropertiesRebinderAutoConfiguration'.
   */
  public static BeanDefinition getConfigurationPropertiesRebinderAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigurationPropertiesRebinderAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(ConfigurationPropertiesRebinderAutoConfiguration::new);
    return beanDefinition;
  }

  /**
   * Get the bean definition for 'configurationPropertiesBeans'.
   */
  public static BeanDefinition getConfigurationPropertiesBeansBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigurationPropertiesRebinderAutoConfiguration.class);
    beanDefinition.setTargetType(ConfigurationPropertiesBeans.class);
    beanDefinition.setInstanceSupplier(BeanInstanceSupplier.<ConfigurationPropertiesBeans>forFactoryMethod(ConfigurationPropertiesRebinderAutoConfiguration.class, "configurationPropertiesBeans").withGenerator((registeredBean) -> ConfigurationPropertiesRebinderAutoConfiguration.configurationPropertiesBeans()));
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'configurationPropertiesRebinder'.
   */
  private static BeanInstanceSupplier<ConfigurationPropertiesRebinder> getConfigurationPropertiesRebinderInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<ConfigurationPropertiesRebinder>forFactoryMethod(ConfigurationPropertiesRebinderAutoConfiguration.class, "configurationPropertiesRebinder", ConfigurationPropertiesBeans.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.autoconfigure.ConfigurationPropertiesRebinderAutoConfiguration", ConfigurationPropertiesRebinderAutoConfiguration.class).configurationPropertiesRebinder(args.get(0)));
  }

  /**
   * Get the bean definition for 'configurationPropertiesRebinder'.
   */
  public static BeanDefinition getConfigurationPropertiesRebinderBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigurationPropertiesRebinder.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.autoconfigure.ConfigurationPropertiesRebinderAutoConfiguration");
    beanDefinition.setInstanceSupplier(getConfigurationPropertiesRebinderInstanceSupplier());
    return beanDefinition;
  }
}
