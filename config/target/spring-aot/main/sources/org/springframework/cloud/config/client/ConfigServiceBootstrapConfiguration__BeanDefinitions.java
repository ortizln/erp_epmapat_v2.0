package org.springframework.cloud.config.client;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ConfigServiceBootstrapConfiguration}.
 */
@Generated
public class ConfigServiceBootstrapConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'configServiceBootstrapConfiguration'.
   */
  public static BeanDefinition getConfigServiceBootstrapConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigServiceBootstrapConfiguration.class);
    InstanceSupplier<ConfigServiceBootstrapConfiguration> instanceSupplier = InstanceSupplier.using(ConfigServiceBootstrapConfiguration::new);
    instanceSupplier = instanceSupplier.andThen(ConfigServiceBootstrapConfiguration__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'configClientProperties'.
   */
  private static BeanInstanceSupplier<ConfigClientProperties> getConfigClientPropertiesInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<ConfigClientProperties>forFactoryMethod(ConfigServiceBootstrapConfiguration.class, "configClientProperties")
            .withGenerator((registeredBean) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.config.client.ConfigServiceBootstrapConfiguration", ConfigServiceBootstrapConfiguration.class).configClientProperties());
  }

  /**
   * Get the bean definition for 'configClientProperties'.
   */
  public static BeanDefinition getConfigClientPropertiesBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigClientProperties.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.config.client.ConfigServiceBootstrapConfiguration");
    InstanceSupplier<ConfigClientProperties> instanceSupplier = getConfigClientPropertiesInstanceSupplier();
    instanceSupplier = instanceSupplier.andThen(ConfigClientProperties__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
