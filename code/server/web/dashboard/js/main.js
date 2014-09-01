$(function () {
     $(document).ready(function() {

         Highcharts.setOptions({
             global: {
                 useUTC: false
             }
         });
     
         var chart;
         $('#container').highcharts({
             chart: {
                 type: 'spline',
                 animation: Highcharts.svg, // don't animate in old IE
                 marginRight: 10,
                 events: {
                     load: function() {

                         // set up the updating of the chart each second
                         var series = this.series[0];
                         setInterval(function() {
                            $.get( "../rsc/onlineUsers", function( userNum ) {
                                 var x = (new Date()).getTime(), // current time
                                     y = parseInt(userNum);
                                 series.addPoint([x, y], true, true);
                             });
                         }, 1000);
                     }
                 }
             },
             title: {
                 text: 'Current Online User'
             },
             xAxis: {
                 type: 'datetime',
                 tickPixelInterval: 150
             },
             yAxis: {
                 allowDecimals:false,
                 title: {
                     text: 'Number'
                 },
                 plotLines: [{
                     value: 0,
                     width: 1,
                     color: '#808080'
                 }]
             },
             tooltip: {
                 formatter: function() {
                         return '<b>'+ this.series.name +'</b><br/>'+
                         Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) +'<br/>'+
                         Highcharts.numberFormat(this.y, 2);
                 }
             },
             legend: {
                 enabled: false
             },
             exporting: {
                 enabled: false
             },
             series: [{
                 name: 'Realtime data',
                 data: (function() {
                     // generate an array of random data
                     var data = [],
                         time = (new Date()).getTime(),
                         i;
     
                     for (i = -19; i <= 0; i++) {
                         data.push({
                             x: time + i * 1000,
                             y: 0
                         });
                     }
                     return data;
                 })()
             }]
         });
         
          $("#stopButtonT").hide();
          $('#recentTransaction').load('/../../api/data/web/?type=transaction');
          
          function dataRefreshstart_t(){
              refreshcon = setInterval(function(){
                  $('#recentTransaction').load('/../../api/data/web/?type=transaction', function(){
                   $("#startButtonT").hide();
                   $("#stopButtonT").show();
                  });
                }, 1000);
          }

          function dataRefreshstop_t(){
              try{
                  clearInterval(refreshcon);
                  $("#startButtonT").show();
                  $("#stopButtonT").hide();
              }catch(err){}
          }

          $("#startButtonT").click(function(){
            dataRefreshstart_t();
          });

          $("#stopButtonT").click(function(){
            dataRefreshstop_t();
          });

     });     
});