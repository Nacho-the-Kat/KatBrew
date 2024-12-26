package com.katbrew.demo.beans;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.*;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Registriert beim Serverstart Beans, damit diese durch Injection geladen werden können.
 */
@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {


    @Value("${database.with-output-mapping}")
    private boolean withOutputMapping;


    private final DataSource dataSource;


    @Bean
    public org.jooq.Configuration configuration() {

        if (withOutputMapping) {
            final Settings settings = new Settings()
                    .withRenderMapping(new RenderMapping()
                            .withSchemata(
                                    new MappedSchema().withInput("krc20backend")
                                            .withOutput("krc20backend")));

            return new DefaultConfiguration()
                    .set(dataSource)
                    .set(SQLDialect.POSTGRES)
                    .set(settings);
        } else {
            return new DefaultConfiguration().set(dataSource).set(SQLDialect.POSTGRES);
        }
    }
//    @Bean
//    public DSLContext dslContext() {
//        return configuration().dsl();
//    }
}
