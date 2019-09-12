package com.multicert.project.v2x.pkimanager.config;

import com.google.common.base.Predicates;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
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
@ComponentScan("com.multicert.project.v2x.pkimanager.api.rest")
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.multicert.project.v2x.pkimanager.api.rest"))
                .paths(Predicates.not(PathSelectors.regex("/error")))
                .build()
                .apiInfo(apiInfo());
    }
    

    private ApiInfo apiInfo() {
        String description = "The RAservice is responsible for handling the request for enrollment and authorization certificates ";
        return new ApiInfoBuilder()
                .title("RA Service")
                .description(description)
                .termsOfServiceUrl("github")
                .license("")
                .licenseUrl("")
                .version("1.0")
                .build();
    }

}
