package ru.itis.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Component
public class AllowedRssDomainValidator implements ConstraintValidator<AllowedRssDomain, String> {

    private static final List<String> ALLOWED_DOMAINS = Arrays.asList(
            "habr.com",
            "kommersant.ru",
            "lenta.ru",
            "rbc.ru",
            "bbc.com",
            "nytimes.com",
            "github.com",
            "medium.com"
    );

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        if (url == null || url.isEmpty()) {
            return true;
        }

        try {
            URI uri = new URI(url);
            String host = uri.getHost();

            if (host == null) {
                return false;
            }

            for (String allowedDomain : ALLOWED_DOMAINS) {
                if (host.equals(allowedDomain) || host.endsWith("." + allowedDomain)) {
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            return false;
        }
    }
}