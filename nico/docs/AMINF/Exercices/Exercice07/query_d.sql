SELECT pk_license AS "License number", fk_team_belongs AS "Team", surname AS "Surname", firstName AS "First Name",
nationality AS "Nationality", dateOfBirth AS "Date of birth", date AS "Date of doping test", result AS "Result"
FROM cyclist
INNER JOIN dopingtest
	ON pk_license = fk_cyclist_undergoes
/*WHERE YEAR(date) = 2005
	OR YEAR(date) = 2014
    OR YEAR(date) = 2016
    OR YEAR(date) = 2019;*/
WHERE YEAR(date) IN (2005, 2014, 2016, 2019);