<?php  

$link=mysqli_connect("localhost","root","586394", "appdata" );  
if (!$link)  
{  
    echo "MySQL 접속 에러 : ";
    echo mysqli_connect_error();
    exit();  
}  



mysqli_set_charset($link,"utf8"); 
$markerid=$_REQUEST[markerid];

$sql="select name_place, content, name_user from marker where markerid='$markerid'";


$result=mysqli_query($link,$sql);
$data = array();   
if($result){  
    
    while($row=mysqli_fetch_array($result)){
        array_push($data, 
            array('name_place'=>$row[0],
'content'=>$row[1],
'name_user'=>$row[2]
	  
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