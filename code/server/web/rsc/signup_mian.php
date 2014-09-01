<!DOCTYPE html>
<!-- this form template (including html+css+js) is based on zeMirco's ng-signup-form
Source: https://github.com/zeMirco/ng-signup-form/
-->

<html ng-app="myApp" class="ng-scope">
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta charset="UTF-8">
        <title>
        	Ushare - Sign Up
        </title>
        <meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=2.0"/>
        <link rel="stylesheet" href="./css/bootstrap.min.css">
        <link rel="stylesheet" href="./css/halflings.css">
        <link rel="stylesheet" href="./css/style.css">
        <style type="text/css">
        .small-select {
            width:25%;
        }
        .small-input {
            width:70%;
        }
        .placehold5 {
            width:5%;
        }
        .placehold3 {
            width:3%;
        }
        .gp {
            display: -webkit-inline-box;   /* OLD: Safari,  iOS, Android browser, older WebKit browsers.  */
            display: -moz-inline-box;      /* OLD: Firefox (buggy) */ 
            display: -ms-inline-flexbox;   /* MID: IE 10 */
            display: -webkit-inline-flex;  /* NEW, Chrome 21–28, Safari 6.1+ */
            display: inline-flex;          /* NEW: IE11, Chrome 29+, Opera 12.1+, Firefox 22+ */
/*            -webkit-flex-direction: row;
            vertical-align: baseline;*/
            font-size: 0;
        }
        </style>
        <style type="text/css">
            @charset "UTF-8";[ng\:cloak],[ng-cloak],[data-ng-cloak],[x-ng-cloak],.ng-cloak,.x-ng-cloak{display:none;}ng\:form{display:block;}
        </style>
    </head>
    
    <body style="">
        <div class="container">
            <div class="col-lg-offset-4 col-lg-4">
                <div class="panel">
                    <div class="panel-heading">
                        Sign up
                    </div>
                    <form name="form" method="post" action="./signup" novalidate=""
                    class="ng-invalid ng-invalid-required ng-dirty ng-valid-email">
                        <div class="form-group">
                            <label for="username" class="control-label">
                                Username
                            </label>
                            <input type="text" name="username" id="username" placeholder="Username"
                            unique-username="" required="" ng-model="username" autofocus="" class="form-control ng-valid-is-taken ng-valid-invalid-chars ng-dirty ng-valid ng-valid-required">
                            <i ng-show="busy" class="halflings-icon refresh rotating" style="display: none;">
                            </i>
                            <span ng-show="form.username.$dirty &amp;&amp; form.username.$error.required"
                            class="help-block" style="display: none;">
                                Please choose a username
                            </span>
                            <span ng-show="form.username.$dirty &amp;&amp; form.username.$error.isTaken"
                            class="help-block" style="display: none;">
                                Username already taken
                            </span>
                            <span ng-show="form.username.$dirty &amp;&amp; form.username.$error.invalidChars"
                            class="help-block" style="display: none;">
                                Username may not contain any non-url-safe characters
                            </span>
                        </div>
                        <div class="form-group">
                            <label for="email" class="control-label">
                                Email
                            </label>
                            <input type="email" ng-model="email" name="email" id="email" placeholder="Email"
                            required="" class="form-control ng-dirty ng-invalid ng-invalid-required ng-valid-email">
                            <span ng-show="form.email.$dirty &amp;&amp; form.email.$error.email" class="help-block"
                            style="display: none;">
                                Email is invalid
                            </span>
                        </div>
                        <div class="form-group">
                            <label class="control-label small-select">
                                Gender
                            </label>
                            <label class="placehold3"></label>
                            <label class="control-label">
                                Phone
                            </label>

                            <div class="controls gp">
                                <select class="form-control small-select" name="gender" id="gender">
                                    <option value="other">Secret</option> 
                                    <option value="male">Male</option>
                                    <option value="female">Female</option>
                                </select>
                                <div class="placehold5"></div>
                                <div class="input-group small-input">
                                    <span class="input-group-addon">+86 </span>
                                    <input class="form-control" type="text" name="phone" id="phone" maxlength="11" placeholder="Optional">
                                </div>
                            </div>
                            
                        </div>
                        <div class="form-group">
                            <label for="password" class="control-label">
                                Password
                            </label>
                            <input type="password" name="password" id="password" ng-model="password"
                            required="" match="verification" class="form-control ng-pristine ng-invalid ng-invalid-required ng-valid-match">
                            <span ng-show="form.password.$dirty &amp;&amp; form.password.$error.required"
                            class="help-block" style="display: none;">
                                Please enter a password
                            </span>
                        </div>
                        <div class="form-group">
                            <label for="verification" class="control-label">
                                Repeat password
                            </label>
                            <input type="password" name="verification" id="verification" ng-model="verification"
                            required="" match="password" class="form-control ng-pristine ng-invalid ng-invalid-required ng-valid-match">
                            <span ng-show="form.verification.$dirty &amp;&amp; form.verification.$error.required"
                            class="help-block" style="display: none;">
                                Please repeat your password
                            </span>
                            <span ng-show="form.verification.$dirty &amp;&amp; form.verification.$error.match &amp;&amp; !form.verification.$error.required"
                            class="help-block" style="display: none;">
                                Passwords don't match
                            </span>
                        </div>
                        <?
                        if($errorMessage!="")
                            echo '<div class="alert">
                            '.$errorMessage.'
                        </div>';
                        ?>
                        <input type="submit" value="Sign up" ng-disabled="form.$invalid" class="btn btn-primary"
                        disabled="disabled">
                    </form>
                </div>
            </div>
        </div>
        <script src="./js/jquery.min.js">
        </script>
        <script src="./js/bootstrap.min.js">
        </script>
        <script src="./js/angular.js">
        </script>
        <script src="./js/script.js">
        </script>
    </body>

</html>