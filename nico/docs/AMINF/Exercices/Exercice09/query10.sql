SELECT firstName AS "First name", surname AS "Surname"#, CONCAT(totalPrice, " €") AS "Total"
FROM `client`
LEFT JOIN `order`
	ON pk_client = fk_client_issues
WHERE pk_order IS NULL;