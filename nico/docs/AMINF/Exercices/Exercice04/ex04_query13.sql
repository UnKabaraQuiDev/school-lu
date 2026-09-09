SELECT residanceName, YEAR(rentalDate) AS year, SUM(nbRooms) AS "Number of Rooms"
FROM exercice04.apartment
GROUP BY year, residanceName;