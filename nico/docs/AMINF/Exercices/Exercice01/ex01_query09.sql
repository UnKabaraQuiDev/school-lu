ALTER TABLE exercice01.apartment
MODIFY COLUMN maxOccupancy DECIMAL(5,2)
GENERATED ALWAYS AS (
    nbRooms * 2
) STORED;