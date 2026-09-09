SELECT member.pk_code, surname, firstname, birthdate, streetNbr, city, postcode, designation
FROM member
INNER JOIN country
	ON fk_country_lives = country.pk_code
WHERE (YEAR(birthdate) BETWEEN 1980 AND 2000
	AND LOWER(designation) = "luxembourg")
    OR firstname LIKE "%-%";	# we don't need to include cases where the "-" is at the beginnig/end  of the string, because a name cannot start with a "-"