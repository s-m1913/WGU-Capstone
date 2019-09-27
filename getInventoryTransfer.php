<?php
include 'db/db_connect.php';

$sql = $_POST["query"];
$response = array();
$temp = array();
$result = array();
$stmt = mysqli_query ($conn, $sql);

if (mysqli_num_rows($stmt) > 0) {

	while($row = mysqli_fetch_assoc($stmt)) {

		$temp = ["InventoryTransferID" => $row["InventoryTransferID"],
		"InventoryID" => $row["InventoryID"],
		"InventoryTransferFrom" => $row["InventoryTransferFrom"],
		"InventoryTransferTo" => $row["InventoryTransferTo"],
		"InventoryTransferBy" => $row["InventoryTransferBy"],
		"InventoryTransferDate" => $row["InventoryTransferDate"],
		"InventoryTransferQuantity" => $row["InventoryTransferQuantity"]];
		
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