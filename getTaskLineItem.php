<?php
include 'db/db_connect.php';

$sql = $_POST["query"];
$response = array();
$temp = array();
$result = array();
$stmt = mysqli_query ($conn, $sql);

if (mysqli_num_rows($stmt) > 0) {

	while($row = mysqli_fetch_assoc($stmt)) {

		$temp = ["TaskLineItemID" => $row["TaskLineItemID"],
		"TaskLineItemQuant" => $row["TaskLineItemQuant"],
		"InventoryID" => $row["InventoryID"],
		"TaskID" => $row["TaskID"]];
		
		$result[]=$temp;
	}
	
    $response["success"] = 1;
	$response["data"] = $result;

}else{
	//Some error while fetching data
	$response["success"] = 0;
	$response["message"] = mysqli_error($con);
}
mysqli_close($conn);
echo json_encode($response);
?>