package org.springframework.cloud.netflix.eureka.config;

import java.lang.SuppressWarnings;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cloud.configuration.TlsProperties;
import org.springframework.cloud.netflix.eureka.RestTemplateTimeoutProperties;
import org.springframework.cloud.netflix.eureka.http.EurekaClientHttpRequestFactorySupplier;
import org.springframework.cloud.netflix.eureka.http.RestTemplateDiscoveryClientOptionalArgs;
import org.springframework.cloud.netflix.eureka.http.RestTemplateTransportClientFactories;

/**
 * Bean definitions for {@link DiscoveryClientOptionalArgsConfiguration}.
 */
@Generated
public class DiscoveryClientOptionalArgsConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'discoveryClientOptionalArgsConfiguration'.
   */
  public static BeanDefinition getDiscoveryClientOptionalArgsConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(DiscoveryClientOptionalArgsConfiguration.class);
    beanDefinition.setInstanceSupplier(DiscoveryClientOptionalArgsConfiguration::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'tlsProperties'.
   */
  private static BeanInstanceSupplier<TlsProperties> getTlsPropertiesInstanceSupplier() {
    return BeanInstanceSupplier.<TlsProperties>forFactoryMethod(DiscoveryClientOptionalArgsConfiguration.class, "tlsProperties")
            .withGenerator((registeredBean) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.netflix.eureka.config.DiscoveryClientOptionalArgsConfiguration", DiscoveryClientOptionalArgsConfiguration.class).tlsProperties());
  }

  /**
   * Get the bean definition for 'tlsProperties'.
   */
  public static BeanDefinition getTlsPropertiesBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(TlsProperties.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.netflix.eureka.config.DiscoveryClientOptionalArgsConfiguration");
    beanDefinition.setInstanceSupplier(getTlsPropertiesInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Bean definitions for {@link DiscoveryClientOptionalArgsConfiguration.RestTemplateConfiguration}.
   */
  @Generated
  public static class RestTemplateConfiguration {
    /**
     * Get the bean definition for 'restTemplateConfiguration'.
     */
    @SuppressWarnings("deprecation")
    public static BeanDefinition getRestTemplateConfigurationBeanDefinition() {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(DiscoveryClientOptionalArgsConfiguration.RestTemplateConfiguration.class);
      beanDefinition.setInstanceSupplier(DiscoveryClientOptionalArgsConfiguration.RestTemplateConfiguration::new);
      return beanDefinition;
    }

    /**
     * Get the bean instance supplier for 'defaultEurekaClientHttpRequestFactorySupplier'.
     */
    @SuppressWarnings({ "deprecation", "removal" })
    private static BeanInstanceSupplier<EurekaClientHttpRequestFactorySupplier> getDefaultEurekaClientHttpRequestFactorySupplierInstanceSupplier(
        ) {
      return BeanInstanceSupplier.<EurekaClientHttpRequestFactorySupplier>forFactoryMethod(DiscoveryClientOptionalArgsConfiguration.RestTemplateConfiguration.class, "defaultEurekaClientHttpRequestFactorySupplier", RestTemplateTimeoutProperties.class)
              .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.netflix.eureka.config.DiscoveryClientOptionalArgsConfiguration$RestTemplateConfiguration", DiscoveryClientOptionalArgsConfiguration.RestTemplateConfiguration.class).defaultEurekaClientHttpRequestFactorySupplier(args.get(0)));
    }

    /**
     * Get the bean definition for 'defaultEurekaClientHttpRequestFactorySupplier'.
     */
    public static BeanDefinition getDefaultEurekaClientHttpRequestFactorySupplierBeanDefinition() {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(EurekaClientHttpRequestFactorySupplier.class);
      beanDefinition.setFactoryBeanName("org.springframework.cloud.netflix.eureka.config.DiscoveryClientOptionalArgsConfiguration$RestTemplateConfiguration");
      beanDefinition.setInstanceSupplier(getDefaultEurekaClientHttpRequestFactorySupplierInstanceSupplier());
      return beanDefinition;
    }

    /**
     * Get the bean instance supplier for 'restTemplateDiscoveryClientOptionalArgs'.
     */
    @SuppressWarnings("deprecation")
    private static BeanInstanceSupplier<RestTemplateDiscoveryClientOptionalArgs> getRestTemplateDiscoveryClientOptionalArgsInstanceSupplier(
        ) {
      return BeanInstanceSupplier.<RestTemplateDiscoveryClientOptionalArgs>forFactoryMethod(DiscoveryClientOptionalArgsConfiguration.RestTemplateConfiguration.class, "restTemplateDiscoveryClientOptionalArgs", TlsProperties.class, EurekaClientHttpRequestFactorySupplier.class, ObjectProvider.class)
              .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.netflix.eureka.config.DiscoveryClientOptionalArgsConfiguration$RestTemplateConfiguration", DiscoveryClientOptionalArgsConfiguration.RestTemplateConfiguration.class).restTemplateDiscoveryClientOptionalArgs(args.get(0), args.get(1), args.get(2)));
    }

    /**
     * Get the bean definition for 'restTemplateDiscoveryClientOptionalArgs'.
     */
    @SuppressWarnings("deprecation")
    public static BeanDefinition getRestTemplateDiscoveryClientOptionalArgsBeanDefinition() {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(RestTemplateDiscoveryClientOptionalArgs.class);
      beanDefinition.setFactoryBeanName("org.springframework.cloud.netflix.eureka.config.DiscoveryClientOptionalArgsConfiguration$RestTemplateConfiguration");
      beanDefinition.setInstanceSupplier(getRestTemplateDiscoveryClientOptionalArgsInstanceSupplier());
      return beanDefinition;
    }

    /**
     * Get the bean instance supplier for 'restTemplateTransportClientFactories'.
     */
    @SuppressWarnings({ "deprecation", "removal" })
    private static BeanInstanceSupplier<RestTemplateTransportClientFactories> getRestTemplateTransportClientFactoriesInstanceSupplier(
        ) {
      return BeanInstanceSupplier.<RestTemplateTransportClientFactories>forFactoryMethod(DiscoveryClientOptionalArgsConfiguration.RestTemplateConfiguration.class, "restTemplateTransportClientFactories", RestTemplateDiscoveryClientOptionalArgs.class)
              .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.netflix.eureka.config.DiscoveryClientOptionalArgsConfiguration$RestTemplateConfiguration", DiscoveryClientOptionalArgsConfiguration.RestTemplateConfiguration.class).restTemplateTransportClientFactories(args.get(0)));
    }

    /**
     * Get the bean definition for 'restTemplateTransportClientFactories'.
     */
    @SuppressWarnings("removal")
    public static BeanDefinition getRestTemplateTransportClientFactoriesBeanDefinition() {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(RestTemplateTransportClientFactories.class);
      beanDefinition.setFactoryBeanName("org.springframework.cloud.netflix.eureka.config.DiscoveryClientOptionalArgsConfiguration$RestTemplateConfiguration");
      beanDefinition.setInstanceSupplier(getRestTemplateTransportClientFactoriesInstanceSupplier());
      return beanDefinition;
    }
  }
}
