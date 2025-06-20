package org.springframework.cloud.config.server.config;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 * Bean definitions for {@link DefaultTextEncryptionAutoConfiguration}.
 */
@Generated
public class DefaultTextEncryptionAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'defaultTextEncryptionAutoConfiguration'.
   */
  public static BeanDefinition getDefaultTextEncryptionAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(DefaultTextEncryptionAutoConfiguration.class);
    InstanceSupplier<DefaultTextEncryptionAutoConfiguration> instanceSupplier = InstanceSupplier.using(DefaultTextEncryptionAutoConfiguration::new);
    instanceSupplier = instanceSupplier.andThen(DefaultTextEncryptionAutoConfiguration__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'defaultTextEncryptor'.
   */
  private static BeanInstanceSupplier<TextEncryptor> getDefaultTextEncryptorInstanceSupplier() {
    return BeanInstanceSupplier.<TextEncryptor>forFactoryMethod(DefaultTextEncryptionAutoConfiguration.class, "defaultTextEncryptor", KeyProperties.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.config.server.config.DefaultTextEncryptionAutoConfiguration", DefaultTextEncryptionAutoConfiguration.class).defaultTextEncryptor(args.get(0)));
  }

  /**
   * Get the bean definition for 'defaultTextEncryptor'.
   */
  public static BeanDefinition getDefaultTextEncryptorBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(TextEncryptor.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.config.server.config.DefaultTextEncryptionAutoConfiguration");
    beanDefinition.setInstanceSupplier(getDefaultTextEncryptorInstanceSupplier());
    return beanDefinition;
  }
}
