SELECT firstName, surname, COUNT(fk_author_writes) AS "Number of articles written by this author"
FROM author
LEFT JOIN article
	ON pk_author = fk_author_writes
GROUP BY pk_author;
#GROUP BY firstName, surname;