package org.springframework.cloud.client.discovery.simple;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredMethodArgumentsResolver;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.commons.util.InetUtils;

/**
 * Autowiring for {@link SimpleDiscoveryClientAutoConfiguration}.
 */
@Generated
public class SimpleDiscoveryClientAutoConfiguration__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static SimpleDiscoveryClientAutoConfiguration apply(RegisteredBean registeredBean,
      SimpleDiscoveryClientAutoConfiguration instance) {
    AutowiredMethodArgumentsResolver.forMethod("setServer", ServerProperties.class).resolve(registeredBean, args -> instance.setServer(args.get(0)));
    AutowiredMethodArgumentsResolver.forRequiredMethod("setInet", InetUtils.class).resolve(registeredBean, args -> instance.setInet(args.get(0)));
    return instance;
  }
}
