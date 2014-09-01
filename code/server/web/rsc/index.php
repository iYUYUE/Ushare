<?php
// .htaccess
// RewriteEngine On
// RewriteBase /web/rsc
// RewriteCond $1 !(index\.php|css|images|js|avatar)
// RewriteRule ^(.*)$  index.php?action=$1 [L]

// by Yue Yu
$con = mysql_connect("localhost","ushare","ushare2014");
if (!$con)
{
	// DB Error, Exist
	echo 'Could not connect: ' . mysql_error();
	exit;
}
mysql_select_db("ushare", $con);

if(isset($_GET['action'])){

	if($_GET['action']=="list")
	{
		sleep (5);
		header('Content-Type: text/html; charset=utf-8');
		$response = stripslashes(file_get_contents("../shake/list.html"));
	}

	else if($_GET['action']=="result_success")
	{
		sleep (3);
		header('Content-Type: text/html; charset=utf-8');
		$response = stripslashes(file_get_contents("../shake/result_success.html"));
	}

	else if($_GET['action']=="result_fail")
	{
		sleep (3);
		header('Content-Type: text/html; charset=utf-8');
		$response = stripslashes(file_get_contents("../shake/result_fail.html"));
	}

	else if($_GET['action']=="shareRequest"&&isset($_GET['uid'])&&isset($_GET['request'])&&isset($_GET['token']))
	{
		header('Content-Type: text/html; charset=utf-8');
		$uid = $_GET['uid'];
		$request = $_GET['request'];
		$token = $_GET['token'];
		$userAuthCode=userAuth($con, $request, $token);
		$requestNote = isset($_GET['note'])?rtrim(substr(mysql_real_escape_string($_GET['note']), 0, 160)):"";
		if($userAuthCode==1) {
			$result = mysql_query("SELECT update_time FROM Ushare_Ava WHERE uid=".$uid);
			// check both user availability
			if(mysql_num_rows($result)>0) {
				$row = mysql_fetch_array($result);
				$updateInterval = time() - strtotime($row['update_time']);
				// check user liveness (last update no longer than 10 mins ago)
				if($updateInterval<600) {
					// get the request's rid and the sharer's name
					$result = mysql_query("SELECT rid FROM Ushare_User WHERE id=".$uid);
					$requestR = mysql_query("SELECT name FROM Ushare_User WHERE id=".$request);
					// add user to hold list (may be duplicate)
					if(mysql_query("INSERT INTO Ushare_Hold (uid) VALUES (".$uid.")"))
					{
						$row = mysql_fetch_array($result);
						$rowR = mysql_fetch_array($requestR);

						$mContent = $rowR['name'] . " has sent you a request. Confirm to exchange your contacts.\n\nNote: ".$requestNote;

						mysql_query("INSERT INTO Ushare_Message (uid, uid_r, type, content) VALUES (".$uid.", ".$request.", 1, '".$mContent."')");
						
						// send notification
						pushNotification($row['rid'], "Ushare Request", "New Request from Ushare", $mContent);

						$response = stripslashes(file_get_contents("../shake/result_success.html"));

					} else {
						$response = stripslashes(str_replace("XXX", "-3", file_get_contents("../shake/result_fail.html")));
					}

				} else {
					$response = stripslashes(str_replace("XXX", "-2", file_get_contents("../shake/result_fail.html")));
				}
			} else {
				$response = stripslashes(str_replace("XXX", "-1", file_get_contents("../shake/result_fail.html")));
			}
		} else {
			$response = stripslashes(str_replace("XXX", $userAuthCode, file_get_contents("../shake/result_invalid.html")));
		}
		
	}

	else if($_GET['action']=="shake"&&isset($_GET['bssid'])&&isset($_GET['uid'])&&isset($_GET['token']))
	{
		$mac = strtoupper($_GET['bssid']);
		$uid = $_GET['uid'];
		$token = $_GET['token'];

		if(!preg_match('/^([0-9A-F]{2}[:-]){5}([0-9A-F]{2})$/', $mac)){
			$response = stripslashes(str_replace("XXX", "invalid BSSID", file_get_contents("../shake/result_invalid.html")));
		} else {
			$userAuthCode=userAuth($con, $uid, $token);
			if($userAuthCode!=1) {
				$response = stripslashes(str_replace("XXX", "auth ".$userAuthCode, file_get_contents("../shake/result_invalid.html")));
			} else {
				$response = '<div id="Main">
				<div id="top">
					<div class="navi">
						<label class="title">Ushare</label>
						
						<table class="selection">
			                <tr>
			                    <td>
			                        <input type="checkbox" name="checkboxG4" id="checkboxG4" class="css-checkbox"
			                        checked="checked" />
			                        <label for="checkboxG4" class="css-label">
			                            <div style="width:10px;"></div><img src="./img/Female_small.png" class="avatar" border="0" align="default" style="max-width: 16px; max-height: 16px;">
			                        </label>
			                    </td>
			                    <td>
			                    <div style="width:16px;"></div>
			                    </td>
			                    <td>
			                        <input type="checkbox" name="checkboxG5" id="checkboxG5" class="css-checkbox"
			                        checked="checked" />
			                        <label for="checkboxG5" class="css-label">
			                            <img src="./img/Male_small.png" class="avatar" border="0" align="default" style="max-width: 16px; max-height: 16px;">
			                        </label>
			                    </td>
			                </tr>
			            </table>
						
					</div>
					<div class="content">
				</div>';
				$count = 0;

				// sleep(3);
				$avUsers = userSearch($con, $mac, $uid);

				if($avUsers!=null&&count($avUsers)>0){
					$response = $response . stripslashes(userListFormatter($con, $avUsers, $uid, $token, 20));
					$count += count($avUsers);
				}
				
				// next AP
				if($count<5){
					$avaAPs = apNearby($con, $mac, true);
					if($avaAPs!=null&&count($avaAPs)>0){
						for($i=0; $i<count($avaAPs); $i++) {
							$tmp = userSearch($con, $avaAPs[$i], $uid);
							if($tmp!=null&&count($tmp)>0){
								for($j=0; $j<count($tmp); $j++) {
									if($count==0 || !in_array($tmp[$j], $avUsers)) {
										$avUsers_tmp[] = $tmp[$j];
										$avUsers[] = $tmp[$j];
										$count++;
									}
								}

							}
						}
					}
				}
				
				// next AP (loose mode)
				if($count<5){
					$avaAPs = apNearby($con, $mac, false);
					if($avaAPs!=null&&count($avaAPs)>0){
						for($i=0; $i<count($avaAPs); $i++) {
							$tmp = userSearch($con, $avaAPs[$i], $uid);
							if($tmp!=null&&count($tmp)>0){
								for($j=0; $j<count($tmp); $j++) {
									if($count==0 || !in_array($tmp[$j], $avUsers)) {
										$avUsers_tmp[] = $tmp[$j];
										$avUsers[] = $tmp[$j];
										$count++;
									}
								}

							}
						}
					}
				}

				if($avUsers_tmp!=null&&count($avUsers_tmp)>0) {
					$response = $response . stripslashes(userListFormatter($con, $avUsers_tmp, $uid, $token, 50));
				}

				$response = $response . '</div></div>';

				if($count==0) {
					$temp = file_get_contents("../shake/noUser_found.html");
					$response = stripslashes('<div id="Main">' . $temp . '</div>');
				}
			}

		}
		
		header('Content-Type: text/html; charset=utf-8');
		
	}

	if($_GET['action']=="readMessage"&&isset($_GET['mid'])&&isset($_GET['uid'])&&isset($_GET['token']))
	{
		$mid = $_GET['mid'];
		$uid = $_GET['uid'];
		$token = $_GET['token'];
		$userAuthCode=userAuth($con, $uid, $token);
		// user auth
		if($userAuthCode!=1) {
			$response ="false";
		} else {
			// message ownership check
			if(getMessageUID($con, $mid)!=$uid) {
				$response ="false";
			} else {
				if(updateUnread($con, $mid))
					$response = "true";
				else
					$response ="false";
			}
		}
	}

	if($_GET['action']=="deleteMessage"&&isset($_GET['mid'])&&isset($_GET['uid'])&&isset($_GET['token']))
	{
		$mid = $_GET['mid'];
		$uid = $_GET['uid'];
		$token = $_GET['token'];
		$userAuthCode=userAuth($con, $uid, $token);
		// user auth
		if($userAuthCode!=1) {
			$response ="false";
		} else {
			// message ownership check
			if(getMessageUID($con, $mid)!=$uid) {
				$response ="false";
			} else {
				if(deleteMessage($con, $mid))
					$response = "true";
				else
					$response ="false";
			}
		}
	}

	else if($_SERVER['REQUEST_METHOD']=='POST'&&$_GET['action']=="check/username")
	{
		$rawdata = json_decode(file_get_contents('php://input'), true);
		$userName = $rawdata["username"];
		if(!isset($userName)||!userNameValidate($userName)) {
			header('HTTP/1.0 403 Forbidden');
			$response = '{"invalidChars": true}';
			
		} else {
			
			$result = mysql_query("SELECT id FROM Ushare_User WHERE name='".$userName."'");
			if(mysql_num_rows($result)>0) {
				header('HTTP/1.0 403 Forbidden');
				$response = '{"isTaken": true}';
			} else {
				header("HTTP/1.1 200 OK");
				$response = 'OK';
			}
		}
		// header
		header('Content-Type:text/plain; charset=utf-8');
	}

	else if($_SERVER['REQUEST_METHOD']=='POST'&&$_GET['action']=="weatherBroadcast") {
		if(!isset($_POST["cityName"])||!isset($_POST["weatherType"])) {
			header('HTTP/1.0 403 Forbidden');
			$response = "-1"; // parameter error
		} else {
			$city = strtolower($_POST["cityName"]);
			$weather = intval($_POST["weatherType"]);

			$result = mysql_query("SELECT id, name, rid FROM Ushare_User");
			
			header("HTTP/1.1 200 OK");

			$response = ($city=="suzhou"&&$weather>0)?mysql_num_rows($result):0;

			if($response>0) {
				for($i=0; $i<$response; $i++) {
					$row = mysql_fetch_array($result);
					$mContent = "Dear " . $row['name'] . ": It's ".(($weather==1)?"raining":"snowing").". Would you share your umbrella with the cute person beside you?";
					mysql_query("INSERT INTO Ushare_Message (uid, type, content) VALUES (".$row['id'].", 0, '".mysql_real_escape_string($mContent)."')");

					// send notification
					pushNotification($row['rid'], "System Message", "Ushare Miss You", $mContent);
				}
			}		

		}	
		// header
		header('Content-Type:text/plain; charset=utf-8');
	}

	else if($_GET['action']=="signup")
	{
		header('Content-Type: text/html; charset=utf-8');
		if($_SERVER['REQUEST_METHOD']=='POST') {
			if (empty($_POST['username'])||empty($_POST['email'])||empty($_POST['password'])||empty($_POST['verification'])){
				$errorMessage = "All fields are required";
				include("signup_mian.php");
			} else {
				$userName = $_POST['username'];
				if(!userNameValidate($userName)) {
					$errorMessage = "Username may not contain any non-url-safe characters";
					include("signup_mian.php");
				} else {
					// duplicate for security
					$con = mysql_connect("localhost","ushare","ushare2014");
					if (!$con)
					{
						// DB Error, Exist
						echo 'Could not connect: ' . mysql_error();
						exit;
					}
					mysql_select_db("ushare", $con);
					$result = mysql_query("SELECT id FROM Ushare_User WHERE name='".$userName."'");
					if(mysql_num_rows($result)>0) {
						$errorMessage = "Username already taken";
						include("signup_mian.php");
					} else {
						$userEmail = $_POST['email'];
						if (!filter_var($userEmail, FILTER_VALIDATE_EMAIL)) {
							$errorMessage = "Email is invalid";
							include("signup_mian.php");
						} else {
							$userPsd = $_POST['password'];
							$userVery = $_POST['verification'];
							if($userPsd!=$userVery) {
								$errorMessage = "Passwords don't match";
								include("signup_mian.php");
							} else {
								// gender selection
								if($_POST['gender']=="male")
									$userGender = "1";
								else if($_POST['gender']=="female")
									$userGender = "0";
								else 
									$userGender = "NULL";

								// phone num
								$userPhone = substr($_POST['phone'], 0, 11);

								// if(isset($_GET['rid'])&&strlen($_GET['rid'])==11)
								// 	$userRid = substr($_GET['rid'], 0, 11);
								// else
									// $userRid = "NULL";
								$timestamp = date('Y-m-d H:i:s', time());

								$userPsd = pwdEncrypt($userPsd, $timestamp, $userEmail);
								$result = mysql_query("INSERT INTO Ushare_User (name, gender, email, phone, password, reg_date) VALUES ('".$userName."', ".$userGender.", '".$userEmail."', '".$userPhone."', '".$userPsd."', '".$timestamp."')");
								
								if(!$result){
									$resultMessage = '<div class="bs-callout bs-callout-danger">
                    <h4>Registration Failed</h4>
                    <p>Oops! Something went wrong with your registration, please try again later.</p>
                </div>';
								}
								else{
									$resultMessage = '<div class="bs-callout bs-callout-success">
                    <h4>Registration Successful</h4>
                    <p>Well done! Your registration successfully.</p>
                </div>';
								}
								include("signup_result.php");

							}

						}
					}
				}


			}

		} else {
			$errorMessage = "";
			include("signup_mian.php");
		}

	}

	else if($_GET['action']=="onlineUsers")
	{
		$result = mysql_query("SELECT uid FROM Ushare_Ava");
		$response = mysql_num_rows($result);

	}

	else if(!isset($response)) {
		header('Content-Type: text/html; charset=utf-8');
		$response = errorTips ();
	}

} else {
	header('Content-Type: text/html; charset=utf-8');
	$response = errorTips ();
}

echo $response;

function userNameValidate($str) {
    return !preg_match('/[^A-Za-z0-9.#\\-$]/', $str);
}

function pwdEncrypt ($password, $salt1, $salt2) {
	$pwd=md5(md5(md5($password) . $salt1) . $salt2);
	return $pwd;
}

function userAuth($con, $uid, $token) {
	if (!$con)
		return -3;
	mysql_select_db("ushare", $con);
	$result = mysql_query("SELECT token, expire_time FROM Ushare_User WHERE id=".$uid, $con);
	if(mysql_num_rows($result)>0)
	{
		$row = mysql_fetch_array($result);
		if($row['token']!=$token) {
			// invalid token
			return -2;
			} else {
				if(strtotime($row['expire_time'])<time()) {
					// The token has expired
					return 0;
				} else {
					// success
					return 1;
				}
			}

	} else {
		// invalid User ID
		return -1;
	}
}

function activeStatus ($con, $uid) {
	if (!$con)
		return "minutes ago";
	mysql_select_db("ushare", $con);
	$result = mysql_query("SELECT update_time FROM Ushare_Ava WHERE uid=".$uid, $con);
	if(mysql_num_rows($result)>0) {
		$row = mysql_fetch_array($result);
		$updateInterval = time() - strtotime($row['update_time']);
		if($updateInterval<300)
			return "just now";
	}
	return "minutes ago";
}

function userSearch ($con, $mac, $uid) {
	$fmac = str_replace(":","",$mac);
	$avaUsers = array();

	if (!$con)
		return null;

	mysql_select_db("ushare", $con);
	
	$result = mysql_query("SELECT uid FROM Ushare_Ava WHERE current_bssid = x'".$fmac."'");
	if(mysql_num_rows($result)>0) {
		for($i=0; $i<mysql_num_rows($result); $i++) {
			$row = mysql_fetch_array($result);
			$uresult = mysql_query("SELECT * FROM Ushare_Hold WHERE uid=".$row['uid']);
			// prevent user is in holding status or the requester him or her self
			if($row['uid']!=$uid&&mysql_num_rows($uresult)==0) {
				// echo $row['uid'];
				$avaUsers[] = $row['uid'];
			}
		}
	}
	if(count($avaUsers)>0)
		return $avaUsers;
	else
		return null;
	// return array("8");
}

function apNearby ($con, $mac, $strict) {
	$fmac = str_replace(":","",$mac);
	$avaAPs = array();

	if (!$con)
		return null;

	mysql_select_db("ushare", $con);

	if($strict)
		$result = mysql_query(
		"SELECT BSSID1, BSSID2, Count(*)
		FROM 
			(	SELECT Ushare_Track.BSSID1, Ushare_Track.BSSID2
				FROM Ushare_Track, Ushare_Track AS cp
				WHERE Ushare_Track.BSSID1 = cp.BSSID2
				AND Ushare_Track.BSSID2 = cp.BSSID1
				AND Ushare_Track.BSSID1 < Ushare_Track.BSSID2
			)	tmp
		WHERE BSSID1 = x'".$fmac."'
		OR BSSID2 = x'".$fmac."'
		GROUP BY BSSID1, BSSID2
		ORDER BY Count(*) DESC
		LIMIT 5");
	else
		$result = mysql_query(
		"SELECT BSSID1, BSSID2, Count(*)
		FROM Ushare_Track
		WHERE BSSID1 = x'".$fmac."'
		OR BSSID2 = x'".$fmac."'
		GROUP BY BSSID1, BSSID2
		ORDER BY Count(*) DESC
		LIMIT 5");
	if(mysql_num_rows($result)>0) {
		for($i=0; $i<mysql_num_rows($result); $i++) {
			$row = mysql_fetch_array($result);
			$avaAPs[] = (int2mac($row['BSSID1'])==$fmac)?int2macaddress($row['BSSID2']):int2macaddress($row['BSSID1']);
		}
	}
	if(count($avaAPs)>0)
		return $avaAPs;
	else
		return null;

}

function getUserName ($con, $uid) {
	if (!$con)
		return "null";
	mysql_select_db("ushare", $con);
	$result = mysql_query("SELECT name FROM Ushare_User WHERE id=".$uid, $con);
	if(mysql_num_rows($result)>0) {
		$row = mysql_fetch_array($result);
		return $row['name'];
	}
	return "null";
}

function getUserUpdates ($con, $uid) {
	if (!$con)
		return "null";
	mysql_select_db("ushare", $con);
	$result = mysql_query("SELECT updates FROM Ushare_User WHERE id=".$uid, $con);
	if(mysql_num_rows($result)>0) {
		$row = mysql_fetch_array($result);
		return $row['updates'];
	}
	return "null";
}

function getUserGender ($con, $uid) {
	if (!$con)
		return -1;
	mysql_select_db("ushare", $con);
	$result = mysql_query("SELECT gender FROM Ushare_User WHERE id=".$uid, $con);
	if(mysql_num_rows($result)>0) {
		$row = mysql_fetch_array($result);
		if($row['gender'] === NULL)
			return -1;
		else if($row['gender'] == "0")
			return 0;
		else if($row['gender'] == "1")
			return 1;
	}
	return -1;
}

function getMessageUID ($con, $mid) {
	if (!$con)
		return "null";
	mysql_select_db("ushare", $con);
	$result = mysql_query("SELECT uid FROM Ushare_Message WHERE id=".$mid, $con);
	if(mysql_num_rows($result)>0) {
		$row = mysql_fetch_array($result);
		return $row['uid'];
	}
	return "null";
}

function updateUnread ($con, $mid) {
	if (!$con)
		return false;
	mysql_select_db("ushare", $con);
	$result = mysql_query("UPDATE Ushare_Message SET unread = 0 WHERE id=".$mid, $con);
	if($result)
		return true;
	return false;
}

function deleteMessage ($con, $mid) {
	if (!$con)
		return false;
	mysql_select_db("ushare", $con);
	$result = mysql_query("DELETE FROM Ushare_Message WHERE id=".$mid, $con);
	if($result)
		return true;
	return false;
}

function userListFormatter ($con, $avUsers, $uid, $token, $distance) {
	$response = "";
	for($i=0; $i<count($avUsers); $i++) {
		$active = activeStatus ($con, $avUsers[$i]);
		$gender = getUserGender($con, $avUsers[$i]);
		$name = getUserName($con, $avUsers[$i]);
		$updates = getUserUpdates($con, $avUsers[$i]);

		$temp = file_get_contents("../shake/list_framework.html");
		$temp = str_replace("USER_NAME", $name, $temp);
		$temp = str_replace("ACTIVE_TIME", $active, $temp);
		$temp = str_replace("DISTANCE", $distance, $temp);
		$temp = str_replace("AVATAR_URL", "../rsc/avatar/".$avUsers[$i].".png", $temp);
		$temp = str_replace("REQUEST_URL", "../rsc/shareRequest&uid=".$avUsers[$i]."&request=".$uid."&token=".$token, $temp);
		if($gender==0) {
			$temp = str_replace("GENDER_ICON", "./img/Female_small.png", $temp);
			$temp = str_replace("USER_GENDER", "0", $temp);
		}
		else if($gender==1) {
			$temp = str_replace("GENDER_ICON", "./img/Male_small.png", $temp);
			$temp = str_replace("USER_GENDER", "1", $temp);
		}
		else {
			$temp = str_replace("GENDER_ICON", "./img/Unknown_small.png", $temp);
			$temp = str_replace("USER_GENDER", "-1", $temp);
		}
		$temp = str_replace("USER_UPDATES", $updates, $temp);
		$response = $response.$temp;
	}
	return $response;
}

function int2macaddress($int) {
    $hex = base_convert($int, 10, 16);
    while (strlen($hex) < 12)
        $hex = '0'.$hex;
    return strtoupper(implode(':', str_split($hex,2)));
}

function int2mac($int) {
    $hex = base_convert($int, 10, 16);
    while (strlen($hex) < 12)
        $hex = '0'.$hex;
    return strtoupper($hex);
}

function pushNotification($rid, $descr, $title, $mContent) {
	include_once 'lib/jpush_api_php_client.php';
	$master_secret = 'fbf789bb2863c41828901c6a';
	$app_key='c9f13e28eac18ed91d0694f6';
	$sendno=mt_rand();
	$client = new JpushClient($app_key,$master_secret,0);
	
	//send message by ID
	$str = $client->sendNotificationByID($rid, $sendno, $descr,
      $title ,$mContent, 'android','');
	
	// log on
	$file_handle = fopen("/var/www/ushare/api/data/notificationLog.txt", "a+");
	$file_contents = $str." - ".strftime('%c')."\n";
	fwrite($file_handle, $file_contents);
	fclose($file_handle);
}

/* error tips */
function errorTips () {
	return <<<HTML
<html>
<head>
<title>The page is temporarily unavailable</title>
<style>
body { font-family: Tahoma, Verdana, Arial, sans-serif; }
</style>
</head>
<body bgcolor="white" text="black">
<table width="100%" height="100%">
<tr>
<td align="center" valign="middle">
The page you are looking for is temporarily unavailable.<br/>
Please try again later.
</td>
</tr>
</table>
</body>
</html>
HTML;
}
?>
