
-- Insert tags first (since article_tags depends on them)
INSERT INTO public.tags (name) VALUES
('Java'), ('Microservices'), ('NixOS'), ('Quarkus'), ('Spring Boot'),
('PostgreSQL'), ('Database'), ('DevOps'), ('Cloud'), ('Performance')
ON CONFLICT (name) DO NOTHING;

-- Insert articles
INSERT INTO public.articles (id, title, logo_url, summary, text_content, created_at, updated_at) VALUES
(51, 'Introduction to Microservices: Pros and Cons', 'https://example.com/logos/microservices.png',
'An overview of microservices architecture: advantages, disadvantages, and use cases.',
'# Introduction to Microservices\n\nMicroservices are an approach to software development where an application is divided into small, independent services.\n\n## Pros\n- Scalability\n- Flexibility in development\n- Resilience to failures\n\n## Cons\n- Complexity in management\n- Transaction challenges\n- Increased network load',
'2026-04-01 10:00:00', '2026-04-01 10:00:00'),

(101, 'Java 21: New Features', 'https://example.com/logos/java21.png',
'An overview of new features in Java 21: virtual threads, string templates, and sequential collections.',
'# Java 21: New Features\n\nJava 21 brings many improvements, including:\n\n## Virtual Threads\n- Simplify multithreading.\n\n## String Templates\n- Allow easier string formatting.\n\n## Sequential Collections\n- New interfaces for working with collections.',
'2026-04-02 11:00:00', '2026-04-02 11:00:00'),

(151, 'NixOS for Java Developers', 'https://example.com/logos/nixos-java.png',
'How to set up a Java development environment in NixOS: tools, configurations, and tips.',
'# NixOS for Java Developers\n\nNixOS is a Linux distribution that uses a declarative approach to system configuration.\n\n## Installing JDK\n```bash\nnix-env -iA nixos.jdk21\n```\n\n## IDE Setup\n- Use `nixvim` for Neovim integration.\n- Configure the environment via `shell.nix`.',
'2026-04-03 12:00:00', '2026-04-03 12:00:00'),

(201, 'Quarkus vs Spring Boot: Comparison', 'https://example.com/logos/quarkus-vs-spring.png',
'A comparison of Quarkus and Spring Boot frameworks: performance, ecosystem, and use cases.',
'# Quarkus vs Spring Boot\n\n## Performance\n- Quarkus: Fast startup, low memory consumption.\n- Spring Boot: Mature ecosystem, but heavier.\n\n## Ecosystem\n- Quarkus: Optimized for cloud applications.\n- Spring Boot: Large community and many libraries.',
'2026-04-04 13:00:00', '2026-04-04 13:00:00'),

(251, 'PostgreSQL: Query Optimization', 'https://example.com/logos/postgresql.png',
'Tips for optimizing queries in PostgreSQL: indexes, execution plans, and performance analysis.',
'# PostgreSQL: Query Optimization\n\n## Indexes\n- Create indexes for frequently used fields.\n\n## Execution Plans\n- Use `EXPLAIN ANALYZE` to analyze queries.\n\n## Monitoring\n- Set up `pg_stat_statements` to track slow queries.',
'2026-04-05 14:00:00', '2026-04-05 14:00:00');


INSERT INTO public.sources (article_id, title, url, citation) VALUES
(51, 'Microservices.io', 'https://microservices.io/', 'Chris Richardson, Microservices.io, 2023'),
(51, 'Martin Fowler: Microservices', 'https://martinfowler.com/articles/microservices.html', 'Martin Fowler, Microservices, 2014'),
(101, 'Oracle: Java 21 Release Notes', 'https://www.oracle.com/java/technologies/javase/21-relnote-issues.html', 'Oracle, Java 21 Release Notes, 2023'),
(101, 'Java 21 Features - Baeldung', 'https://www.baeldung.com/java-21-features', 'Baeldung, Java 21 Features, 2023'),
(151, 'NixOS Manual', 'https://nixos.org/manual/', 'NixOS Contributors, NixOS Manual, 2024'),
(151, 'Nixvim GitHub', 'https://github.com/nix-community/nixvim', 'Nix Community, Nixvim, 2024'),
(201, 'Quarkus Official Guide', 'https://quarkus.io/guides/', 'Red Hat, Quarkus Guides, 2024'),
(201, 'Spring Boot Documentation', 'https://spring.io/projects/spring-boot', 'VMware, Spring Boot Docs, 2024'),
(251, 'PostgreSQL Official Docs', 'https://www.postgresql.org/docs/', 'PostgreSQL Global Development Group, PostgreSQL Docs, 2024'),
(251, 'Use The Index, Luke', 'https://use-the-index-luke.com/', 'Markus Winand, Use The Index, Luke, 2023');

-- Insert article_tags (links between articles and tags)
INSERT INTO public.article_tags (article_id, tag_id) VALUES
(51, (SELECT id FROM public.tags WHERE name = 'Microservices')),
(51, (SELECT id FROM public.tags WHERE name = 'DevOps')),
(51, (SELECT id FROM public.tags WHERE name = 'Cloud')),

(101, (SELECT id FROM public.tags WHERE name = 'Java')),
(101, (SELECT id FROM public.tags WHERE name = 'Performance')),

(151, (SELECT id FROM public.tags WHERE name = 'Java')),
(151, (SELECT id FROM public.tags WHERE name = 'NixOS')),
(151, (SELECT id FROM public.tags WHERE name = 'DevOps')),

(201, (SELECT id FROM public.tags WHERE name = 'Quarkus')),
(201, (SELECT id FROM public.tags WHERE name = 'Spring Boot')),
(201, (SELECT id FROM public.tags WHERE name = 'Cloud')),
(201, (SELECT id FROM public.tags WHERE name = 'Java')),

(251, (SELECT id FROM public.tags WHERE name = 'PostgreSQL')),
(251, (SELECT id FROM public.tags WHERE name = 'Database')),
(251, (SELECT id FROM public.tags WHERE name = 'Performance'));
