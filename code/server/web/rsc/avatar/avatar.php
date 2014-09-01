<?php
// .htaccess
// RewriteEngine On
// RewriteBase /web/rsc/avatar
// RewriteRule ^(.*).png$  avatar.php?action=$1

header("Content-type: image/png");

$con = mysql_connect("localhost","ushare","ushare2014");
if (!$con)
{
	// DB Error, Exist
	echo 'Could not connect: ' . mysql_error();
	exit;
}

$uid = $_GET['action'];
mysql_select_db("ushare", $con);
$result = mysql_query("SELECT avatar FROM Ushare_User WHERE id=".$uid, $con);
if(mysql_num_rows($result)>0)
{
	$root = "../images/avatars/";
	$row = mysql_fetch_array($result);
	if($row['avatar']===NULL) {
		$avatars[] = "Avengers-Agent-Coulson-icon.png";
		$avatars[] = "Avengers-Black-Widow-icon.png";
		$avatars[] = "Avengers-Captain-America-icon.png";
		$avatars[] = "Avengers-Giant-Man-icon.png";
		$avatars[] = "Avengers-Hawkeye-icon.png";
		$avatars[] = "Avengers-Hulk-icon.png";
		$avatars[] = "Avengers-Iron-Man-icon.png";
		$avatars[] = "Avengers-Loki-icon.png";
		$avatars[] = "Avengers-Nick-Fury-icon.png";
		$avatars[] = "Avengers-Thor-icon.png";
		$avatars[] = "Avengers-War-Machine-icon.png";

		$avatar = $avatars[mt_rand(0, count($avatars)-1)];

		mysql_query("UPDATE Ushare_User SET avatar = '".$avatar."' WHERE id=".$uid, $con);

		displayPNG($root . $avatar);
		
	} else {
		displayPNG($root . $row['avatar']);
	}
}

function displayPNG($str) {
	$newavatar = imagecreatefrompng($str);

	// transparency apply
	imagealphablending($newavatar, false);
	imagesavealpha($newavatar, true);

	imagepng($newavatar);
	imagedestroy($newavatar);

}
?>
