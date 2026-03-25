package ru.itis.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

//сканирую все кроме контроллеров (они отдельно, а то что-то с ними ничего не вышло)
@Configuration
@ComponentScan(basePackages = "ru.itis",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                value = Controller.class
        ))
@PropertySource("classpath:application.properties")
public class Config {
}