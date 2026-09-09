/*INSERT INTO doctor(pk_code, surname, firstName)
VALUES("111", "aaa", "aaa"),
("222", "bbb", "bbb");*/

#SELECT *
DELETE #doctor
FROM doctor
/*LEFT JOIN dopingtest
	ON pk_code = fk_doctor_performs
WHERE fk_doctor_performs IS NULL;*/
WHERE pk_code NOT IN (SELECT fk_doctor_performs
					FROM dopingtest
);