<?php
// echo $_GET['type'];
  if(isset($_GET['type']) && $_GET['type']=="flow")
  {
    // $count = 1;
    // get contents of a file into a string
    $filename = "/var/www/ushare/api/data/shareLog.txt";
    $handle = fopen($filename, "r");
    if(filesize($filename)>0) {
      $contents = fread($handle, filesize($filename));
      $pieces = explode("/", $contents);
      fclose($handle);
      for($i=0; $i<sizeof($pieces)-1; $i++) {
        $paras = explode(";", $pieces[$i]);
        list($bssid, $ssid) = split ("-", $paras[1], 2); 
        echo '<tr>
        <td>'.$paras[0].'</td>
        <td>'.$bssid.'</td>
        <td>'.$ssid.'</td>
        <td>'.$paras[2].'</td>
      </tr>';
      }
    }
  }
  else if(isset($_GET['type']) && $_GET['type']=="transaction")
  {
    $con = mysql_connect("localhost","ushare","ushare2014");
          if (!$con)
          {
            // DB Error, Exist
            echo errorMessage('Could not connect: ' . mysql_error());
            exit;
          }
          mysql_select_db("ushare", $con);
          $result = mysql_query("SELECT * FROM `Ushare_Message` WHERE type=1 ORDER BY  `Ushare_Message`.`rec_date` DESC LIMIT 0 , 16");
          if(mysql_num_rows($result)>0) {
            for($i=0; $i<mysql_num_rows($result); $i++) {
              $row = mysql_fetch_array($result);
              
              $uName = "Unknown";
              $uName_r = "Unknown";

              $result_c = mysql_query("SELECT name FROM Ushare_User WHERE id=".$row['uid_r']);
              if(mysql_num_rows($result_c)>0) {
                $row_c = mysql_fetch_array($result_c);
                $uName_r = $row_c['name'];
              }

              $result_c = mysql_query("SELECT name FROM Ushare_User WHERE id=".$row['uid']);
              if(mysql_num_rows($result_c)>0) {
                $row_c = mysql_fetch_array($result_c);
                $uName = $row_c['name'];
              }

              echo '<tr>
                <td>'.$row['id'].'</td>
                <td>'.$uName_r.' ( UID: '.$row['uid_r'].' )'.'</td>
                <td>'.$uName.' ( UID: '.$row['uid'].' )'.'</td>
                <td>'.$row['rec_date'].'</td>
                <td>'.($row['answer']=="1"?"Done":"Pending").'</td>
              </tr>';
            }

          } else {
            echo '<tr>
              <td></td>
              <td></td>
              <td>No new transaction</td>
              <td></td>
              <td></td>
            </tr>';
          }

  }
  else if($_SERVER['REQUEST_METHOD']=='POST' && isset($_GET['type']) && $_GET['type']=="versioncontrol")
  {
    if(!isset($_POST["code"])||!isset($_POST["name"])||!isset($_POST["content"])) {
      header('HTTP/1.0 403 Forbidden');
      echo "-1"; // parameter error
    } else {
      $versionCode = $_POST['code'];
      $versionName = $_POST['name'];
      $updatesContent = $_POST['content'];
      $filename = "/var/www/ushare/install/latest.json";

      if (is_writable($filename)) {
        // open file
        if (!$file_handle = fopen($filename, 'w')) {
             header('HTTP/1.0 403 Forbidden');
             echo "0";
             exit;
        }

        // $contents = fread($handle, filesize($filename));
        $file_contents = '{
          "version_code": '.$versionCode.',
          "content": "Version '.$versionName.' '.$updatesContent.'"
          }';

        // write
        if (fwrite($file_handle, $file_contents) === FALSE) {
            header('HTTP/1.0 403 Forbidden');
            echo "0";
            exit;
        }
        fclose($file_handle);
        header("HTTP/1.1 200 OK");
        echo "1";
      } else {
          header('HTTP/1.0 403 Forbidden');
          echo "0";
      }
      
    }
    
  }

  else if(isset($_GET['type']) && $_GET['type']=="getversion")
  {
    $filename = "/var/www/ushare/install/latest.json";
    $handle = fopen($filename, "r");
    $contents = fread($handle, filesize($filename));
    fclose($handle);
    
    $versionInfo = json_decode($contents);

    $versionCode = $versionInfo->{'version_code'};
    $versionContent = $versionInfo->{'content'};
    $versionName = substr($versionContent, 8, strpos($versionContent, "<p>")-9);
    $versionContent = substr($versionContent, strpos($versionContent, "<li>")+4, strlen($versionContent)-strpos($versionContent, "<li>")-9);
    $versionContent = str_replace("</li><li>","\n",$versionContent );
    // $features = split("</li><li>", $versionContent);
    // var_dump($features);
    echo $versionCode."|".$versionName."|".$versionContent;

  } else {
    echo errorTips();
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