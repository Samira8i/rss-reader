package ru.itis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.itis.validator.AllowedRssDomain;

public class RssSourceForm {

    @NotBlank(message = "Название не может быть пустым")
    @Size(min = 3, max = 100, message = "Название должно быть от 3 до 100 символов")
    private String name;

    @NotBlank(message = "URL не может быть пустым")
    @Pattern(regexp = "^https?://.+", message = "URL должен начинаться с http:// или https://")
    @AllowedRssDomain(message = "Разрешены только RSS ленты с доменов: habr.com, kommersant.ru, lenta.ru, rbc.ru")
    private String url;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "RssSourceForm{name='" + name + "', url='" + url + "'}";
    }
}