CREATE TABLE IF NOT EXISTS public.tags (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS public.article_tags (
    article_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    CONSTRAINT fk_article
        FOREIGN KEY (article_id)
        REFERENCES public.articles(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_tags
        FOREIGN KEY (tag_id)
        REFERENCES public.tags(id)
        ON DELETE CASCADE
);
CREATE SEQUENCE IF NOT EXISTS public.tags_SEQ INCREMENT BY 50 OWNED BY public.tags.id;
ALTER TABLE public.tags ALTER COLUMN id SET DEFAULT nextval('public.tags_SEQ');
