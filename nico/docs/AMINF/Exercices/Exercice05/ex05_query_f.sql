SELECT pk_idApartment, floor, lastName, firstName
FROM rents
INNER JOIN apartment
	ON pk_idApartment = pkfk_apartment
INNER JOIN person
	ON pk_ssn = pkfk_person
WHERE exitDate IS NULL;