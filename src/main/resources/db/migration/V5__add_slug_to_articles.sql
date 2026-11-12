-- URL-friendly unique identifier for articles, addressed in REST paths as /api/articles/{slug}.
ALTER TABLE public.articles ADD COLUMN IF NOT EXISTS slug VARCHAR(255);

-- Backfill: slugify existing titles (lowercase, non-alphanumeric runs become '-', trimmed).
-- Titles consisting only of special characters fall back to 'article-<id>'.
UPDATE public.articles
SET slug = CASE
    WHEN btrim(regexp_replace(lower(title), '[^[:alnum:]]+', '-', 'g'), '-') = ''
        THEN 'article-' || id::text
    ELSE btrim(regexp_replace(lower(title), '[^[:alnum:]]+', '-', 'g'), '-')
END;

-- Dedupe collisions (e.g. 'My Title' and 'my-title' both slugify to 'my-title') with a numeric suffix.
WITH ranked AS (
    SELECT id, slug, row_number() OVER (PARTITION BY slug ORDER BY id) AS rn
    FROM public.articles
)
UPDATE public.articles a
SET slug = a.slug || '-' || r.rn
FROM ranked r
WHERE a.id = r.id AND r.rn > 1;

ALTER TABLE public.articles ALTER COLUMN slug SET NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_articles_slug ON public.articles (slug);
