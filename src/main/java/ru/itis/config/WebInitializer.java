package ru.itis.config;

import jakarta.servlet.Filter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{Config.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{MvcConfigurer.class};
    }

    //по кривому просто на слеше / - уже в семестровке буду делать как Ференец просит
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
    @Override
    protected Filter[] getServletFilters() {
        // Фильтр для кодировки (UTF-8)
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter("UTF-8", true);

        // DelegatingFilterProxy — специальный фильтр, который делегирует вызовы
        // Spring бину с именем "authFilter". Spring сам создаст и настроит фильтр - без него не получилось
        DelegatingFilterProxy authFilterProxy = new DelegatingFilterProxy("authFilter");

        return new Filter[]{encodingFilter, authFilterProxy};
    }
}