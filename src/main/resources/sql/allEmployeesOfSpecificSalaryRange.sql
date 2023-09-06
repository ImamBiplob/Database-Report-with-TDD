select first_name, dept_name, salary from salaries s join employees e on e.emp_no = s.emp_no join dept_emp de on de.emp_no = e.emp_no join departments d on d.dept_no = de.dept_no where salary > 150000;