package ru.itis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import ru.itis.converter.StringToReadStatusConverter;
import ru.itis.formatter.LocalDateTimeFormatter;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ru.itis.controller",
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                value = Controller.class
        ))
public class MvcConfigurer implements WebMvcConfigurer {

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
    }

    @Autowired
    private LocalDateTimeFormatter localDateTimeFormatter;

    @Autowired
    private StringToReadStatusConverter readStatusConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // Регистрирую конвертер для статуса (read/unread → Boolean)
        registry.addConverter(readStatusConverter);

        // Регистрирую форматтер для отображения LocalDateTime в JSP
        registry.addFormatter(localDateTimeFormatter);
    }
}