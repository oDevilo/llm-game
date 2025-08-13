package io.github.devil.llm.avalon;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Devil
 */
@SpringBootApplication
@EnableTransactionManagement
@EntityScan(basePackages = "io.github.devil.llm.avalon.dao.entity")
@EnableJpaRepositories(value = {"io.github.devil.llm.avalon.dao.repository"})
public class Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
            .web(WebApplicationType.SERVLET)
            .sources(Application.class)
            .build()
            .run(args);
    }
}
