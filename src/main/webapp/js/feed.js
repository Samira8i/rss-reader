let currentPage = 0;
let isLoading = false;
let hasMore = true;
let pageSize = 10;

function loadMorePosts() {
    if (isLoading || !hasMore) return;

    isLoading = true;
    document.getElementById('loading').style.display = 'block';

    let url = '/api/feed?page=' + currentPage + '&size=' + pageSize;

    console.log('Загружаем URL:', url);

    fetch(url)
        .then(function(response) {
            if (!response.ok) {
                throw new Error('Ошибка ' + response.status);
            }
            return response.json();
        })
        .then(function(data) {
            console.log('Получено постов:', data.posts.length, 'hasMore:', data.hasMore);
            renderPosts(data.posts);
            hasMore = data.hasMore;
            currentPage++;
            isLoading = false;
            document.getElementById('loading').style.display = 'none';
        })
        .catch(function(error) {
            console.error('Ошибка загрузки:', error);
            isLoading = false;
            document.getElementById('loading').style.display = 'none';
        });
}

function escapeHtml(text) {
    if (!text) return '';
    var div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function renderPosts(posts) {
    var container = document.getElementById('posts-container');

    posts.forEach(function(post) {
        var postDate = post.publishedAt ? new Date(post.publishedAt).toLocaleString() : 'Дата не указана';
        var title = escapeHtml(post.title) || 'Без названия';
        var sourceName = escapeHtml(post.sourceName) || 'Неизвестный источник';
        var description = post.description || 'Описание отсутствует';

        if (description.length > 500) {
            description = description.substring(0, 500) + '...';
        }

        var statusText = post.read ? 'Прочитано' : 'Новое';
        var statusClass = post.read ? 'read' : 'unread';

        container.innerHTML += `
            <div class="post-item">
                <h3><a href="/feed/post/${post.id}">${title}</a></h3>
                <div class="post-meta">
                    <span>${sourceName}</span>
                    <span class="read-status ${statusClass}">${statusText}</span>
                    <span> ${postDate}</span>
                </div>
                <div class="post-description">${description}</div>
                <div class="post-link"><a href="${post.link}" target="_blank">Читать оригинал →</a></div>
                <hr>
            </div>
        `;
    });
}

document.addEventListener('DOMContentLoaded', function() {
    var sentinel = document.getElementById('sentinel');
    if (sentinel) {
        var observer = new IntersectionObserver(function(entries) {
            if (entries[0].isIntersecting) {
                loadMorePosts();
            }
        }, {
            rootMargin: '100px'
        });
        observer.observe(sentinel);
    }
    loadMorePosts();
});