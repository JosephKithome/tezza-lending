package com.tezza.lending.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI lendingOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Tezza Lending Application API")
                .version("v1")
                .description("Loan products, customer limits, loan disbursement, repayments, overdue sweeps, and notifications.")
                .contact(new Contact().name("Tezza Case Study"))
                .license(new License().name("Case Study")));
    }
}
