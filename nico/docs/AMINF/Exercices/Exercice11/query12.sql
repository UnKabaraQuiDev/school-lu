SELECT member.pk_code, surname, firstname, birthdate, streetNbr, city, postcode, designation
FROM member
INNER JOIN country
	ON fk_country_lives = country.pk_code
INNER JOIN sponsors
	ON member.pk_code = pkfk_member_godchild
GROUP BY member.pk_code, surname, firstname, birthdate, streetNbr, city, postcode, designation;