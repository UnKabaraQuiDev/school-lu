SELECT pk_employee, firstName, surname, dateOfBirth, hiringDate
FROM Employee
LEFT JOIN Title
	ON pk_employee = pkfk_employee_holds
WHERE pkfk_employee_holds IS NULL
ORDER BY pk_employee;