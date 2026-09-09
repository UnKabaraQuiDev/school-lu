#SELECT title AS "Title" , 'abstract', publicationDate AS "Publication Date" --> ëmmer String
#SELECT title AS "Title" , `abstract`, publicationDate AS "Publication Date" --> bezeechnung fir eng kolonn, awer net bonotzen, fonctionéiert net ëmmer

SELECT title AS "Title" , abstract AS "Abstract", publicationDate AS "Publication date"
FROM article
ORDER BY publicationDate DESC;