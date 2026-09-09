SELECT /*pk_article,*/ title AS "Title", abstract AS "Abstract", publicationDate AS "Publication Date",
firstName AS "Firstname", surname AS "Surname", COUNT(pkfk_article_from) /*oder: COUNT(pk_article)*/ AS "Number of links"
FROM article
INNER JOIN links
	ON pk_article = pkfk_article_from
INNER JOIN author
	ON pk_author = fk_author_writes
GROUP BY pk_article;