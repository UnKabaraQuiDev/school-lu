SELECT YEAR(purchaseDate) AS "Year", MONTH(purchaseDate) AS "Month", CONCAT(ROUND(SUM(quantity * unitPrice), 2), " €") AS "Total" #CONCAT(MONTHNAME(purchaseDate), " ", YEAR(purchaseDate)) AS "Month"
FROM `order`
INNER JOIN item
	ON pk_order = fk_order_concerns
GROUP BY YEAR(purchaseDate), MONTH(purchaseDate) #CONCAT(MONTHNAME(purchaseDate), " ", YEAR(purchaseDate))
ORDER BY YEAR(purchaseDate) ASC, MONTH(purchaseDate) ASC;