DELETE FROM exercice02.apartment
WHERE exercice02.apartment.pk_apartment = (SELECT pk_apartment
						FROM exercice02.apartment
                        WHERE (balcony = 1
                        AND rentalDate BETWEEN "2000-01-01" AND "2000-12-31")
                        OR nbRooms = 1);