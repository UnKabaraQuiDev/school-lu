SELECT pk_order AS "Order Nr", CONCAT(ROUND(SUM(quantity * unitPrice), 2), " €") AS "Subtotal", CONCAT(shipping, " €") AS "Shipping",
CONCAT(ROUND(SUM(quantity * unitPrice) + shipping, 2), " €") AS "Total"
#FROM exercise09.order
FROM `order`
INNER JOIN item
	ON pk_order = fk_order_concerns
GROUP BY pk_order;