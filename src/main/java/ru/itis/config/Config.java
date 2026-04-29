package ru.itis.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("ru.itis")
@PropertySource("classpath:application.properties")
@Import({JpaConfig.class, LiquibaseConfig.class})
public class Config {
}