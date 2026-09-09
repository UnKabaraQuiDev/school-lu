SELECT designation, COUNT(*) AS "Number of Orders"
FROM item
GROUP BY designation
HAVING COUNT(*) > 1
ORDER BY COUNT(*) DESC;