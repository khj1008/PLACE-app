<?php  

$link=mysqli_connect("localhost","root","586394", "appdata" );  
if (!$link)  
{  
    echo "MySQL 접속 에러 : ";
    echo mysqli_connect_error();
    exit();  
}  



mysqli_set_charset($link,"utf8"); 
$userid=$_REQUEST[userid];

$sql="select imgurl,name from user where userid='$userid'";


$result=mysqli_query($link,$sql);
$data = array();   
if($result){  
    
    while($row=mysqli_fetch_array($result)){
        array_push($data, 
            array('imgurl'=>$row[0],
'name'=>$row[1]
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