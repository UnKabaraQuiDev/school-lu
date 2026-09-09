SELECT title, publicationDate, firstName, surname
FROM article
INNER JOIN author
	ON pk_author = fk_author_writes
ORDER BY firstName ASC;