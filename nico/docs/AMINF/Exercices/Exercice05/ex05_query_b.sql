SELECT apartmentNumber, name
FROM exercise05.apartment
INNER JOIN residence
	ON fk_residence_contains = pk_idResidence
WHERE balcony = 1
ORDER BY name DESC;