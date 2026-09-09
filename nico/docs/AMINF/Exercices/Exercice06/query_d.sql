SELECT title, publicationDate, abstract, /*body,*/ LENGTH(body) AS "Number of bytes", CHAR_LENGTH(body) AS "Number of letters"
FROM article
ORDER BY LENGTH(body) ASC;