SELECT firstName, surname, amount AS "Highest salary"
FROM Employee
INNER JOIN Salary
	ON pkfk_employee_assignedTo = pk_employee
WHERE endDate IS NOT NULL
	AND amount =   (SELECT MAX(s.amount)
					FROM Salary as s
                    WHERE s.endDate IS NOT NULL);