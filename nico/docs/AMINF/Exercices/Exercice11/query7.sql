SELECT title AS "Title", CONCAT(ROUND(COUNT(commission.pk_code) / (SELECT COUNT(*) FROM member) * 100, 0), " %") AS "Occupancy"
FROM commission
INNER JOIN assigns
	ON commission.pk_code = pkfk_comission
INNER JOIN member
	ON pkfk_member = member.pk_code
GROUP BY title;