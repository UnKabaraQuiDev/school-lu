SELECT balcony, COUNT(*) AS "Nombre d'aparements"
FROM exercice04.apartment
WHERE balcony >= 2
GROUP BY balcony;