SELECT firstname AS "First name", surname AS "Surname", CONCAT(100 * COUNT(commission.pk_code), " €") AS "Indemnity"
FROM member
INNER JOIN assigns
	ON member.pk_code = pkfk_member
INNER JOIN commission
	ON pkfk_comission = commission.pk_code
GROUP BY surname, firstname
ORDER BY surname DESC, firstname DESC;	# added order on firstname as it was not specified which name needs to be sorted