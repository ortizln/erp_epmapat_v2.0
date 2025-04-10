package org.springframework.cloud.autoconfigure;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cloud.context.environment.EnvironmentManager;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Bean definitions for {@link LifecycleMvcEndpointAutoConfiguration}.
 */
@Generated
public class LifecycleMvcEndpointAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'lifecycleMvcEndpointAutoConfiguration'.
   */
  public static BeanDefinition getLifecycleMvcEndpointAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(LifecycleMvcEndpointAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(LifecycleMvcEndpointAutoConfiguration::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'environmentManager'.
   */
  private static BeanInstanceSupplier<EnvironmentManager> getEnvironmentManagerInstanceSupplier() {
    return BeanInstanceSupplier.<EnvironmentManager>forFactoryMethod(LifecycleMvcEndpointAutoConfiguration.class, "environmentManager", ConfigurableEnvironment.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.autoconfigure.LifecycleMvcEndpointAutoConfiguration", LifecycleMvcEndpointAutoConfiguration.class).environmentManager(args.get(0)));
  }

  /**
   * Get the bean definition for 'environmentManager'.
   */
  public static BeanDefinition getEnvironmentManagerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(EnvironmentManager.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.autoconfigure.LifecycleMvcEndpointAutoConfiguration");
    beanDefinition.setInstanceSupplier(getEnvironmentManagerInstanceSupplier());
    return beanDefinition;
  }
}
