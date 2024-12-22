package com.nacho.core.security;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguration für den Websocket der Anwendung.
 */
@ComponentScan
@Configuration
@EnableAutoConfiguration
public class WebsocketSourceConfiguration {

    /**
     * Tomcat Servlet Container für Redirekts.
     *
     * @return Konfigurierten Tomcat Servlet Container.
     */
    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        final TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {

            /**
             * Context, der nach der Url Verarbeitung getriggert wird.
             *
             * @param context Der Application Context.
             */
            @Override
            protected void postProcessContext(Context context) {
                final SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };

        tomcat.addAdditionalTomcatConnectors(createHttpConnector());
        return tomcat;
    }

    /**
     * Erstellt den Connector, für Http, welcher auf Https redirekted.
     *
     * @return Der Connector für Http.
     */
    private Connector createHttpConnector() {
        Connector connector =
                new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setSecure(false);
        connector.setPort(80);
        connector.setRedirectPort(443);
        return connector;
    }
}
