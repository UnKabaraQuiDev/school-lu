SELECT pk_idApartment, floor, surface
FROM apartment
WHERE surface > (SELECT AVG(surface) FROM apartment)
ORDER BY pk_idApartment DESC;