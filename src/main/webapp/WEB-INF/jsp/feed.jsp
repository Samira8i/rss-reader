<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Моя лента - RSS Reader</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<h1>Моя лента</h1>

<div class="menu">
    <a href="/">Главная</a> |
    <a href="/sources">Мои источники</a> |
    <a href="/feed">Моя лента</a> |
    <a href="/auth/logout">Выйти (${user.username})</a>
</div>

<!-- Фильтрация по статусу -->
<div>
    <a href="/feed">Все посты</a> |
    <a href="/feed?status=read">Прочитанные</a> |
    <a href="/feed?status=unread">Непрочитанные</a>
</div>

<c:if test="${empty posts}">
    <p>У вас пока нет постов. Добавьте RSS источники на странице <a href="/sources">Мои источники</a></p>
</c:if>

<c:if test="${not empty posts}">
    <div class="posts">
        <c:forEach items="${posts}" var="post">
            <div class="post-item">
                <h3>
                    <a href="/feed/post/${post.id}">${post.title != null ? post.title : "Без названия"}</a>
                </h3>
                <div class="post-meta">
                    <span>Источник: ${post.source.name}</span>
                    <span>
                        <c:choose>
                            <c:when test="${post.read}">
                                 Прочитано
                            </c:when>
                            <c:otherwise>
                                 Новое
                            </c:otherwise>
                        </c:choose>
                    </span>
                    <span>
                        <c:choose>
                            <c:when test="${post.publishedAt != null}">
                                 <spring:eval expression="post.publishedAt" />
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
                    <a href="${post.link}" target="_blank">Читать оригинал →</a>
                </div>
                <hr>
            </div>
        </c:forEach>
    </div>

    <!-- Пагинация -->
    <div>
        <c:if test="${hasPrev}">
            <a href="/feed?page=${currentPage - 1}&status=${statusParam}">← Предыдущая</a>
        </c:if>

        <span>Страница ${currentPage + 1} из ${totalPages}</span>

        <c:if test="${hasNext}">
            <a href="/feed?page=${currentPage + 1}&status=${statusParam}">Следующая →</a>
        </c:if>
    </div>
</c:if>
</body>
</html>