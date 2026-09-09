SELECT residanceName, AVG(nbRooms) AS "Average Rooms"
FROM exercice04.apartment
GROUP BY residanceName
HAVING LOWER(residanceName) LIKE "% ciel %"
	OR LOWER(residanceName) LIKE "ciel %"
    OR LOWER(residanceName) LIKE "% ciel";