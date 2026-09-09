SET sql_safe_updates = 0;

#DELETE
#FROM item
#INNER JOIN `order`
#	ON item.fk_order_concerns = `order`.pk_order
#WHERE purchaseDate < '2019-02-01';

DELETE
FROM item
WHERE fk_order_concerns IN (
	SELECT pk_order
    FROM `order`
    WHERE purchaseDate < '2019-02-01');

DELETE
FROM `order`
#WHERE purchaseDate < "01.02.2019";
WHERE purchaseDate < '2019-02-01';

SET sql_safe_updates = 1;