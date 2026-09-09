SELECT purchaseDate, CONCAT(ROUND(SUM(totalPrice), 2), " €") AS "Total"
FROM `order`
GROUP BY purchaseDate;