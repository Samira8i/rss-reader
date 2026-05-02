<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Вход - RSS Reader</title>
    <link rel="stylesheet" href="/css/style.css">
    <style>
        .oauth-login {
            margin-top: 20px;
            text-align: center;
            border-top: 1px solid #ddd;
            padding-top: 20px;
        }
        .github-btn {
            background-color: #24292e;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            text-decoration: none;
            display: inline-block;
        }
        .github-btn:hover {
            background-color: #444;
        }
        .success {
            color: green;
            background-color: #dff0d8;
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 15px;
        }
        .error {
            color: red;
            background-color: #f2dede;
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 15px;
        }
    </style>
</head>
<body>
<h1>Вход в RSS Reader</h1>

<c:if test="${param.logout != null}">
    <div class="success">Вы успешно вышли из системы.</div>
</c:if>

<c:if test="${not empty error}">
    <div class="error">${error}</div>
</c:if>

<div class="oauth-login">
    <a href="/oauth2/authorization/github" class="github-btn">
        Войти через GitHub
    </a>
</div>

<hr>

<form method="post" action="/auth/login">
    <div class="form-group">
        <label for="username">Имя пользователя:</label>
        <input type="text" id="username" name="username" required>
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