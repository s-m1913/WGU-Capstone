<?php
include 'db/db_connect.php';

$sql = $_POST["query"];
$response = array();
$temp = array();
$result = array();
$stmt = mysqli_query ($conn, $sql);

if (mysqli_num_rows($stmt) > 0) {

	while($row = mysqli_fetch_assoc($stmt)) {

		$temp = ["TaskID" => $row["TaskID"],
		"CustomerID" => $row["CustomerID"],
		"EmployeeID" => $row["EmployeeID"],
		"TaskStatus" => $row["TaskStatus"],
		"TaskDescription" => $row["TaskDescription"],
		"TaskResolution" => $row["TaskResolution"]];
		
		$result[]=$temp;
	}
	
    $response["success"] = "1";
	$response["data"] = $result;

}else{
	//Some error while fetching data
	$response["success"] = "0";

}
mysqli_close($conn);
echo json_encode($response);
?>
