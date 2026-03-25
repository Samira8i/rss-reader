<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Регистрация - RSS Reader</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<h1>Регистрация</h1>

<c:if test="${not empty error}">
    <div class="error">${error}</div>
</c:if>

<form method="post" action="/auth/register">
    <div class="form-group">
        <label for="username">Имя пользователя:</label>
        <input type="text" id="username" name="username" value="${signUpForm.username}" required>
        <c:if test="${not empty bindingResult.getFieldError('username')}">
            <span class="error">${bindingResult.getFieldError('username').defaultMessage}</span>
        </c:if>
    </div>

    <div class="form-group">
        <label for="password">Пароль:</label>
        <input type="password" id="password" name="password" required>
        <c:if test="${not empty bindingResult.getFieldError('password')}">
            <span class="error">${bindingResult.getFieldError('password').defaultMessage}</span>
        </c:if>
    </div>

    <button type="submit">Зарегистрироваться</button>
</form>

<p>Уже есть аккаунт? <a href="/auth/login">Войти</a></p>
</body>
</html>