DELETE FROM exercice02.apartment AS a
WHERE a.pk_apartment = (SELECT pk_apartment
						FROM exercice02.apartment
						WHERE surface BETWEEN 50 AND 80);