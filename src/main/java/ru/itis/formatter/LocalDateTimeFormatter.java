package ru.itis.formatter;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
// использую в jsp для вывода даты в <spring:eval expression="post.publishedAt" />
// спринг сам подключает мой форматтер и все красиво выводит
// В результате пользователь видит: 25.03.2024 14:30 вместо 2024-03-25T14:30:00
@Component
public class LocalDateTimeFormatter implements Formatter<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public LocalDateTime parse(String text, Locale locale) throws ParseException {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(text, FORMATTER);
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    @Override
    public String print(LocalDateTime object, Locale locale) {
        if (object == null) {
            return "";
        }
        return object.format(FORMATTER);
    }
}