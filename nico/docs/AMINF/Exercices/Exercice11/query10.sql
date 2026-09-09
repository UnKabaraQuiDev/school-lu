SELECT title AS "Title"#, COUNT(*)
FROM commission
INNER JOIN assigns
	ON commission.pk_code = pkfk_comission
INNER JOIN member
	ON pkfk_member = member.pk_code
WHERE pk_entryDate < "2020-01-03"
	AND (leavingDate IS NULL
    OR leavingDate > "2020-01-03")
GROUP BY title
HAVING COUNT(pkfk_member) > 3;