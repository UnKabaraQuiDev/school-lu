SET SQL_SAFE_UPDATES = 0;

UPDATE country
SET designation = UPPER(designation);

SET SQL_SAFE_UPDATES = 1;