package org.springframework.cloud.configuration;

import java.util.List;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CompatibilityVerifierAutoConfiguration}.
 */
@Generated
public class CompatibilityVerifierAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'compatibilityVerifierAutoConfiguration'.
   */
  public static BeanDefinition getCompatibilityVerifierAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CompatibilityVerifierAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(CompatibilityVerifierAutoConfiguration::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'compositeCompatibilityVerifier'.
   */
  private static BeanInstanceSupplier<CompositeCompatibilityVerifier> getCompositeCompatibilityVerifierInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<CompositeCompatibilityVerifier>forFactoryMethod(CompatibilityVerifierAutoConfiguration.class, "compositeCompatibilityVerifier", List.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.configuration.CompatibilityVerifierAutoConfiguration", CompatibilityVerifierAutoConfiguration.class).compositeCompatibilityVerifier(args.get(0)));
  }

  /**
   * Get the bean definition for 'compositeCompatibilityVerifier'.
   */
  public static BeanDefinition getCompositeCompatibilityVerifierBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CompositeCompatibilityVerifier.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.configuration.CompatibilityVerifierAutoConfiguration");
    beanDefinition.setInstanceSupplier(getCompositeCompatibilityVerifierInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'springBootVersionVerifier'.
   */
  private static BeanInstanceSupplier<SpringBootVersionVerifier> getSpringBootVersionVerifierInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<SpringBootVersionVerifier>forFactoryMethod(CompatibilityVerifierAutoConfiguration.class, "springBootVersionVerifier", CompatibilityVerifierProperties.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.configuration.CompatibilityVerifierAutoConfiguration", CompatibilityVerifierAutoConfiguration.class).springBootVersionVerifier(args.get(0)));
  }

  /**
   * Get the bean definition for 'springBootVersionVerifier'.
   */
  public static BeanDefinition getSpringBootVersionVerifierBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(SpringBootVersionVerifier.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.configuration.CompatibilityVerifierAutoConfiguration");
    beanDefinition.setInstanceSupplier(getSpringBootVersionVerifierInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'sleuthPresentVerifier'.
   */
  private static BeanInstanceSupplier<SleuthPresentVerifier> getSleuthPresentVerifierInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<SleuthPresentVerifier>forFactoryMethod(CompatibilityVerifierAutoConfiguration.class, "sleuthPresentVerifier")
            .withGenerator((registeredBean) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.configuration.CompatibilityVerifierAutoConfiguration", CompatibilityVerifierAutoConfiguration.class).sleuthPresentVerifier());
  }

  /**
   * Get the bean definition for 'sleuthPresentVerifier'.
   */
  public static BeanDefinition getSleuthPresentVerifierBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(SleuthPresentVerifier.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.configuration.CompatibilityVerifierAutoConfiguration");
    beanDefinition.setInstanceSupplier(getSleuthPresentVerifierInstanceSupplier());
    return beanDefinition;
  }
}
