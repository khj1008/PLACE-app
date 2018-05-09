<?php
    header("Content-Type: text/html;charset=UTF-8");
    $conn = mysqli_connect("localhost","root","586394","appdata");
    $data_stream = "'".$_POST['markerid']."','".$_POST['lat']."','".$_POST['lng']."','".$_POST['name_place']."','".$_POST['name_group']."','".$_POST['content']."','".$_POST['name_user']."','".$_POST['userid']."'";
    $query = "insert into marker(markerid,lat,lng,name_place,name_group,content,name_user,userid) values (".$data_stream.")";
    $result = mysqli_query($conn, $query);
     
    if($result)
      echo "1";
    else
      echo "-1";
     
    mysqli_close($conn);
?>


