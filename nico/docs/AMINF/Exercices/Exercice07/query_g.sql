SELECT race.pk_name AS "Race name", surname AS "Surname", firstName AS "First name", SUM(points) AS "Total points"
FROM cyclist
INNER JOIN participates
	ON pk_license = pkfk_cyclist_Participates
INNER JOIN stage
	ON pkfk_stage_Participates = stage.pk_code
INNER JOIN race
	ON fk_race_consists = race.pk_name
GROUP BY race.pk_name, surname, firstName
ORDER BY race.pk_name, surname, firstName;