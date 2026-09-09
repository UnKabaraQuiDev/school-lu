#SELECT *
#FROM item
#WHERE unitPrice >= 0;

SET sql_safe_updates=0;

UPDATE item
SET unitPrice = NULL
#WHERE unitPrice < 0;
WHERE pk_Item = (SELECT pk_Item
				 FROM item
                 WHERE unitPrice < 0);
                 
SET sql_safe_updates=1;