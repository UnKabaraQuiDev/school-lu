SET sql_safe_updates = 0;

#UPDATE `Order`
UPDATE `item`
SET `category` = (
	CASE
		WHEN unitPrice < 200 THEN 1
		WHEN unitPrice < 500 THEN 2
		WHEN unitPrice < 1000 THEN 3
		ELSE 4
	END );

SET sql_safe_updates = 1;

/*
this query (when applied to the table `item` is setting a value to the attribute named "category":
- when the unitPrice is less than 200, category is 1
- when the unitPrice is less than 500, category is 2
- when the unitPrice is less than 1000, category is 3
- in all other cases, category is 4
*/