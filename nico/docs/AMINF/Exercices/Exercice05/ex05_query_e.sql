SELECT name, constructionDate, SUM(surface) AS "Total Surface"
FROM apartment
INNER JOIN residence
	ON pk_idResidence = fk_residence_contains
GROUP BY name, constructionDate;

# oder:
#GROUP BY pk_idResidence;