SELECT *
FROM country
WHERE designation IS NULL
	OR designation = ""
    OR prefix IS NULL
    OR prefix = "";