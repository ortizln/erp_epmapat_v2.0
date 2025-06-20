package org.springframework.cloud.config.server.config;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link DefaultTextEncryptionAutoConfiguration}.
 */
@Generated
public class DefaultTextEncryptionAutoConfiguration__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static DefaultTextEncryptionAutoConfiguration apply(RegisteredBean registeredBean,
      DefaultTextEncryptionAutoConfiguration instance) {
    instance.context = AutowiredFieldValueResolver.forRequiredField("context").resolve(registeredBean);
    return instance;
  }
}
