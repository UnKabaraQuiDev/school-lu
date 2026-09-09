SET sql_safe_updates=0;

/*UPDATE `order`
SET totalPrice = (SELECT ROUND(SUM(quantity * unitPrice), 2)
				  FROM `order`
                  INNER JOIN item
					  ON pk_order = fk_order_concerns
				  GROUP BY pk_order) + shipping;*/

UPDATE `order`
SET totalPrice = shipping + (
    SELECT ROUND(SUM(quantity * unitPrice), 2)
    FROM item
    WHERE fk_order_concerns = pk_order
);

SET sql_safe_updates=1;