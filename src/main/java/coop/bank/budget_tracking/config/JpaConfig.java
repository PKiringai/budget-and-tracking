package coop.bank.budget_tracking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.OffsetDateTime;
import java.util.Optional;

@Configuration
//@EnableJpaRepositories(basePackages = "ke.co.coop.budget.repository")
@EnableTransactionManagement
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")

public class JpaConfig {
    @Bean(name = "auditingDateTimeProvider")
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
    }

}
