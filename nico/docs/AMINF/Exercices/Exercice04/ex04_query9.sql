SELECT balcony, YEAR(rentalDate) AS "Year", COUNT(pk_apartment) AS "Nombre d'appartements"
FROM exercice04.apartment
GROUP BY balcony, YEAR(rentalDate)
ORDER BY COUNT(pk_apartment) DESC;