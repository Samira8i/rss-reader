<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Вход - RSS Reader</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<h1>Вход</h1>

<c:if test="${param.registered}">
    <div class="success">Регистрация успешна! Войдите в систему.</div>
</c:if>

<c:if test="${not empty error}">
    <div class="error">${error}</div>
</c:if>

<form method="post" action="/auth/login">
    <div class="form-group">
        <label for="username">Имя пользователя:</label>
        <input type="text" id="username" name="username" value="${signInForm.username}" required>
    </div>

    <div class="form-group">
        <label for="password">Пароль:</label>
        <input type="password" id="password" name="password" required>
    </div>

    <button type="submit">Войти</button>
</form>

<p>Нет аккаунта? <a href="/auth/register">Зарегистрироваться</a></p>
</body>
</html>