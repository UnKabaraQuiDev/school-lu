SELECT pk_license AS "License number", fk_team_belongs AS "Team", surname AS "Surname", firstName AS "First Name",
nationality AS "Nationality", dateOfBirth AS "Date of birth"#, date AS "Date of doping test", result AS "Result"
FROM cyclist
LEFT JOIN dopingtest
	ON pk_license = fk_cyclist_undergoes
WHERE pk_test IS NULL;