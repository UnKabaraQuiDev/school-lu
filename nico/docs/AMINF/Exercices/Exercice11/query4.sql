SELECT firstname AS "First name", surname AS "Surname", designation AS "Country", GROUP_CONCAT(title ORDER BY title SEPARATOR ", ") AS "All titles"
FROM member
INNER JOIN country
	ON fk_country_lives = country.pk_code
LEFT JOIN assigns
	ON member.pk_code = pkfk_member
LEFT JOIN commission
	ON pkfk_comission = commission.pk_code
GROUP BY firstname, surname, designation
ORDER BY GROUP_CONCAT(title ORDER BY title SEPARATOR ", ") ASC, surname DESC;