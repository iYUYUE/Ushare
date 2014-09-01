function mac2int(str) {
  if(str.length!=17)
    return "";
  var macAddressParts = str.split(":");
  var result = "0x";
  for(var i=0; i<6; i++){
    result = result.concat(macAddressParts[i]);
  }

  return parseInt(result).toString();
}