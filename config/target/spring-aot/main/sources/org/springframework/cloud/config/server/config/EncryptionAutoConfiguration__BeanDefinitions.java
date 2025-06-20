package org.springframework.cloud.config.server.config;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cloud.config.server.encryption.EnvironmentEncryptor;
import org.springframework.cloud.config.server.encryption.SingleTextEncryptorLocator;
import org.springframework.cloud.config.server.encryption.TextEncryptorLocator;
import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 * Bean definitions for {@link EncryptionAutoConfiguration}.
 */
@Generated
public class EncryptionAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'encryptionAutoConfiguration'.
   */
  public static BeanDefinition getEncryptionAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(EncryptionAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(EncryptionAutoConfiguration::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'singleTextEncryptorLocator'.
   */
  private static BeanInstanceSupplier<SingleTextEncryptorLocator> getSingleTextEncryptorLocatorInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<SingleTextEncryptorLocator>forFactoryMethod(EncryptionAutoConfiguration.class, "singleTextEncryptorLocator", TextEncryptor.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.config.server.config.EncryptionAutoConfiguration", EncryptionAutoConfiguration.class).singleTextEncryptorLocator(args.get(0)));
  }

  /**
   * Get the bean definition for 'singleTextEncryptorLocator'.
   */
  public static BeanDefinition getSingleTextEncryptorLocatorBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(SingleTextEncryptorLocator.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.config.server.config.EncryptionAutoConfiguration");
    beanDefinition.setInstanceSupplier(getSingleTextEncryptorLocatorInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'environmentEncryptor'.
   */
  private static BeanInstanceSupplier<EnvironmentEncryptor> getEnvironmentEncryptorInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<EnvironmentEncryptor>forFactoryMethod(EncryptionAutoConfiguration.class, "environmentEncryptor", TextEncryptorLocator.class, TextEncryptor.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.config.server.config.EncryptionAutoConfiguration", EncryptionAutoConfiguration.class).environmentEncryptor(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'environmentEncryptor'.
   */
  public static BeanDefinition getEnvironmentEncryptorBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(EnvironmentEncryptor.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.config.server.config.EncryptionAutoConfiguration");
    beanDefinition.setInstanceSupplier(getEnvironmentEncryptorInstanceSupplier());
    return beanDefinition;
  }
}
