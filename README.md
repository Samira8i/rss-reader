# Запуск
mvn clean package
docker-compose down
docker-compose up --buil

# Таблицы
CREATE TABLE users (
id BIGSERIAL PRIMARY KEY,                    -- Уникальный идентификатор пользователя
username VARCHAR(50) UNIQUE NOT NULL,        -- Имя пользователя (уникальное)
password_hash VARCHAR(255) NOT NULL,         -- Хэш пароля 
salt VARCHAR(255) NOT NULL,                  -- Соль для хэширования пароля
created_at TIMESTAMP NOT NULL                -- Дата регистрации
);

CREATE TABLE sessions (
id BIGSERIAL PRIMARY KEY,                    -- Уникальный идентификатор сессии
user_id BIGINT NOT NULL,                     -- ID пользователя
session_id VARCHAR(255) UNIQUE NOT NULL,     -- UUID сессии (хранится в cookie)
created_at TIMESTAMP NOT NULL,               -- Дата создания сессии
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE rss_sources (
id BIGSERIAL PRIMARY KEY,                    -- Уникальный идентификатор источника
user_id BIGINT NOT NULL,                     -- ID пользователя, владельца источника
name VARCHAR(100) NOT NULL,                  -- Название источника (задает пользователь)
url TEXT NOT NULL,                           -- URL RSS ленты
created_at TIMESTAMP NOT NULL,               -- Дата добавления источника
last_checked_at TIMESTAMP,                   -- Время последней проверки обновлений
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
UNIQUE(user_id, url)                         -- Один URL не может быть добавлен дважды одним пользователем
);

CREATE TABLE posts (
id BIGSERIAL PRIMARY KEY,                    -- Уникальный идентификатор поста
source_id BIGINT NOT NULL,                   -- ID источника RSS
title VARCHAR(500),                          -- Заголовок поста
description TEXT,                            -- Описание/содержание поста
link VARCHAR(500),                           -- Ссылка на оригинальную статью
guid VARCHAR(500) NOT NULL,                  -- Уникальный идентификатор поста в RSS ленте
published_at TIMESTAMP,                      -- Дата публикации (из RSS)
created_at TIMESTAMP NOT NULL,               -- Дата сохранения в БД
FOREIGN KEY (source_id) REFERENCES rss_sources(id) ON DELETE CASCADE,
UNIQUE(source_id, guid)                      -- Один пост не может быть добавлен дважды из одного источника
);

-- Для быстрого поиска постов по источнику
CREATE INDEX idx_posts_source_id ON posts(source_id);

-- Для сортировки по дате публикации
CREATE INDEX idx_posts_published_at ON posts(published_at DESC);

-- Для быстрого поиска источников пользователя
CREATE INDEX idx_rss_sources_user_id ON rss_sources(user_id);

-- Для поиска сессий
CREATE INDEX idx_sessions_user_id ON sessions(user_id);
CREATE INDEX idx_sessions_session_id ON sessions(session_id);
