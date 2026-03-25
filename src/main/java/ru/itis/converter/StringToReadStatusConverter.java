package ru.itis.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToReadStatusConverter implements Converter<String, Boolean> {
// странный конвертер, но тз надо было выполнить (
    @Override
    public Boolean convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        // "read" → true, "unread" → false
        if ("read".equalsIgnoreCase(source)) {
            return true;
        }
        if ("unread".equalsIgnoreCase(source)) {
            return false;
        }
        return null;
    }
}