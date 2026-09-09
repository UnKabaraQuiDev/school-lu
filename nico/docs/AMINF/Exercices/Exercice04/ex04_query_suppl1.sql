SELECT residanceName, AVG(surface) AS 'Average surface'
FROM exercice04.apartment
GROUP BY residanceName;