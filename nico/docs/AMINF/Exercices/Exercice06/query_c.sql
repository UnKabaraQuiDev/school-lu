SELECT title, publicationDate#, abstract
FROM article
WHERE YEAR(publicationDate) = 2019
	AND abstract IS NULL;