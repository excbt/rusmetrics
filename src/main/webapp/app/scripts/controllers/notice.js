'use strict'

var app = angular.module('portalNMC');

app.controller('NoticeCtrl', function($scope, $http, $resource, $rootScope, crudGridDataFactory){
//console.log("$('#div-main-area').width()=");    
//console.log($('#div-main-area').width()); 
//    
//console.log("$('td.col-md-4').width()=");    
//console.log(Math.round($('#div-main-area').width()*8.333/100*4));  
//    
//console.log("$('td.col-md-3').width()=");    
//console.log(Math.round($('#div-main-area').width()*8.333/100*3));     
//    Math.round($('#div-main-area').width()*8.333/100)= 130 = 20*x; => x= 130/20
    //Math.round($('#div-main-area').width()*8.333/100/6.5) //dynamic coef
    $scope.TEXT_CAPTION_LENGTH = 20*4-3; //length of message visible part. Koef 4 for class 'col-md-4', for class 'col-md-3' koef = 3 and etc.
    $scope.TYPE_CAPTION_LENGTH = 20*3-3; //length of type visible part 
    $scope.crudTableName= "../api/subscr/contObjects";
    
    $scope.tableDef = {
        tableClass : "crud-grid table table-lighter table-condensed table-hover table-striped",
        hideHeader : false,
        headerClassTR : "nmc-main-table-header",
        columns : [ 

        {
            fieldName : "noticeDate",
            header : "Дата",
            headerClass : "col-md-1",
            dataClass : "col-md-1"
        },{
            fieldName : "noticeType",
            header : "Тип",
            headerClass : "col-md-1 nmc-hide",
            dataClass : "col-md-1 nmc-hide"
        }, {
            fieldName : "noticeMessage",
            header : "Уведомление",
            headerClass : "col-md-1 nmc-hide",
            dataClass : "col-md-1 nmc-hide"
        },
        {
            fieldName : "noticeTypeCaption",
            header : "Тип",
            headerClass : "col-md-3",
            dataClass : "col-md-3"
        }, {
            fieldName : "noticeCaption",
            header : "Уведомление",
            headerClass : "col-md-4",
            dataClass : "col-md-4"
        },  {
            fieldName : "noticeObjectName",
            header : "Объект",
            headerClass : "col-md-2",
            dataClass : "col-md-2"
        } , {
            fieldName : "noticeZpoint",
            header : "Точка учета",
            headerClass : "col-md-1",
            dataClass : "col-md-1"
        } ]
    };
    
    $scope.data = {};
    
    $scope.currentObject = {};
    $scope.selectedObjects = [];
    
    $scope.notices = [];
    $scope.totalNotices = 0;
    $scope.noticesPerPage = 25; // this should match however many results your API puts on one page
    
    var getNotices = function(table, startDate, endDate, objectArray){
        return $resource(table, {},
                         {'get':{method:'GET', params:{startDate: startDate, endDate: endDate, contObjectIds: objectArray}}
        });
    }; 
    $scope.pagination = {
        current: 1
    };

    $scope.pageChanged = function(newPage) {       
        $scope.getResultsPage(newPage);
    };
    
    var dataParse = function(arr){
        var oneNotice = {};
        var tmp = arr.map(function(el){
            oneNotice = {};
            oneNotice.noticeType = el.contEventType.name;
            oneNotice.noticeMessage = el.message;                        
            if (el.contEventType.name.length > $scope.TYPE_CAPTION_LENGTH){
                    oneNotice.noticeTypeCaption= el.contEventType.name.substr(0, $scope.TYPE_CAPTION_LENGTH)+"...";
                }else{
                     oneNotice.noticeTypeCaption= el.contEventType.name;
            };
            if (el.message == null){
                oneNotice.noticeCaption = "";
            }else{
                if (el.message.length > $scope.TEXT_CAPTION_LENGTH){
                        oneNotice.noticeCaption= el.message.substr(0, $scope.TEXT_CAPTION_LENGTH)+"...";
                    }else{
                         oneNotice.noticeCaption= el.message;
                };
            };

            for (var i=0; i<$scope.objects.length; i++){                       
                if ($scope.objects[i].id == el.contObjectId ){
                    oneNotice.noticeObjectName = $scope.objects[i].fullName;  
                };   
            };

            oneNotice.noticeDate = $scope.dateFormat(el.eventTime);

            switch (el.contServiceType)
            {
                        case "heat" : oneNotice.noticeZpoint = "ТС"; break;
                        case "hw" : oneNotice.noticeZpoint = "ГВС"; break;
                        case "cw" : oneNotice.noticeZpoint = "ХВ"; break;
                        case null : oneNotice.noticeZpoint = ""; break;
                        default: oneNotice.noticeZpoint  = ""+el.contServiceType+"";
             }
            return oneNotice;
        });
        return tmp;
    };
    
//    $(window).resize(function() {
//        $scope.TEXT_CAPTION_LENGTH = Math.round($('#div-main-area').width()*8.333/100/6.5)*4-3; //length of message visible part. Koef 4 for class 'col-md-4', for class 'col-md-3' koef = 3 and etc.
//        $scope.TYPE_CAPTION_LENGTH = Math.round($('#div-main-area').width()*8.333/100/6.5)*3-3; //length of type visible part 
//console.log("Re parse");
//console.log($scope.TEXT_CAPTION_LENGTH);  
//console.log($scope.TYPE_CAPTION_LENGTH);         
//        var tmp =dataParse($scope.data.objects);
//        $scope.notices = tmp;
//       
//    });
    
    $scope.getResultsPage = function(pageNumber) {
        $scope.pagination.current = pageNumber;        
        var url =  $scope.crudTableName+"/eventsFilterPaged"+"?"+"page="+(pageNumber-1)+"&"+"size="+$scope.noticesPerPage;        
        $scope.startDate = $rootScope.reportStart || moment().format('YYYY-MM-DD');
        $scope.endDate = $rootScope.reportEnd || moment().format('YYYY-MM-DD');   
        getNotices(url, $scope.startDate, $scope.endDate, $scope.selectedObjects).get(function(data){                  
                        var result = [];
                        $scope.data= data;
                        var oneNotice = {};
                        $scope.totalNotices = data.totalElements;
                        var tmp = dataParse(data.objects);
//                        var tmp = data.objects.map(function(el){
//                            oneNotice = {};
//                            oneNotice.noticeType = el.contEventType.name;
//                            oneNotice.noticeMessage = el.message;                        
//                            if (el.contEventType.name.length > $scope.TYPE_CAPTION_LENGTH){
//                                    oneNotice.noticeTypeCaption= el.contEventType.name.substr(0, $scope.TYPE_CAPTION_LENGTH)+"...";
//                                }else{
//                                     oneNotice.noticeTypeCaption= el.contEventType.name;
//                            };
//                            
//                            if (el.message.length > $scope.TEXT_CAPTION_LENGTH){
//                                    oneNotice.noticeCaption= el.message.substr(0, $scope.TEXT_CAPTION_LENGTH)+"...";
//                                }else{
//                                     oneNotice.noticeCaption= el.message;
//                            };
//                            
//                            for (var i=0; i<$scope.objects.length; i++){                       
//                                if ($scope.objects[i].id == el.contObjectId ){
//                                    oneNotice.noticeObjectName = $scope.objects[i].fullName;  
//                                };   
//                            };
//                            
//                            oneNotice.noticeDate = $scope.dateFormat(el.eventTime);
//                            
//                            switch (el.contServiceType)
//                            {
//                                        case "heat" : oneNotice.noticeZpoint = "ТС"; break;
//                                        case "hw" : oneNotice.noticeZpoint = "ГВС"; break;
//                                        case "cw" : oneNotice.noticeZpoint = "ХВ"; break;
//                                        default: oneNotice.noticeZpoint  = data;
//                             }
//                            return oneNotice;
//                        });
                        $scope.notices = tmp;
                    });
    };
    
    $scope.dateFormat = function(date){
        return (new Date(date)).toLocaleString();
    };

      $scope.selectObjectsClick = function(){
          $('#selectObjectsModal').modal('show');
      };
      
      $scope.selectObjects = function(){
          $scope.selectedObjects_list = "";
          $scope.selectedObjects = [];
          $('#selectObjectsModal').modal('hide');
          $scope.objects.map(function(el){
              if(el.selected){
                  $scope.selectedObjects_list+=el.fullName+"; ";
                  $scope.selectedObjects.push(el.id);
              }
          });
          $scope.selectedObjects_list = $scope.selectedObjects.length;
          $scope.getResultsPage(1);
         
      };
      
      $scope.selectedItem = function (item) {
          var curObject = angular.copy(item);
          $scope.currentObject = curObject;
  
      };
    
    $scope.showNoticeDetail = function(object){
        $scope.selectedItem(object);
        $('#showNoticeModal').modal();
    };
    
        
    
             //get Objects
    $scope.getObjects = function(){
        crudGridDataFactory($scope.crudTableName).query(function(data){
            $scope.objects = data;
              $scope.getResultsPage(1);
        });
    };
    
    $scope.$watch('reportStart', function (newDates) {
        $scope.getObjects();                              
    }, false);
    
    
    //chart
    $scope.runChart = function(){
        var data = [];//, series = Math.floor(Math.random() * 6) + 3;

//		for (var i = 0; i < series; i++) {
//			data[i] = {
//				label: "Series" + (i + 1),
//				data: Math.floor(Math.random() * 100) + 1
//			}
//		};
        var noticeTypes = [];
        var noticeCountEveryType = [];
//        get notice types.
        for (var i = 0; i< $scope.notices.length; i++){
            if (noticeTypes.length>0){
                var flag = false;
                for (var j = 0; j<noticeTypes.length; j++){
//console.log("noticeType = "+noticeTypes[j]);
//console.log("$scope.notices =");
//console.log($scope.notices[i]);                    
                    if(noticeTypes[j]===$scope.notices[i].noticeType){
                        noticeCountEveryType[j] +=1;
                        flag =true;
                        continue;
                    };
                };
                if (!flag){
                    noticeTypes.push($scope.notices[i].noticeType);
                    noticeCountEveryType.push(1);
                };
            }else{
                noticeTypes.push($scope.notices[i].noticeType);
                noticeCountEveryType.push(1);
            };
        };
        for (var i = 0; i < noticeTypes.length; i++) {
			data[i] = {
				label: noticeTypes[i],
				data: noticeCountEveryType[i]
			}
		};
        
        // выводим график
        $("#noticeChart-area").width(300);
        $("#noticeChart-area").height(300);
        $("#chartModal.modal-dialog").width(700);
        $('#chartModal').modal();
        
        $.plot('#noticeChart-area', data,{
            series: {
                pie: {
                    show: true,
                    label :{
                        show : false,
                        formatter: labelFormatter
                    }
                }
            },
            legend: {
                show: true,
                labelFormatter: labelFormatter,
                position: "ne",
                margin: [-400, 0]
            }
        });
        
    };
    
    function labelFormatter(label, series) {
		return "<div style='font-size:8pt; text-align:center; padding:2px; color:black;'>" + label + " (" + Math.round(series.percent) + "%)</div>";
	}
});