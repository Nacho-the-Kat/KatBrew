package com.nacho.demo;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Initialisiert die Servlets für die Web Services.
 */
public class ServletInitializer extends SpringBootServletInitializer {

    /**
     * Konfiguration für den Application Builder.
     *
     * @param application Der SpringApplicationBuilder.
     * @return Der geupdatete SpringApplicationBuilder.
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(KatBrewApplication.class);
    }

}
