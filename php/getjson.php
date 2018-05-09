<?php  

$link=mysqli_connect("localhost","root","586394", "appdata" );  
if (!$link)  
{  
    echo "MySQL ���� ���� : ";
    echo mysqli_connect_error();
    exit();  
}  

mysqli_set_charset($link,"utf8"); 


$sql="select * from user";

$result=mysqli_query($link,$sql);
$data = array();   
if($result){  
    
    while($row=mysqli_fetch_array($result)){
        array_push($data, 
            array('userid'=>$row[0],
            'imgurl'=>$row[1],
            'name'=>$row[2]
        ));
    }

    header('Content-Type: application/json; charset=utf8');
    $json = json_encode(array("kimhyju"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
    echo $json;

}  
else{  
    echo "SQL�� ó���� ���� �߻� : "; 
    echo mysqli_error($link);
} 


 
mysqli_close($link);  
   
?>