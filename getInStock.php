<?php
include 'db/db_connect.php';

$sql = $_POST["query"];
$response = array();
$temp = array();
$result = array();
$stmt = mysqli_query ($conn, $sql);

if (mysqli_num_rows($stmt) > 0) {

	while($row = mysqli_fetch_assoc($stmt)) {

		$temp = ["InventoryID" => $row["InventoryID"],
		"InventoryTitle" => $row["InventoryTitle"],
		"InventoryPrice" => $row["InventoryPrice"],
		"InStockID" => $row["InStockID"],
		"InStockInventoryID" => $row["InStockInventoryID"],
		"InStockQuantity" => $row["InStockQuantity"],
		"InStockEmployeeID" => $row["InStockEmployeeID"]];
		
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