<?php
    header("Content-Type: text/html;charset=UTF-8");
    $conn = mysqli_connect("localhost","root","586394","appdata");
    $data_stream = "'".$_POST['friendid']."','".$_POST['me_name']."','".$_POST['me_userid']."','".$_POST['friend_name']."','".$_POST['friend_userid']."','".$_POST['friend_image']."'";
    $query = "insert into friend_relation(friendid,me_name,me_userid,friend_name,friend_userid,friend_image) values (".$data_stream.")";
    $result = mysqli_query($conn, $query);
     
    if($result)
      echo "1";
    else
      echo "-1";
     
    mysqli_close($conn);
?>
