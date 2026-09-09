SELECT pk_employee, firstName, surname, title, departement
FROM Employee
INNER JOIN AssignedTo
	ON pk_employee = pkfk_employee
INNER JOIN Department
	ON pk_department = pkfk_department
INNER JOIN Title
	ON pk_employee = pkfk_employee_holds
WHERE assignedTo.endDate IS NULL
	AND title.endDate IS NULL
ORDER BY pk_employee ASC;