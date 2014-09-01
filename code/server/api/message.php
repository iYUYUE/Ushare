<?php
  include_once 'lib/jpush_api_php_client.php';
  $master_secret = 'fbf789bb2863c41828901c6a';
  $app_key='c9f13e28eac18ed91d0694f6';
  $sendno=mt_rand();

  // echo phpinfo();
  $client = new JpushClient($app_key,$master_secret,0);
  
  //send message by ID
  $sendstr = $client->sendNotificationByID('060a8504373', $sendno, 'des',
                      'tag notify title','tag notify content', 'android','');

  $msg_id = json_decode($sendstr)->{"msg_id"};
  // echo $sendstr."\n";
  time_nanosleep(1, 300000000);

  $recstr = $client->getReceivedApi($msg_id);

  $item = json_decode($recstr);

  $item = $item[0];

  echo $item->{"android_received"};

  if($item->{"android_received"}==1)
    echo $recstr."\n";
?>