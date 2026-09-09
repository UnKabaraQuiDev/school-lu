SELECT firstName AS "First name", surname AS "Surname", CONCAT(`order`.totalPrice, " €") AS "Total"
FROM client
INNER JOIN `order`
	ON pk_client = fk_client_issues
ORDER BY totalPrice DESC;