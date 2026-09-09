#ALTER TABLE `Order`
ALTER TABLE `item`
ADD category TINYINT UNSIGNED NOT NULL AFTER unitPrice;

/*
this query is wrong and doesn't do anything because in the table `Order` is no attribute named "unitPrice"

if changed and applied to the table `item`, then a new attribute named "category" is created after the attribute "unitPrice"
the attribute "category" is an unsigned tinyint and cannot be NULL
*/