var opts = {
  lines: 12, // The number of lines to draw
  length: 14, // The length of each line
  width: 6, // The line thickness
  radius: 12, // The radius of the inner circle
  corners: 1, // Corner roundness (0..1)
  rotate: 0, // The rotation offset
  direction: 1, // 1: clockwise, -1: counterclockwise
  color: '#000', // #rgb or #rrggbb or array of colors
  speed: 1, // Rounds per second
  trail: 60, // Afterglow percentage
  shadow: false, // Whether to render a shadow
  hwaccel: false, // Whether to use hardware acceleration
  className: 'spinner', // The CSS class to assign to the spinner
  zIndex: 2e9, // The z-index (defaults to 2000000000)
  top: '50%', // Top position relative to parent in px
  left: '50%' // Left position relative to parent in px
};

var clickedBox = null;

function getid(id) {
  return(typeof id == 'string') ? document.getElementById(id) : id
};

$(document).ready(function () {
    var bssid = getParameter()["bssid"];
    var uid = getParameter()["uid"];
    var token = getParameter()["token"];
    if(bssid==null || uid==null || token==null) {
      var url = "../rsc/list";
    } else {
      var url = "../rsc/shake&bssid="+bssid+"&uid="+uid+"&token="+token;
    }
    $(".content").load(url+" #Main", function() {
//    $(".content").load("./list.html #Main", function() {
      init();
    });
    
});

function init()
{
  $('#checkboxG4').change(
    function(){
        if ($(this).is(':checked')) {
          // check
          $(".sep20[gender=0]").css("display", "block");
          $(".box[gender=0]").css("display", "block"); 
        } else {
          // uncheck
          $(".sep20[gender=0]").css("display", "none");
          $(".box[gender=0]").css("display", "none"); 
        }
    });

  $('#checkboxG5').change(
    function(){
        if ($(this).is(':checked')) {
          // check
          $(".sep20[gender=1]").css("display", "block");
          $(".box[gender=1]").css("display", "block");
        } else {
          // uncheck
          $(".sep20[gender=1]").css("display", "none");
          $(".box[gender=1]").css("display", "none");
        }
    });

  $('.hover').bind('touchstart touchend', function(e) {
      e.preventDefault();
      $(this).toggleClass('hover_effect');
  });

  // shadow for selected entry
  $('.box').hover(function() {
      $(this).children('#overlay').fadeIn(300);
  }, function() {
      $(this).children('#overlay').stop().fadeOut(300);
  });
  
  // box click
  $('.box').click(function(){
      // record clickedBox
      clickedBox = $(this);
      // get target
      // var target = $(this).children('#overlay')[0];
      
      // add loading text
      // var el = document.createElement('div')
      // el.setAttribute('class', 'textInShadow');
      // el.innerHTML = "Sending Request";
      // target.insertBefore(el, target.firstChild||null);
      
      // add loading animation
      // var spinner = new Spinner(opts).spin(target);

      $(this).children('#overlay').stop().fadeOut(30);
      
      window.location.href="#modal-text";
      
  });

  $('.commentsubmit').click(function(){
    window.location.href="#!";
    clickedBox.children('#overlay').fadeIn(300);

    var target = clickedBox.children('#overlay')[0];
    var el = document.createElement('div')
    el.setAttribute('class', 'textInShadow');
    el.innerHTML = "Sending Request";
    target.insertBefore(el, target.firstChild||null);
      
    // add loading animation
    var spinner = new Spinner(opts).spin(target);

    var comment_content = getid("comment").value;

    var uContent = clickedBox.attr('url');
    if(typeof uContent == "undefined" || uContent == null)
        uContent = "../rsc/result_fail";
    $('.content').load(uContent+"&note="+comment_content.split(" ").join("+")+" #Main");

    // stop animation
    // spinner.stop();

  });
}

//function requestHandler(url, e) {
//    var target = document.getElementById(e);
//    var spinner = new Spinner(opts).spin(target);
//    window.location.href = url;
////    spinner.stop();
//}

// get URL parameter
function getParameter() {
  var URLParams = new Array();
  var aParams = document.location.search.substr(1).split('&');
  for (i=0; i < aParams.length; i++){
   var aParam = aParams[i].split('=');
   if (!URLParams[aParam[0]]) {
    URLParams[aParam[0]] = aParam[1];
   }
  }
  return URLParams;
}

