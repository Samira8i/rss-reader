<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Мои RSS источники</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<h1>Мои RSS источники</h1>

<c:if test="${param.success == 'added'}">
    <div class="success"> Источник успешно добавлен!</div>
</c:if>
<c:if test="${param.success == 'deleted'}">
    <div class="success"> Источник удален!</div>
</c:if>

<div class="menu">
    <a href="/">Главная</a> |
    <a href="/sources">Мои источники</a> |
    <a href="/feed">Моя лента</a> |
    <a href="/auth/logout">Выйти (${user.username})</a>
</div>

<h2>Добавить новый источник</h2>

<form method="post" action="/sources/add" onsubmit="logFormSubmit();">
    <div class="form-group">
        <label for="name">Название:</label>
        <input type="text" id="name" name="name" value="${sourceForm.name}" required>
        <c:if test="${not empty bindingResult.getFieldError('name')}">
            <span class="error">${bindingResult.getFieldError('name').defaultMessage}</span>
        </c:if>
    </div>

    <div class="form-group">
        <label for="url">RSS URL:</label>
        <input type="text" id="url" name="url" value="${sourceForm.url}" required style="width: 100%;">
        <c:if test="${not empty bindingResult.getFieldError('url')}">
            <span class="error" style="color: red; display: block; margin-top: 5px;">
                 ${bindingResult.getFieldError('url').defaultMessage}
            </span>
        </c:if>
        <small>Пример: https://habr.com/ru/rss/articles/top/daily/?fl=ru</small>
    </div>

    <button type="submit">➕ Добавить</button>
</form>

<h2>Мои источники</h2>
<c:choose>
    <c:when test="${empty sources}">
        <p> У вас пока нет RSS источников. Добавьте первый выше!</p>
    </c:when>
    <c:otherwise>
        <table border="1" style="width: 100%; border-collapse: collapse;">
            <tr style="background-color: #f0f0f0;">
                <th>ID</th>
                <th>Название</th>
                <th>URL</th>
                <th>Добавлен</th>
                <th>Последняя проверка</th>
                <th>Действия</th>
            </tr>
            <c:forEach items="${sources}" var="source">
                <tr>
                    <td>${source.id}</td>
                    <td>${source.name}</td>
                    <td><a href="${source.url}" target="_blank">${source.url}</a></td>
                    <td>${source.createdAt}</td>
                    <td>${source.lastCheckedAt != null ? source.lastCheckedAt : "ещё не проверялся"}</td>
                    <td>
                        <form method="post" action="/sources/${source.id}/delete" style="display:inline;" onsubmit="return confirm('Удалить источник?');">
                            <button type="submit" style="background-color: #cc0000;">🗑️ Удалить</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>
</body>
</html>