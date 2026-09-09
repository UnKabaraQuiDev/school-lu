# SELECT COUNT(residanceName) AS "Total apartments with a readable name"
# FROM exercice04.apartment;

SELECT residanceName, COUNT(*) AS "Nombre d'appartements"
FROM exercice04.apartment
WHERE residanceName IS NOT NULL
	AND residanceName <> ""
GROUP BY residanceName;