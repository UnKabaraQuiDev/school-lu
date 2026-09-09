SELECT DISTINCT lastName, firstName, residence.name
FROM residence
JOIN apartment
	ON pk_idResidence = fk_residence_contains
JOIN rents
	ON pkfk_apartment = pk_idApartment
RIGHT JOIN person
	ON pk_ssn = pkfk_person;

/*FROM rents
LEFT JOIN person
	ON pk_ssn = pkfk_person
LEFT JOIN apartment
	ON pkfk_apartment = pk_idApartment
LEFT JOIN residence
	ON pk_idResidence = fk_residence_contains;*/