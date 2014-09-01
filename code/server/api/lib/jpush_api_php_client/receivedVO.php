<?php
/**
 * 接收信息对象
 * @author xinxin@jpush.cn
 *
 */
class ReceivedVO
{
	
    //appkey
    private $app_key;
    
    //密匙
    private $masterSecret;
    
    //用户信息
    private $auth;
    
    //msg_ids
    private $msg_ids;
    
    /**
     * 构造函数
     * @param unknown $app_key
     * @param unknown $masterSecret
     * @param unknown $msg_ids
     */
    public function __construct($app_key, $masterSecret, $msg_ids)
    {
    	$this->app_key      = $app_key;
    	$this->masterSecret = $masterSecret;
    	$this->msg_ids      = $msg_ids;
    }
    
    /**
     * 获取用户信息字符串
     * @return string
     */
    public function getAuthStr()
    {
    	return $this->app_key.":".$this->masterSecret;
    }
    
    /**
     * 设置用户信息
     * @param unknown $authStr
     */
    public function setAuth($authStr)
    {
    	$this->auth = $authStr;
    }	
    
    /**
     * 获取用户信息
     * @param unknown $authStr
     */
    public function getAuth()
    {
    	return $this->auth;
    }	
    
    /**
     * 返回url参数
     * @return string
     */
    public function getParams()
    {
    	// return "app_key=".$this->app_key."&msg_ids=".$this->msg_ids;
        return "msg_ids=".$this->msg_ids;
    }
}

?>