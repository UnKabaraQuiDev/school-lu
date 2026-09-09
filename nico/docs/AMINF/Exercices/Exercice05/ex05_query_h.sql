SELECT DISTINCT residence.name, firstName, lastName
FROM rents
JOIN person
	ON pk_ssn = pkfk_person
JOIN apartment
	ON pkfk_apartment = pk_idApartment
JOIN residence
	ON fk_residence_contains = pk_idResidence
WHERE exitDate IS NULL;