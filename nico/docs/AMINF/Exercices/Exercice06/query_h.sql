SELECT pk_article AS "Article ID", title AS "Title", abstract AS "Abstract", publicationDate AS "Publication Date",
firstName AS "Author's firstname", surname AS "Author's surname"
FROM article
INNER JOIN author
	ON pk_author = fk_author_writes
LEFT JOIN links
	ON pk_article = pkfk_article_to
WHERE pkfk_article_to IS NULL
ORDER BY publicationDate ASC;