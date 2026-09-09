SELECT designation, GROUP_CONCAT(fk_order_concerns SEPARATOR ", ") AS "Order Nr"
FROM item
GROUP BY designation
HAVING COUNT(*) > 1
ORDER BY COUNT(*) DESC;