SELECT race.pk_name AS "Race name", COUNT(pkfk_stage_Participates) AS "Number of participants"
FROM race
INNER JOIN stage
	ON race.pk_name = fk_race_consists
LEFT JOIN participates
	ON stage.pk_code = pkfk_stage_Participates
GROUP BY race.pk_name;