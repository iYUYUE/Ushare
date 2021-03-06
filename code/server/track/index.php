<?php
require_once('./preheader.php');
include ('./ajaxCRUD.class.php');

 #this one line of code is how you implement the class
########################################################
##

$tblDemo = new ajaxCRUD("Track", "Ushare_Track", "");
?>
<!DOCTYPE html PUBLIC"-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

		<?php $tblDemo->insertHeader(); ?>

		<!-- these js/css includes are ONLY to make the calendar widget work (fldDateMet);
			 these includes are not necessary for the class to work!! -->
		<link rel="stylesheet" href="includes/jquery.ui.all.css">
		<link rel="stylesheet" href="includes/css/bootstrap.min.css">
		<style type="text/css">
		.page-header {
			margin-top:0px;
		}
        </style>
		<script src="includes/jquery.ui.core.js"></script>
		<!--script src="includes/jquery.ui.widget.js"></script-->
		<script src="includes/jquery.ui.datepicker.js"></script>
		<script src="includes/main.js"></script>
	</head>

<?php
##
########################################################

## all that follows is setup configuration for your fields....
## full API reference material for all functions can be found here - http://ajaxcrud.com/api/
## note: many functions below are commented out (with //). note which ones are and which are not

#i can define a relationship to another table
#the 1st field is the fk in the table, the 2nd is the second table, the 3rd is the pk in the second table, the 4th is field i want to retrieve as the dropdown value
#http://ajaxcrud.com/api/index.php?id=defineRelationship
//$tblDemo->defineRelationship("fkID", "tblDemoRelationship", "pkID", "fldName", "fldSort DESC"); //use your own table - this table (tblDemoRelationship) not included in the installation script

#i don't want to visually show the primary key in the table
// $tblDemo->omitPrimaryKey();

#the table fields have prefixes; i want to give the heading titles something more meaningful
$tblDemo->displayAs("uid", "User ID");
$tblDemo->displayAs("BSSID1", "Source BSSID");
$tblDemo->displayAs("BSSID2", "Destination BSSID");
$tblDemo->displayAs("log_time", "Log Time");

#set the textarea height of the longer field (for editing/adding)
#http://ajaxcrud.com/api/index.php?id=setTextareaHeight
$tblDemo->setTextareaHeight('fldLongField', 150);

#i could omit a field if I wanted
#http://ajaxcrud.com/api/index.php?id=omitField
//$tblDemo->omitField("fldField2");

#i could omit a field from being on the add form if I wanted
//$tblDemo->omitAddField("fldField2");

#i could disallow editing for certain, individual fields
//$tblDemo->disallowEdit('fldField2');

#i could set a field to accept file uploads (the filename is stored) if wanted
//$tblDemo->setFileUpload("fldField2", "uploads/");

#i can have a field automatically populate with a certain value (eg the current timestamp)
//$tblDemo->addValueOnInsert("fldField1", "NOW()");

#i can use a where field to better-filter my table
//$tblDemo->addWhereClause("WHERE (fldField1 = 'test')");

#i can order my table by whatever i want
//$tblDemo->addOrderBy("ORDER BY fldField1 ASC");

#i can set certain fields to only allow certain values
#http://ajaxcrud.com/api/index.php?id=defineAllowableValues
// $allowableValues = array("Allowable Value1", "Allowable Value2", "Dropdown Value", "CRUD");
// $tblDemo->defineAllowableValues("fldCertainFields", $allowableValues);

//set field fldCheckbox to be a checkbox
// $tblDemo->defineCheckbox("fldCheckbox");

$tblDemo->disallowDelete();
$tblDemo->disallowAdd();
$tblDemo->turnOffAjaxEditing();
// $tblDemo->disallowEdit('uid');
// $tblDemo->disallowEdit('BSSID1');
// $tblDemo->disallowEdit('BSSID2');
// $tblDemo->disallowEdit('log_time');

#i can disallow deleting of rows from the table
#http://ajaxcrud.com/api/index.php?id=disallowDelete
//$tblDemo->disallowDelete();

#i can disallow adding rows to the table
#http://ajaxcrud.com/api/index.php?id=disallowAdd
//$tblDemo->disallowAdd();

#i can add a button that performs some action deleting of rows for the entire table
#http://ajaxcrud.com/api/index.php?id=addButtonToRow
//$tblDemo->addButtonToRow("Add", "add_item.php", "all");

#set the number of rows to display (per page)
$tblDemo->setLimit(30);

#set a filter box at the top of the table
// $tblDemo->addAjaxFilterBox('id');

#show CSV export button
$tblDemo->showCSVExportOption();

#if really desired, a filter box can be used for all fields
$tblDemo->addAjaxFilterBoxAllFields();

#i can set the size of the filter box
//$tblDemo->setAjaxFilterBoxSize('fldField1', 3);

#i can format the data in cells however I want with formatFieldWithFunction
#this is arguably one of the most important (visual) functions
$tblDemo->formatFieldWithFunction('uid', 'makeBlue');
$tblDemo->formatFieldWithFunction('BSSID1', 'makeBold');
$tblDemo->formatFieldWithFunction('BSSID2', 'makeBold');
$tblDemo->formatFieldWithFunction('SSID1', 'makeSelected');
$tblDemo->formatFieldWithFunction('SSID2', 'makeSelected');

$tblDemo->modifyFieldWithClass("log_time", "datepicker");
//$tblDemo->modifyFieldWithClass("fldField1", "zip required"); 	//for testing masked input functionality
//$tblDemo->modifyFieldWithClass("fldField2", "phone");			//for testing masked input functionality

//$tblDemo->onAddExecuteCallBackFunction("mycallbackfunction"); //uncomment this to try out an ADD ROW callback function

// $tblDemo->deleteText = "delete";

?>
<h1 class="page-header">User Track
<div style="float: right; font-weight:normal; font-size: 14px;">
		Total Returned Rows: <b><?=$tblDemo->insertRowsReturned();?></b><br />
</div>
</h1>
<div style="clear:both;"></div>
<?php

#actually show the table
$tblDemo->showTable();

#my self-defined functions used for formatFieldWithFunction
function makeBold($val){
	if ($val == "") return "no value";
	$val = int2macaddress($val);
	return "<b contenteditable=\"true\" onclick=\"document.execCommand('selectAll',false,null)\">$val</b>";
}

function makeBlue($val){
	return "<span style='color: blue;' contenteditable=\"true\" onclick=\"document.execCommand('selectAll',false,null)\">$val</span>";
}

function makeSelected($val){
	return "<span contenteditable=\"true\" onclick=\"document.execCommand('selectAll',false,null)\">$val</span>";
}

function myCallBackFunction($array){
	echo "THE ADD ROW CALLBACK FUNCTION WAS implemented";
	print_r($array);
}

function int2macaddress($int) {
    $hex = base_convert($int, 10, 16);
    while (strlen($hex) < 12)
        $hex = '0'.$hex;
    return strtoupper(implode(':', str_split($hex,2)));
}

?>