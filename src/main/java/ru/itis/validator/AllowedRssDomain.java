package ru.itis.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD}) // куда применяем аннотацию
@Retention(RetentionPolicy.RUNTIME) // когда доступна
@Constraint(validatedBy = AllowedRssDomainValidator.class) // какой класс выполняет
public @interface AllowedRssDomain {
    String message() default "RSS ссылка должна вести на разрешенный домен (habr.com, kommersant.ru, etc.)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}