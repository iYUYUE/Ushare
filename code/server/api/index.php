<?php
// .htaccess
// RewriteEngine On
// RewriteBase /api
// RewriteCond $1 !(index\.php|message\.php|data)
// RewriteRule ^(.*)$  index.php?action=$1 [L]

$debugMode = false;
$XMLHeader = '<?xml version="1.0"?>';
$begin = getCurrentTime();

$con = mysql_connect("localhost","ushare","ushare2014");
if (!$con)
  {
  	// DB Error, Exist
  	echo errorMessage('Could not connect: ' . mysql_error());
  	exit;
  }

header('Content-Type: text/xml; charset=UTF-8');

if(isset($_GET['action'])){

	if($_GET['action']=="find"&&isset($_GET['mac'])&&isset($_GET['gender'])){
		$gender = intval($_GET['gender']);
		$mac = strtoupper($_GET['mac']);

		if(preg_match('/^([0-9A-F]{2}[:-]){5}([0-9A-F]{2})$/', $mac)){
			$post_string = '
			<Ushare>                   
				<type>1</type>
				<mac>' . $mac . '</mac>
				<gender>' . ($gender==1?'male':($gender==0?'female':'unknown')) . '</gender>
			';
		} else {
			$post_string = errorMessage("invalid BSSID");
		}
	}
	else if($_GET['action']=="auth"&&isset($_GET['user'])&&isset($_GET['password'])){
		$userName = $_GET['user'];
		$userPassword = $_GET['password'];
		mysql_select_db("ushare", $con);
		$lifeCycle = 48;

		$result = mysql_query("SELECT id, email, password, rid, reg_date, token, expire_time FROM Ushare_User WHERE name='".$userName."'");
		if(mysql_num_rows($result)>0)
		{
			$row = mysql_fetch_array($result);
			if($row['password'] == pwdEncrypt($userPassword, $row['reg_date'], $row['email'])) {
				if($row['token']!=NULL && $row['expire_time']!=NULL && strtotime($row['expire_time'])>=time()){
					$token = $row['token'];
					if(isset($_GET['rid'])&&strlen($_GET['rid'])==11){
						$userRid = substr($_GET['rid'], 0, 11);
						if(is_null($row['rid']) || $row['rid']!=$userRid) {
							mysql_query("UPDATE Ushare_User SET rid = NULL WHERE rid='".$userRid."'");
							if(!mysql_query("UPDATE Ushare_User SET rid = '".$userRid."' WHERE name='".$userName."'"))
								$token = NULL;
						}
					}
				} else {
					$token = tokenGenerator($userName);
					if(isset($_GET['rid'])&&strlen($_GET['rid'])==11){
						$userRid = substr($_GET['rid'], 0, 11);
						if(is_null($row['rid']) || $row['rid']!=$userRid) {
							mysql_query("UPDATE Ushare_User SET rid = NULL WHERE rid='".$userRid."'");
							if(!mysql_query("UPDATE Ushare_User SET rid = '".$userRid."' WHERE name='".$userName."'"))
								$token = NULL;
						}
					}
					if(!mysql_query("UPDATE Ushare_User SET expire_time = NOW() + INTERVAL " .$lifeCycle ." HOUR, token = '".$token."'
						WHERE name='".$userName."'"))
						$token = NULL;
				}
				$post_string = '
				<Ushare>                   
					<type>1</type>
					<auth>
						<status>success</status>
						<uid>'.$row['id'].'</uid>
						<token>'.$token.'</token>
					</auth>
				';
			} else {
				$post_string = '
				<Ushare>                   
					<type>1</type>
					<auth>
						<status>fail</status>
	    				<message>PASSWORD NOT MATCH</message>
					</auth>
				';
			}

		} else {
			$post_string = '
			<Ushare>                   
				<type>1</type>
				<auth>
					<status>fail</status>
    				<message>USER NOT FOUND</message>
				</auth>
			';
		}

	}
	else if($_GET['action']=="share"&&isset($_GET['bssid'])&&isset($_GET['uid'])&&isset($_GET['token'])) {
		$tempBSSIDArray = split("-", $_GET['bssid'], 2);
		if($tempBSSIDArray) {
			$mac = strtoupper($tempBSSIDArray[0]);
			if(strlen($tempBSSIDArray[1])>0)
				$ssid = substr($tempBSSIDArray[1], 0, 64);
		} else {
			$mac = strtoupper($_GET['bssid']);
		}
		
		$uid = $_GET['uid'];
		$token = $_GET['token'];
		mysql_select_db("ushare", $con);

		if(!preg_match('/^([0-9A-F]{2}[:-]){5}([0-9A-F]{2})$/', $mac)){
			$post_string = '
			<Ushare>                   
				<type>1</type>
				<share>
					<status>fail</status>
    				<message>invalid BSSID</message>
				</share>
				';
		} else {
			$result = mysql_query("SELECT token, expire_time FROM Ushare_User WHERE id=".$uid);
			if(mysql_num_rows($result)>0)
			{
				$row = mysql_fetch_array($result);
				if($row['token']!=$token) {
					$post_string = '
					<Ushare>                   
						<type>1</type>
						<share>
							<status>fail</status>
		    				<message>invalid token</message>
						</share>
						';
					} else {
						if(strtotime($row['expire_time'])<time()) {
							$post_string = '
							<Ushare>                   
								<type>1</type>
								<share>
									<status>fail</status>
				    				<message>The token has expired</message>
								</share>
								';
						} else {
							$fmac = str_replace(":","",$mac);
							$result = mysql_query("SELECT current_bssid, current_ssid, update_time FROM Ushare_Ava WHERE uid=".$uid);

							if(mysql_num_rows($result)>0) {
								$row = mysql_fetch_array($result);

								// mac address filter (ignore 00:00:00:00:00:00)
								if($fmac == "000000000000")
									$fmac = int2mac($row['current_bssid']);

								// maintain User Online Stauts
								if(isset($ssid))
									mysql_query("UPDATE Ushare_Ava SET current_bssid = x'".$fmac."', current_ssid = '".$ssid."', update_time = NOW() WHERE uid=".$uid);
								else
									mysql_query("UPDATE Ushare_Ava SET current_bssid = x'".$fmac."', current_ssid = NULL, update_time = NOW() WHERE uid=".$uid);
								
								// User Track
								if(int2mac($row['current_bssid'])!=$fmac) {

									$updateInterval = time() - strtotime($row['update_time']);
									// If the update interval is more than 5 mins, not update the user trace
									if($updateInterval<300)
										$tmp = mysql_query("INSERT INTO Ushare_Track (uid, BSSID1, BSSID2".(($row['current_ssid']===NULL)?"":", SSID1").(isset($ssid)?", SSID2":"").") VALUES (".$uid.", ".$row['current_bssid'].", x'".$fmac."'".(($row['current_ssid']===NULL)?"":(", '".$row['current_ssid']."'")).(isset($ssid)?(", '".$ssid."'"):"").")");

								}

							} else {
								if(isset($ssid))
									mysql_query("INSERT INTO Ushare_Ava (uid, current_bssid, current_ssid) VALUES (".$uid.", x'".$fmac."', '".$ssid."')");
								else
									mysql_query("INSERT INTO Ushare_Ava (uid, current_bssid) VALUES (".$uid.", x'".$fmac."')");
							}

							$post_string = '
							<Ushare>                   
								<type>1</type>
								<share>
									<status>success</status>
								</share>
								';

							// log on
							$file_handle = fopen("/var/www/ushare/api/data/shareLog.txt", "a+");
							$file_contents = $_GET['uid'] . ";" . $_GET['bssid']. ";" . strftime('%c') . "/";

							fwrite($file_handle, $file_contents);
							fclose($file_handle);

						}
					}

			} else {
				$post_string = '
				<Ushare>                   
					<type>1</type>
					<share>
						<status>fail</status>
	    				<message>invalid User ID</message>
					</share>
					';
			}
			
		}
	}
	else if($_GET['action']=="stopShare"&&isset($_GET['uid'])&&isset($_GET['token']))
	{
		$uid = $_GET['uid'];
		$token = $_GET['token'];
		mysql_select_db("ushare", $con);

		$result = mysql_query("SELECT token, expire_time FROM Ushare_User WHERE id=".$uid);
		if(mysql_num_rows($result)>0)
		{
			$row = mysql_fetch_array($result);
			if($row['token']!=$token) {
				$post_string = '
				<Ushare>                   
					<type>1</type>
					<stopShare>
						<status>fail</status>
	    				<message>invalid token</message>
					</stopShare>
					';
				} else {
					if(strtotime($row['expire_time'])<time()) {
						$post_string = '
						<Ushare>                   
							<type>1</type>
							<stopShare>
								<status>fail</status>
			    				<message>The token has expired</message>
							</stopShare>
							';
					} else {
						$result = mysql_query("DELETE FROM Ushare_Ava WHERE uid=".$uid);

						if($result)
							$post_string = '
							<Ushare>                   
								<type>1</type>
								<stopShare>
									<status>success</status>
								</stopShare>
								';
						else
							$post_string = '
							<Ushare>                   
								<type>1</type>
								<stopShare>
									<status>success</status>
			    					<message>The user is not active</message>
								</stopShare>
								';
					}
				}

		} else {
			$post_string = '
			<Ushare>                   
				<type>1</type>
				<stopShare>
					<status>fail</status>
    				<message>invalid User ID</message>
				</stopShare>
				';
		}


	}
	else if($_GET['action']=='shareclean') {
		$file_handle = fopen("/var/www/ushare/api/data/shareLog.txt", "w");
		// $file_contents = "        UID      BSSID                TIMESTAMP\n";
		$file_contents = "";

		fwrite($file_handle, $file_contents);
		fclose($file_handle);

	}
	else if($_GET['action']=='sleep') {
		sleep (10);
	}
	else if($_GET['action']=='answerRequest'&&isset($_GET['mid'])&&isset($_GET['uid'])&&isset($_GET['token'])&&isset($_GET['type'])) {
		$mid = $_GET['mid'];
		$uid = $_GET['uid'];
		$token = $_GET['token'];
		$type = $_GET['type'];
		mysql_select_db("ushare", $con);
		$result = mysql_query("SELECT token, expire_time FROM Ushare_User WHERE id=".$uid);
		if(mysql_num_rows($result)>0)
		{
			$row = mysql_fetch_array($result);
			if($row['token']!=$token) {
				$post_string = '
				<Ushare>                   
					<type>1</type>
					<answerRequest>
						<status>fail</status>
	    				<message>invalid token</message>
					</answerRequest>
					';
				} else {
					if(strtotime($row['expire_time'])<time()) {
						$post_string = '
						<Ushare>                   
							<type>1</type>
							<answerRequest>
								<status>fail</status>
			    				<message>The token has expired</message>
							</answerRequest>
							';
					} else {
						$result = mysql_query("SELECT uid, uid_r, type, answer FROM Ushare_Message WHERE id=".$mid, $con);
						if(mysql_num_rows($result)>0) {
							$row = mysql_fetch_array($result);
							if($row['uid']!=$uid) {
								$post_string = '
								<Ushare>                   
									<type>1</type>
									<answerRequest>
										<status>fail</status>
										<message>invalid message ownership</message>
									</answerRequest>
									';
							} else {
								if($row['answer']=='0'&&$row['type']=='1') {
									mysql_query("UPDATE Ushare_Message SET answer = 1 WHERE id=".$mid, $con);
									$result1 = mysql_query("SELECT id, name, rid FROM Ushare_User WHERE id=".$uid);
									$result2 = mysql_query("SELECT id, name, rid FROM Ushare_User WHERE id=".$row['uid_r']);
									
									// user who share
									$row1 = mysql_fetch_array($result1);
									// the requster
									$row2 = mysql_fetch_array($result2);
									
									if($type=='1') {
										$mContent = "Good news! ".$row1['name']." has accepted your request. Your contacts will be exchanged shortly.";
									} else {
										$mContent = "Sorry! ".$row1['name']." may not be available now. Please try again later.";
									}

									mysql_query("INSERT INTO Ushare_Message (uid, uid_r, type, content) VALUES (".$row['uid_r'].", ".$uid.", 2, '".$mContent."')");

									pushNotification($row2['rid'], 'Ushare Response', 'New Response from Ushare', $mContent);

									// release user
									$result = mysql_query("DELETE FROM Ushare_Hold WHERE uid=".$uid);

									// send contacts
									if ($type=='1')
									{
										$mContent = "Please check the contacts of ".$row1['name'].". The message will be valid for next 12 hours.";
										mysql_query("INSERT INTO Ushare_Message (uid, uid_r, type, content) VALUES (".$row2['id'].", ".$row1['id'].", 3, '".$mContent."')");

										pushNotification($row2['rid'], 'Ushare Contacts', 'New Contacts from Ushare', $mContent);

										$mContent = "Please check the contacts of ".$row2['name'].". The message will be valid for next 12 hours.";
										mysql_query("INSERT INTO Ushare_Message (uid, uid_r, type, content) VALUES (".$row1['id'].", ".$row2['id'].", 3, '".$mContent."')");
										
										pushNotification($row1['rid'], 'Ushare Contacts', 'New Contacts from Ushare', $mContent);
									}

									$post_string = '
									<Ushare>                   
										<type>1</type>
										<answerRequest>
											<status>success</status>
										</answerRequest>
										';
								}
								else{
									$post_string = '
									<Ushare>                   
										<type>1</type>
										<answerRequest>
											<status>fail</status>
											<message>message already answered</message>
										</answerRequest>
										';
								}
							}
						} else {
							$post_string = '
							<Ushare>                   
								<type>1</type>
								<answerRequest>
									<status>fail</status>
									<message>invalid message id</message>
								</answerRequest>
								';
						}
					}
				}

		} else {
			$post_string = '
			<Ushare>                   
				<type>1</type>
				<answerRequest>
					<status>fail</status>
    				<message>invalid User ID</message>
				</answerRequest>
				';
		}
	}
	else if($_GET['action']=='getContacts'&&isset($_GET['mid'])&&isset($_GET['uid'])&&isset($_GET['token'])) {
		$mid = $_GET['mid'];
		$uid = $_GET['uid'];
		$token = $_GET['token'];

		mysql_select_db("ushare", $con);
		$result = mysql_query("SELECT token, expire_time FROM Ushare_User WHERE id=".$uid);
		if(mysql_num_rows($result)>0)
		{
			$row = mysql_fetch_array($result);
			if($row['token']!=$token) {
				$post_string = '
				<Ushare>                   
					<type>1</type>
					<getContacts>
						<status>fail</status>
	    				<message>invalid token</message>
					</getContacts>
					';
				} else {
					if(strtotime($row['expire_time'])<time()) {
						$post_string = '
						<Ushare>                   
							<type>1</type>
							<getContacts>
								<status>fail</status>
			    				<message>The token has expired</message>
							</getContacts>
							';
					} else {
						$result = mysql_query("SELECT uid, uid_r, type, rec_date FROM Ushare_Message WHERE id=".$mid, $con);
						if(mysql_num_rows($result)>0) {
							$row = mysql_fetch_array($result);
							if($row['uid']!=$uid) {
								$post_string = '
								<Ushare>                   
									<type>1</type>
									<getContacts>
										<status>fail</status>
										<message>invalid message ownership</message>
									</getContacts>
									';
							} else {
								if($row['type']=='3' && time()-strtotime($row['rec_date'])<60*60*12) {
									$result = mysql_query("SELECT * FROM Ushare_User WHERE id=".$row['uid_r']);
									if(mysql_num_rows($result)>0)
									{
										$row = mysql_fetch_array($result);
										$post_string = '
										<Ushare>                   
											<type>1</type>
											<getContacts>
												<status>success</status>
												';
										// name, gender, email, phone, avatar, updates
										$post_string = $post_string.'<name>'.$row['name'].'</name>';
										$post_string = $post_string.'<gender>'.$row['gender'].'</gender>';
										$post_string = $post_string.'<email>'.$row['email'].'</email>';
										$post_string = $post_string.'<phone>'.$row['phone'].'</phone>';
										$post_string = $post_string.'<avatar>'."http://ushare.iyuyue.net/web/rsc/avatar/".$row['id'].".png".'</avatar>';
										$post_string = $post_string.'<updates>'.$row['updates'].'</updates>';
										$post_string = $post_string.'
											</getContacts>
											';
										

									} else {
										$post_string = '
										<Ushare>                   
											<type>1</type>
											<getContacts>
												<status>fail</status>
							    				<message>invalid target User ID</message>
											</getContacts>
											';
									}

								} else {
									$post_string = '
									<Ushare>                   
										<type>1</type>
										<getContacts>
											<status>fail</status>
											<message>The message has expired</message>
										</getContacts>
										';
								}
							}
						} else {
							$post_string = '
							<Ushare>                   
								<type>1</type>
								<getContacts>
									<status>fail</status>
									<message>invalid message id</message>
								</getContacts>
								';
						}
					}
				}

		} else {
			$post_string = '
			<Ushare>                   
				<type>1</type>
				<getContacts>
					<status>fail</status>
    				<message>invalid User ID</message>
				</getContacts>
				';
		}
	}
	else if($_GET['action']=="checkStatus"&&isset($_GET['uid'])&&isset($_GET['token'])) {
		$uid = $_GET['uid'];
		$token = $_GET['token'];
		mysql_select_db("ushare", $con);
		$result = mysql_query("SELECT token, expire_time FROM Ushare_User WHERE id=".$uid);
		if(mysql_num_rows($result)>0)
		{
			$row = mysql_fetch_array($result);
			if($row['token']!=$token) {
				$post_string = '
				<Ushare>                   
					<type>1</type>
					<check>
						<status>fail</status>
	    				<message>invalid token</message>
					</check>
					';
				} else {
					if(strtotime($row['expire_time'])<time()) {
						$post_string = '
						<Ushare>                   
							<type>1</type>
							<check>
								<status>fail</status>
			    				<message>The token has expired</message>
							</check>
							';
					} else {
						$post_string = '
						<Ushare>                   
							<type>1</type>
							<check>
								<status>success</status>
							</check>
							';
					}
				}

		} else {
			$post_string = '
			<Ushare>                   
				<type>1</type>
				<check>
					<status>fail</status>
    				<message>invalid User ID</message>
				</check>
				';
		}

	}
	else if($_GET['action']=='messageList'&&isset($_GET['uid'])&&isset($_GET['token']))
	{
		// sleep(5);
		$uid = $_GET['uid'];
		$token = $_GET['token'];
		mysql_select_db("ushare", $con);
		$result = mysql_query("SELECT token, expire_time FROM Ushare_User WHERE id=".$uid);
		if(mysql_num_rows($result)>0)
		{
			$row = mysql_fetch_array($result);
			if($row['token']!=$token) {
				$post_string = '
				<Ushare>                   
					<type>1</type>
					<messageList>
						<status>fail</status>
	    				<message>invalid token</message>
					</messageList>
					';
				} else {
					if(strtotime($row['expire_time'])<time()) {
						$post_string = '
						<Ushare>                   
							<type>1</type>
							<messageList>
								<status>fail</status>
			    				<message>The token has expired</message>
							</messageList>
							';
					} else {
						$post_string = '
						<Ushare>                   
							<type>1</type>
							<messageList>
								<status>success</status>
								';
						$result = mysql_query("SELECT id, unread, type, content, rec_date FROM Ushare_Message WHERE uid=".$uid." ORDER BY  Ushare_Message.rec_date ASC");
						$num = mysql_num_rows($result);
						$post_string = $post_string.'<num>'.$num.'</num>';
						while($row = mysql_fetch_array($result)) {
							$post_string = $post_string.'<message>';
							$post_string = $post_string.'<id>'.$row['id'].'</id>';
							$post_string = $post_string.'<unread>'.$row['unread'].'</unread>';
							$post_string = $post_string.'<type>'.$row['type'].'</type>';
							$post_string = $post_string.'<content>'.$row['content'].'</content>';
							$post_string = $post_string.'<rec_date>'.$row['rec_date'].'</rec_date>';
							$post_string = $post_string.'</message>';
						}
						$post_string = $post_string.'
							</messageList>
							';
					}
				}

		} else {
			$post_string = '
			<Ushare>                   
				<type>1</type>
				<messageList>
					<status>fail</status>
    				<message>invalid User ID</message>
				</messageList>
				';
		}
	}
	else if($_GET['action']=='userInfo'&&isset($_GET['uid'])&&isset($_GET['token']))
	{
		// sleep(5);
		$uid = $_GET['uid'];
		$token = $_GET['token'];
		mysql_select_db("ushare", $con);
		$result = mysql_query("SELECT * FROM Ushare_User WHERE id=".$uid);
		if(mysql_num_rows($result)>0)
		{
			$row = mysql_fetch_array($result);
			if($row['token']!=$token) {
				$post_string = '
				<Ushare>                   
					<type>1</type>
					<userInfo>
						<status>fail</status>
	    				<message>invalid token</message>
					</userInfo>
					';
				} else {
					if(strtotime($row['expire_time'])<time()) {
						$post_string = '
						<Ushare>                   
							<type>1</type>
							<userInfo>
								<status>fail</status>
			    				<message>The token has expired</message>
							</userInfo>
							';
					} else {
						$post_string = '
						<Ushare>                   
							<type>1</type>
							<userInfo>
								<status>success</status>
								';
						// name, gender, email, phone, avatar, updates
						$post_string = $post_string.'<name>'.$row['name'].'</name>';
						$post_string = $post_string.'<gender>'.$row['gender'].'</gender>';
						$post_string = $post_string.'<email>'.$row['email'].'</email>';
						$post_string = $post_string.'<phone>'.$row['phone'].'</phone>';
						$post_string = $post_string.'<avatar>'."http://ushare.iyuyue.net/web/rsc/avatar/".$uid.".png".'</avatar>';
						$post_string = $post_string.'<updates>'.$row['updates'].'</updates>';
						$post_string = $post_string.'
							</userInfo>
							';
					}
				}

		} else {
			$post_string = '
			<Ushare>                   
				<type>1</type>
				<userInfo>
					<status>fail</status>
    				<message>invalid User ID</message>
				</userInfo>
				';
		}
	}
	else if($_GET['action']=='userInfoEdit'&&isset($_GET['uid'])&&isset($_GET['token'])&&isset($_GET['type'])&&isset($_GET['value']))
	{
		// sleep(5);
		$type = $_GET['type'];
		$uid = $_GET['uid'];
		$token = $_GET['token'];
		mysql_select_db("ushare", $con);
		$result = mysql_query("SELECT * FROM Ushare_User WHERE id=".$uid);
		if(mysql_num_rows($result)>0)
		{
			$row = mysql_fetch_array($result);
			if($row['token']!=$token) {
				$post_string = '
				<Ushare>                   
					<type>1</type>
					<userInfoEdit>
						<status>fail</status>
	    				<message>invalid token</message>
					</userInfoEdit>
					';
				} else {
					if(strtotime($row['expire_time'])<time()) {
						$post_string = '
						<Ushare>                   
							<type>1</type>
							<userInfoEdit>
								<status>fail</status>
			    				<message>The token has expired</message>
							</userInfoEdit>
							';
					} else {
						// gender, email, phone
						if($type=="gender") {
							$value = intval($_GET['value']);
							$editResult = mysql_query("UPDATE Ushare_User SET gender = ".(($value==1 || $value==0)?$value:"NULL")." WHERE id=".$uid);

							$post_string = '
							<Ushare>                   
								<type>1</type>
								<userInfoEdit>
									<status>success</status>
								</userInfoEdit>
								';
						}
						else if ($type=="email") {
							$value = substr(mysql_real_escape_string($_GET['value']), 0, 120);
							$editResult = mysql_query("UPDATE Ushare_User SET email = '".$value."' WHERE id=".$uid);

							$post_string = '
							<Ushare>                   
								<type>1</type>
								<userInfoEdit>
									<status>success</status>
								</userInfoEdit>
								';
						}
						else if ($type=="phone") {
							$value = substr(mysql_real_escape_string($_GET['value']), 0, 11);
							$editResult = mysql_query("UPDATE Ushare_User SET phone = '".$value."' WHERE id=".$uid);

							$post_string = '
							<Ushare>                   
								<type>1</type>
								<userInfoEdit>
									<status>success</status>
								</userInfoEdit>
								';

						}
						else if ($type=="updates") {
							$value = rtrim(substr(mysql_real_escape_string($_GET['value']), 0, 140));
							$editResult = mysql_query("UPDATE Ushare_User SET updates = '".$value."' WHERE id=".$uid);

							$post_string = '
							<Ushare>                   
								<type>1</type>
								<userInfoEdit>
									<status>success</status>
								</userInfoEdit>
								';

						} else {
							$post_string = '
							<Ushare>                   
								<type>1</type>
								<userInfoEdit>
									<status>fail</status>
									<message>This type of information is read-only or unavailable</message>
								</userInfoEdit>
								';
						}
						
					}
				}

		} else {
			$post_string = '
			<Ushare>                   
				<type>1</type>
				<userInfoEdit>
					<status>fail</status>
    				<message>invalid User ID</message>
				</userInfoEdit>
				';
		}
	}
	else if(false) {

	}

	// no action matched
	if(!isset($post_string))
		$post_string = errorMessage("Parameter may be missing or unrecognized");

} else {
	// action undefined
	$post_string = errorMessage("Action is undefined or missing");
}

$end = getCurrentTime();
$spend = substr($end-$begin, 0, 6);
$debugInfo = '<Debug>
		<spread>'.$spend.'</spread>
	</Debug>';

echo $XMLHeader . xmlFormater($post_string . (($debugMode)?$debugInfo:'') .'
</Ushare>');

// for test
// var_dump($_SERVER);


function errorMessage ($message) {
	$error_string = '
	<Ushare>
		<type>-1</type>
		<message>' . $message . '</message>
	';
	return $error_string;
}

function xmlFormater ($stirng) {
	$result = preg_replace('/\s*$^\s*/m', "\n", $stirng);
	return preg_replace('/[ \t]+/', ' ', $result);
}

function pwdEncrypt ($password, $salt1, $salt2) {
	$pwd=md5(md5(md5($password) . $salt1) . $salt2);
	return $pwd;
}

function tokenGenerator($salt) {
	return substr(md5($salt.time()),0,16);
}

function getCurrentTime ()  
{  
    list ($msec, $sec) = explode(" ", microtime());  
    return (float)$msec + (float)$sec;  
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

?>