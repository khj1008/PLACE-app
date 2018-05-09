<?php  

$link=mysqli_connect("localhost","root","586394", "appdata" );  
if (!$link)  
{  
    echo "MySQL 접속 에러 : ";
    echo mysqli_connect_error();
    exit();  
}  



mysqli_set_charset($link,"utf8"); 
$group_name=$_REQUEST[group_name];

$sql="select group_password from group_list where group_name='$group_name'";


$result=mysqli_query($link,$sql);
$data = array();   
if($result){  
    
    while($row=mysqli_fetch_array($result)){
        array_push($data, 
            array('group_password'=>$row[0]
        ));
    }

    header('Content-Type: application/json; charset=utf8');
    $json = json_encode(array("kimhyju"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
    echo $json;

}  
else{  
    echo "SQL문 처리중 에러 발생 : "; 
    echo mysqli_error($link);
} 


 
mysqli_close($link);  
   
?>