SET sql_safe_updates=0;

UPDATE item
SET totalPrice = quantity * unitPrice
WHERE unitPrice IS NOT NULL
	AND quantity IS NOT NULL;

SET sql_safe_updates=1;