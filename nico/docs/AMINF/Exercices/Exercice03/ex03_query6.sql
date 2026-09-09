SELECT * FROM exercice03.apartment
WHERE pk_apartment IS NULL
OR residanceName IS NULL
OR surface IS NULL
OR nbRooms IS NULL
OR balcony IS NULL
OR rentalDate IS NULL;