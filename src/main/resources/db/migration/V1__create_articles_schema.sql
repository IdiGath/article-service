CREATE TABLE IF NOT EXISTS public.articles (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    logo_url VARCHAR(255) NOT NULL,
    summary VARCHAR(500) NOT NULL,
    text_content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS public.articles_SEQ INCREMENT BY 50 OWNED BY public.articles.id;
ALTER TABLE public.articles ALTER COLUMN id SET DEFAULT nextval('public.articles_SEQ');

CREATE UNIQUE INDEX IF NOT EXISTS uq_articles_title_ci ON public.articles (lower(title));
