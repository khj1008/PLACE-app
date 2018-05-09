<?php
    header("Content-Type: text/html;charset=UTF-8");
    $conn = mysqli_connect("localhost","root","586394","appdata");
    $data_stream = "'".$_POST['groupname']."','".$_POST['grouppassword']."'";
    $query = "insert into group_list(group_name,group_password) values (".$data_stream.")";
    $result = mysqli_query($conn, $query);
     
    if($result)
      echo "1";
    else
      echo "-1";
     
    mysqli_close($conn);
?>


