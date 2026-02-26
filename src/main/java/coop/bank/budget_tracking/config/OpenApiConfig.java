package coop.bank.budget_tracking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger Configuration
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Budget & Expense Tracker API")
                        .version("1.0.0")
                        .description("API for managing customer budgets and tracking expenses")
                        .contact(new Contact()
                                .name("Co-operative Bank ICT")
                                .email("ict@co-opbank.co.ke")
                        )
                        .license(new License()
                                .name("Proprietary")
                                .url("https://www.co-opbank.co.ke")
                        )
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server")
//                        new Server()
//                                .url("https://api.co-opbank.co.ke")
//                                .description("Production Server")
                ));
    }
}
