package com.erp.config;

import java.lang.Override;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.Environment;

/**
 * {@link ApplicationContextInitializer} to restore an application context based on previous AOT processing.
 */
@Generated
public class ConfigApplication__ApplicationContextInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
  @Override
  public void initialize(GenericApplicationContext applicationContext) {
    DefaultListableBeanFactory beanFactory = applicationContext.getDefaultListableBeanFactory();
    beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
    beanFactory.setDependencyComparator(AnnotationAwareOrderComparator.INSTANCE);
    registerCompositeEnvironmentRepositoryPropertiesBeanDefinitions(beanFactory, applicationContext.getEnvironment());
    new ConfigApplication__BeanFactoryRegistrations().registerBeanDefinitions(beanFactory);
    new ConfigApplication__BeanFactoryRegistrations().registerAliases(beanFactory);
  }

  /**
   * Register composite environment repository bean definitions for composite config data sources.
   */
  public void registerCompositeEnvironmentRepositoryPropertiesBeanDefinitions(
      DefaultListableBeanFactory beanFactory, Environment environment) {
    Binder binder = Binder.get(environment);
  }
}
