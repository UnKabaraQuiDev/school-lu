SELECT residanceName, AVG(nbRooms) AS "Nombre moyenne de chambres"
FROM exercice04.apartment
GROUP BY residanceName
HAVING AVG(nbRooms) >= 2;