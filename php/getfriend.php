<?php  

$link=mysqli_connect("localhost","root","586394", "appdata" );  
if (!$link)  
{  
    echo "MySQL 접속 에러 : ";
    echo mysqli_connect_error();
    exit();  
}  



mysqli_set_charset($link,"utf8"); 
$me_userid=$_REQUEST[me_userid];

$sql="select friend_name,friend_userid,friend_image from friend_relation where me_userid='$me_userid'";


$result=mysqli_query($link,$sql);
$data = array();   
if($result){  
    
    while($row=mysqli_fetch_array($result)){
        array_push($data, 
            array('friend_name'=>$row[0],
'friend_userid'=>$row[1],
'friend_image'=>$row[2]
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