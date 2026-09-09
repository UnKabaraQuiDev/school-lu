SELECT name, apartmentNumber, floor, surface, numberRooms, balcony#, pkfk_apartment
FROM apartment
JOIN residence
	ON pk_idResidence = fk_residence_contains
LEFT JOIN rents
	ON pk_idApartment = pkfk_apartment
WHERE pkfk_apartment IS NULL
	AND pkfk_person IS NULL;