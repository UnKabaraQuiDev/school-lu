#SELECT * FROM exercice01.apartment
#WHERE surface > 70
#	OR YEAR(rentalDate) < 2000;

SELECT YEAR(rentalDate) AS 'YEAR'
FROM exercice01.apartment AS A
WHERE A.surface > 70
	OR 'Year' < 2000;