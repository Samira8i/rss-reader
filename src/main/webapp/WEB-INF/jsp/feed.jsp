<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Моя лента - RSS Reader</title>
    <link rel="stylesheet" href="/css/style.css">
    <style>
        .post-item {
            border: 1px solid #ddd;
            padding: 15px;
            margin-bottom: 15px;
            border-radius: 5px;
        }
        .post-meta {
            color: #666;
            font-size: 14px;
            margin-bottom: 10px;
        }
        .loading {
            text-align: center;
            padding: 20px;
            display: none;
        }
        .filters {
            margin-bottom: 20px;
        }
        .filters a {
            margin-right: 10px;
            cursor: pointer;
            color: #007bff;
            text-decoration: none;
        }
        .filters a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<h1>Моя лента</h1>

<div class="menu">
    <a href="/">Главная</a> |
    <a href="/sources">Мои источники</a> |
    <a href="/feed">Моя лента</a> |
    <a href="/auth/logout">Выйти (${user.username})</a>
</div>

<!-- Фильтрация -->
<div class="filters">
    <a onclick="resetFeed(null)">📋 Все посты</a> |
    <a onclick="resetFeed('true')">✅ Прочитанные</a> |
    <a onclick="resetFeed('false')">🔵 Непрочитанные</a>
</div>

<!-- Контейнер для постов -->
<div id="posts-container"></div>

<!-- Индикатор загрузки -->
<div id="loading" class="loading">⏳ Загрузка...</div>

<!-- Маяк для определения прокрутки до конца -->
<div id="sentinel" style="height: 20px;"></div>

<!-- Подключаем JavaScript -->
<script src="/js/feed.js"></script>
</body>
</html>