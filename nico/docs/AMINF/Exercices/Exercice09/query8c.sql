SELECT MONTHNAME(purchaseDate) AS "Month", CONCAT(ROUND(SUM(totalPrice), 2), " €") AS "Total"
FROM `order`
GROUP BY MONTHNAME(purchaseDate);