<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>RSS Reader - Главная</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<h1>RSS Reader</h1>

<div class="menu">
    <c:if test="${empty user}">
        <a href="/auth/login">Войти</a> |
        <a href="/auth/register">Регистрация</a>
    </c:if>
    <c:if test="${not empty user}">
        <a href="/">Главная</a> |
        <a href="/sources">Мои источники (${user.username})</a> |
        <a href="/feed">Моя лента</a> |
        <a href="/auth/logout">Выйти</a>
    </c:if>
</div>

<div class="content">
    <p>Добро пожаловать в RSS Reader!</p>
    <p>Здесь вы можете добавлять RSS источники и читать новости в удобном формате.</p>
</div>
</body>
</html>