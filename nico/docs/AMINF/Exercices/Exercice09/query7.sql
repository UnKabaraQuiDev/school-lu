SELECT pk_order AS "Order Nr", surname AS "Client's Last Name", firstName AS "Client's First Name",
	purchaseDate AS "Purchase Date", CONCAT(ROUND(SUM(quantity * unitPrice), 2), " €") AS "Subtotal", CONCAT(shipping, " €") AS "Shipping",
	CONCAT(ROUND(SUM(quantity * unitPrice) + shipping, 2), " €") AS "Total"
#FROM exercise09.order
FROM `order`
INNER JOIN item
	ON pk_order = fk_order_concerns
INNER JOIN `client`
	ON fk_client_issues = pk_client
GROUP BY pk_order
ORDER BY surname ASC, firstname DESC;