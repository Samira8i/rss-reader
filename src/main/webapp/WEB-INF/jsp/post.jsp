<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>${post.title} - RSS Reader</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<h1>${post.title != null ? post.title : "Пост без названия"}</h1>

<div class="menu">
    <a href="/">Главная</a> |
    <a href="/sources">Мои источники</a> |
    <a href="/feed">Моя лента</a> |
    <a href="/auth/logout">Выйти (${user.username})</a>
</div>

<div class="post-detail">
    <div class="post-meta">
        <span class="date">
            <c:choose>
                <c:when test="${post.publishedAt != null}">
                     Опубликовано: <spring:eval expression="post.publishedAt" />
                </c:when>
                <c:otherwise>
                     Дата не указана
                </c:otherwise>
            </c:choose>
        </span>
    </div>

    <div class="post-description">
        <c:if test="${post.description != null}">
            <p>${post.description}</p>
        </c:if>
        <c:if test="${post.description == null}">
            <p><em>Описание отсутствует</em></p>
        </c:if>
    </div>

    <div class="post-link">
        <a href="${post.link}" target="_blank" class="button">Читать оригинал на сайте</a>
    </div>

    <div class="back-link">
        <a href="/feed">← Вернуться к ленте</a>
    </div>
</div>
</body>
</html>