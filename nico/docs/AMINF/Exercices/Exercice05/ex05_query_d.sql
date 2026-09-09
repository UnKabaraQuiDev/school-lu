SELECT name, SUM(surface) AS "Total Surface"
FROM apartment
INNER JOIN residence
	ON pk_idResidence = fk_residence_contains
GROUP BY name;