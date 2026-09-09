SELECT DISTINCT surname AS "Surname", firstName AS "First name", race.pk_name AS "Race name", startDate AS "Start date"
FROM cyclist
INNER JOIN participates
	ON pk_license = pkfk_cyclist_Participates
INNER JOIN stage
	ON pkfk_stage_Participates = stage.pk_code
INNER JOIN race
	ON fk_race_consists = race.pk_name
ORDER BY surname ASC;
#ORDER BY firstName ASC;
#ORDER BY race.pk_name ASC;
#ORDER BY startDate ASC;