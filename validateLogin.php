<?php
include 'db/db_connect.php';

$employees = array();
$user = $_POST["user"];
$pass = $_POST["pass"];
$sql = "SELECT * FROM employee";
$valid = 'not valid';

$result = mysqli_query ($conn, $sql);

if (mysqli_num_rows($result) > 0) {

	while($row = mysqli_fetch_assoc($result)) {

		$empName = $row["EmployeeName"];
		$empPass = $row["EmployeePassword"];

		if ($user === $empName && $pass === $empPass) $valid = $row["EmployeeID"];
	}
    
}
echo $valid;

mysqli_close($conn);
?>