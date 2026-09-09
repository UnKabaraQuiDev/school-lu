SELECT surname AS "Surname", firstName AS "First name", SUM(points) AS "Total points"
FROM cyclist
INNER JOIN participates
	ON pk_license = pkfk_cyclist_Participates
GROUP BY pk_license;