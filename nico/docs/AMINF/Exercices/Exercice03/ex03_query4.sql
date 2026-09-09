SELECT * FROM exercice03.apartment
WHERE residanceName IS NULL
OR residanceName = ""
OR rentalDate IS NULL
OR rentalDate = "";