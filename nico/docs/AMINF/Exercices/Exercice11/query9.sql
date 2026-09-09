SELECT godchild.firstname AS "First name of godchild", godchild.surname AS "Surname of godchild",
GROUP_CONCAT(godfather.surname SEPARATOR ", ") AS "Sponsors / godfathers"
FROM sponsors
INNER JOIN member AS godchild
	ON pkfk_member_godchild = godchild.pk_code
INNER JOIN member AS godfather
	ON pkfk_member_godfather = godfather.pk_code
GROUP BY pkfk_member_godchild;