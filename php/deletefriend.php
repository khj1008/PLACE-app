<?php
    header("Content-Type: text/html;charset=UTF-8");
    $conn = mysqli_connect("localhost","root","586394","appdata");
    $data_stream = "'".$_POST['friendid']."'";
    $query = "delete from friend_relation where friendid=$data_stream";
    $result = mysqli_query($conn, $query);
     
    if($result)
      echo "1";
    else
      echo "-1";
     
    mysqli_close($conn);
?>


