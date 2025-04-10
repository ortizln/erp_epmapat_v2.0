package org.springframework.cloud.autoconfigure;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cloud.context.refresh.ConfigDataContextRefresher;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.context.refresh.RefreshScopeLifecycle;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.cloud.endpoint.event.RefreshEventListener;
import org.springframework.cloud.logging.LoggingRebinder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Bean definitions for {@link RefreshAutoConfiguration}.
 */
@Generated
public class RefreshAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'refreshAutoConfiguration'.
   */
  public static BeanDefinition getRefreshAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(RefreshAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(RefreshAutoConfiguration::new);
    return beanDefinition;
  }

  /**
   * Get the bean definition for 'refreshScope'.
   */
  public static BeanDefinition getRefreshScopeBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(RefreshAutoConfiguration.class);
    beanDefinition.setTargetType(RefreshScope.class);
    beanDefinition.setInstanceSupplier(BeanInstanceSupplier.<RefreshScope>forFactoryMethod(RefreshAutoConfiguration.class, "refreshScope").withGenerator((registeredBean) -> RefreshAutoConfiguration.refreshScope()));
    return beanDefinition;
  }

  /**
   * Get the bean definition for 'loggingRebinder'.
   */
  public static BeanDefinition getLoggingRebinderBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(RefreshAutoConfiguration.class);
    beanDefinition.setTargetType(LoggingRebinder.class);
    beanDefinition.setInstanceSupplier(BeanInstanceSupplier.<LoggingRebinder>forFactoryMethod(RefreshAutoConfiguration.class, "loggingRebinder").withGenerator((registeredBean) -> RefreshAutoConfiguration.loggingRebinder()));
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'configDataContextRefresher'.
   */
  private static BeanInstanceSupplier<ConfigDataContextRefresher> getConfigDataContextRefresherInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<ConfigDataContextRefresher>forFactoryMethod(RefreshAutoConfiguration.class, "configDataContextRefresher", ConfigurableApplicationContext.class, RefreshScope.class, RefreshAutoConfiguration.RefreshProperties.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.autoconfigure.RefreshAutoConfiguration", RefreshAutoConfiguration.class).configDataContextRefresher(args.get(0), args.get(1), args.get(2)));
  }

  /**
   * Get the bean definition for 'configDataContextRefresher'.
   */
  public static BeanDefinition getConfigDataContextRefresherBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigDataContextRefresher.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.autoconfigure.RefreshAutoConfiguration");
    beanDefinition.setInstanceSupplier(getConfigDataContextRefresherInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'refreshScopeLifecycle'.
   */
  private static BeanInstanceSupplier<RefreshScopeLifecycle> getRefreshScopeLifecycleInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<RefreshScopeLifecycle>forFactoryMethod(RefreshAutoConfiguration.class, "refreshScopeLifecycle", ContextRefresher.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.autoconfigure.RefreshAutoConfiguration", RefreshAutoConfiguration.class).refreshScopeLifecycle(args.get(0)));
  }

  /**
   * Get the bean definition for 'refreshScopeLifecycle'.
   */
  public static BeanDefinition getRefreshScopeLifecycleBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(RefreshScopeLifecycle.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.autoconfigure.RefreshAutoConfiguration");
    beanDefinition.setInstanceSupplier(getRefreshScopeLifecycleInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'refreshEventListener'.
   */
  private static BeanInstanceSupplier<RefreshEventListener> getRefreshEventListenerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<RefreshEventListener>forFactoryMethod(RefreshAutoConfiguration.class, "refreshEventListener", ContextRefresher.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.cloud.autoconfigure.RefreshAutoConfiguration", RefreshAutoConfiguration.class).refreshEventListener(args.get(0)));
  }

  /**
   * Get the bean definition for 'refreshEventListener'.
   */
  public static BeanDefinition getRefreshEventListenerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(RefreshEventListener.class);
    beanDefinition.setFactoryBeanName("org.springframework.cloud.autoconfigure.RefreshAutoConfiguration");
    beanDefinition.setInstanceSupplier(getRefreshEventListenerInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Bean definitions for {@link RefreshAutoConfiguration.RefreshProperties}.
   */
  @Generated
  public static class RefreshProperties {
    /**
     * Get the bean definition for 'refreshProperties'.
     */
    public static BeanDefinition getRefreshPropertiesBeanDefinition() {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(RefreshAutoConfiguration.RefreshProperties.class);
      beanDefinition.setInstanceSupplier(RefreshAutoConfiguration.RefreshProperties::new);
      return beanDefinition;
    }
  }

  /**
   * Bean definitions for {@link RefreshAutoConfiguration.RefreshScopeBeanDefinitionEnhancer}.
   */
  @Generated
  public static class RefreshScopeBeanDefinitionEnhancer {
    /**
     * Get the bean definition for 'refreshScopeBeanDefinitionEnhancer'.
     */
    public static BeanDefinition getRefreshScopeBeanDefinitionEnhancerBeanDefinition() {
      RootBeanDefinition beanDefinition = new RootBeanDefinition(RefreshAutoConfiguration.RefreshScopeBeanDefinitionEnhancer.class);
      beanDefinition.setInstanceSupplier(RefreshAutoConfiguration.RefreshScopeBeanDefinitionEnhancer::new);
      return beanDefinition;
    }
  }
}
