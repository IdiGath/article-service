CREATE TABLE IF NOT EXISTS public.sources (
    id BIGINT PRIMARY KEY,
    article_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL,
    citation TEXT NOT NULL,
    CONSTRAINT fk_article
        FOREIGN KEY (article_id)
        REFERENCES public.articles(id)
        ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS public.sources_SEQ INCREMENT BY 50 OWNED BY public.sources.id;
ALTER TABLE public.sources ALTER COLUMN id SET DEFAULT nextval('public.sources_SEQ');
