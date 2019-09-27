<?php
//these are the server details
//the username is root by default in case of xampp
//password is nothing by default
//and lastly we have the database named android. if your database name is different you have to change it
//$servername = "localhost";
//$username = "User1";
//$password = "148207me";
//$database = "taskmanager";
//$port = 3307;

//creating a new connection object using mysqli
//$link = mysqli_init();
//$conn = mysqli_real_connect($link, $servername, $username, $password, $database, $port);
//$conn = new mysqli($servername,$username,$password,$database);

//if there is some error connecting to the database
//with die we will stop the further execution by displaying a message causing the error
//if (mysqli_connect_error()) {

    //echo 'Connected NOT.';
//}
//else echo 'Connected successfully.<br />';

//if everything is fine

//creating an array for storing the data
$employees = array();

//this is our sql query
//$sql = "SELECT EmployeeName, EmployeePassword FROM employee;";
//$sql = "SELECT EmployeeName, EmployeePassword FROM employee";
//$sql = "SELECT * FROM `employee`";
//echo $totalRows = mysql_num_rows($sql);
//creating an statment with the query
//$stmt = $conn->prepare($sql);

//executing that statment
//$stmt = mysqli_query($conn,$sql);
//echo 'here<br />';
//binding results for that statment
//$stmt->bind_result($name, $pass);
//$num_of_rows = $stmt->num_rows;
//echo $num_of_rows ;
//looping through all the records
//while($stmt->fetch()){

	//pushing fetched data in an array
	//$temp = [
	//	'EmployeeName'=>$name,
	//	'EmployeePassword'=>$pass
	//];
	//echo 'got line';
	//pushing the array inside the hero array
	//array_push($employees, $temp);
//}
array_push($employees,"testme");
//displaying the data in json format
echo json_encode($employees);
?>