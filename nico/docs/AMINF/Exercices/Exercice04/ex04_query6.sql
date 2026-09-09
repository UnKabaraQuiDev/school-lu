SELECT *, surface/nbRooms AS "Average surface per room",
CONCAT(surface*1200*1.18, " €") AS 'Sales price TTC'
FROM exercice04.apartment
WHERE ;