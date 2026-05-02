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
        .post-meta span {
            margin-right: 15px;
        }
        .read-status {
            font-weight: bold;
        }
        .read-status.read {
            color: green;
        }
        .read-status.unread {
            color: blue;
        }
        .loading {
            text-align: center;
            padding: 20px;
            display: none;
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

<div id="posts-container"></div>

<div id="loading" class="loading">Загрузка...</div>

<div id="sentinel" style="height: 20px;"></div>

<script src="/js/feed.js"></script>
</body>
</html>