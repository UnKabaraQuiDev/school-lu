SELECT residanceName, COUNT(pk_apartment) AS "Nombre d'appartements"
FROM exercice04.apartment
GROUP BY residanceName;