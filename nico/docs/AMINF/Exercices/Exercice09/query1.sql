SELECT *
FROM Client
WHERE firstname = "Muriel"
	AND password = SHA1("test11");