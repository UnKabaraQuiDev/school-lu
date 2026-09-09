CREATE TABLE Category (
	pk_category int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	name varchar(255) NOT NULL,
	PRIMARY KEY (pk_category)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*
this query creates a new table with the following attributes:
- pk_category - integer - primary key
- name  - varschar(255)
(the value NULL can't be assigned to any of them)
*/