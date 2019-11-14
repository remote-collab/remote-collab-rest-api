package com.bmw.remotecollab.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket apiV1() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("API Version 1")
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("com.bmw.remotecollab"))
                .paths(PathSelectors.ant("/api/v1/**"))
                .build()
                .apiInfo(apiInfo());
    }

    @Bean
    public Docket apiV2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("API Version 2")
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("com.bmw.remotecollab"))
                .paths(PathSelectors.ant("/api/v2/**"))
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Viper Remote Collaboration REST API")
                .description(
                        "This REST API is used to manage Remote Collaboration Sessions (called Rooms).\n\n" +
                                "Creation of such rooms and invitation of users therein.")
                .build();
    }
}
