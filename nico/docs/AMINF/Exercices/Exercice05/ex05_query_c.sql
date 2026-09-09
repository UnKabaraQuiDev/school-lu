SELECT apartmentNumber, name, balcony, numberRooms, undergroundParking
FROM apartment
INNER JOIN residence
	ON pk_idResidence = fk_residence_contains
WHERE balcony = 0
	AND numberRooms > 2
    AND undergroundParking = 1;