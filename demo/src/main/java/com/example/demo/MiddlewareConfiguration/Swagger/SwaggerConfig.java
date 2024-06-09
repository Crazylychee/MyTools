package com.example.demo.MiddlewareConfiguration.Swagger;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket createRestApi1(Environment environment) {
        return new Docket(DocumentationType.SWAGGER_2).groupName("登陆接口").apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("com.example.chater"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(" ")
                .termsOfServiceUrl("")
                .description(" ")
                .version("1.0")
                .build();
    }
}