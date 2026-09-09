SELECT member.pk_code, surname, firstname, birthdate, streetNbr, city, postcode, designation
FROM member
INNER JOIN country
	ON fk_country_lives = country.pk_code
LEFT JOIN sponsors
	ON member.pk_code = pkfk_member_godfather
WHERE pkfk_member_godfather IS NULL;