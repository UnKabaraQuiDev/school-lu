SELECT firstname, surname
FROM Employee
INNER JOIN DepartmentManager
	ON pk_pl_employee = pkfk_employee_is
ORDER BY surname;