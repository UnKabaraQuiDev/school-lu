SELECT firstname AS "First name", surname AS "Surname", CONCAT(streetNbr, ", L-", postcode, " ", city) AS "Address"
FROM member
INNER JOIN country
	ON fk_country_lives = country.pk_code
WHERE LOWER(designation) IN ("germany", "france", "luxembourg");