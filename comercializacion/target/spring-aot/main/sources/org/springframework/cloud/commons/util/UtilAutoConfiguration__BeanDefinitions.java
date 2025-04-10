package org.springframework.cloud.commons.util;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link UtilAutoConfiguration}.
 */
@Generated
public class UtilAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'utilAutoConfiguration'.
   */
  public static BeanDefinition getUtilAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(UtilAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(UtilAutoConfiguration::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'inetUtilsProperties'.
   */
  private static BeanInstanceSupplier<InetUtilsProperties> getInetUtilsPropertiesInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<InetUtilsProperties>forFactoryMethod(UtilAutoConfiguration.class, "inetUtilsProperties")
            .withGenerator((registeredBean) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.commons.util.UtilAutoConfiguration", UtilAutoConfiguration.class).inetUtilsProperties());
  }

  /**
   * Get the bean definition for 'inetUtilsProperties'.
   */
  public static BeanDefinition getInetUtilsPropertiesBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(InetUtilsProperties.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.commons.util.UtilAutoConfiguration");
    InstanceSupplier<InetUtilsProperties> instanceSupplier = getInetUtilsPropertiesInstanceSupplier();
    instanceSupplier = instanceSupplier.andThen(InetUtilsProperties__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'inetUtils'.
   */
  private static BeanInstanceSupplier<InetUtils> getInetUtilsInstanceSupplier() {
    return BeanInstanceSupplier.<InetUtils>forFactoryMethod(UtilAutoConfiguration.class, "inetUtils", InetUtilsProperties.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.commons.util.UtilAutoConfiguration", UtilAutoConfiguration.class).inetUtils(args.get(0)));
  }

  /**
   * Get the bean definition for 'inetUtils'.
   */
  public static BeanDefinition getInetUtilsBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(InetUtils.class);
    beanDefinition.setDestroyMethodNames("close");
    beanDefinition.setFactoryBeanName("org.springframework.cloud.commons.util.UtilAutoConfiguration");
    beanDefinition.setInstanceSupplier(getInetUtilsInstanceSupplier());
    return beanDefinition;
  }
}
