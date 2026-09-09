SELECT title AS "Title", publicationDate AS "Publication date"#, rating
FROM article
WHERE rating IS NOT NULL
ORDER BY rating ASC;