SELECT *
FROM exercise05.apartment
JOIN residence
	ON pk_idResidence = fk_residence_contains
ORDER BY surface DESC;