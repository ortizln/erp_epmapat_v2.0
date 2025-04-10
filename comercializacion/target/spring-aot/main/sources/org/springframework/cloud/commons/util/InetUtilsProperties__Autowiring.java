package org.springframework.cloud.commons.util;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link InetUtilsProperties}.
 */
@Generated
public class InetUtilsProperties__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static InetUtilsProperties apply(RegisteredBean registeredBean,
      InetUtilsProperties instance) {
    AutowiredFieldValueResolver.forRequiredField("timeoutSeconds").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
