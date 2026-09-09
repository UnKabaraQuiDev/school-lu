SELECT name, apartmentNumber, floor, surface, numberRooms, balcony
FROM apartment
JOIN residence
	ON pk_idResidence = fk_residence_contains
WHERE firstRentalDate IS NULL;