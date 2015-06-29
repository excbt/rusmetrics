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
    $scope.TEXT_CAPTION_LENGTH = 20*4-5; //length of message visible part. Koef 4 for class 'col-md-4', for class 'col-md-3' koef = 3 and etc.
    $scope.TYPE_CAPTION_LENGTH = 20*3-5; //length of type visible part 
    $scope.objectsUrl= "../api/subscr/contObjects";
    $scope.crudTableName= "../api/subscr/contEvent/notifications";
    $scope.noticeTypesUrl= "../api/contEvent/types";
    
    $scope.criticalTypes_flag = false;
    $scope.noCriticalTypes_flag = false;
    $scope.undefinedCriticalTypes_flag = false;
    
    $scope.allSelected = false;
    
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
    
    var getNotices = function(table, startDate, endDate, objectArray, eventTypeArray, isNew){
        if (isNew==null){
            return $resource(table, {},
                         {'get':{method:'GET', params:{fromDate: startDate, toDate: endDate, contEventTypeIds: eventTypeArray, contObjectIds: objectArray}}
            });
        }else{
            return $resource(table, {},
                         {'get':{method:'GET', params:{fromDate: startDate, toDate: endDate, contEventTypeIds: eventTypeArray, contObjectIds: objectArray, isNew: isNew}}
            });
        };
    }; 
    $scope.pagination = {
        current: 1
    };

    $scope.pageChanged = function(newPage) {       
//console.log("pageChanged");        
        $scope.getResultsPage(newPage);
    };
    
    var dataParse = function(arr){
        var oneNotice = {};
        var tmp = arr.map(function(el){
            oneNotice = {};
            oneNotice.noticeType = el.contEvent.contEventType.name;
            oneNotice.noticeMessage = el.contEvent.message;                        
            if (el.contEvent.contEventType.name.length > $scope.TYPE_CAPTION_LENGTH){
                    oneNotice.noticeTypeCaption= el.contEvent.contEventType.name.substr(0, $scope.TYPE_CAPTION_LENGTH)+"...";
                }else{
                     oneNotice.noticeTypeCaption= el.contEvent.contEventType.name;
            };
            if (el.contEvent.message == null){
                oneNotice.noticeCaption = "";
            }else{
                if (el.contEvent.message.length > $scope.TEXT_CAPTION_LENGTH){
                        oneNotice.noticeCaption= el.contEvent.message.substr(0, $scope.TEXT_CAPTION_LENGTH)+"...";
                    }else{
                         oneNotice.noticeCaption= el.contEvent.message;
                };
            };

            for (var i=0; i<$scope.objects.length; i++){                       
                if ($scope.objects[i].id == el.contObjectId ){
                    oneNotice.noticeObjectName = $scope.objects[i].fullName;  
                };   
            };

            oneNotice.noticeDate = $scope.dateFormat(el.contEvent.eventTime);
            oneNotice.contEventLevelColor = el.contEventLevelColor;
            oneNotice.isNew = el.isNew;
            switch (el.contEvent.contServiceType)
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
//old version        var url =  $scope.crudTableName+"/eventsFilterPaged"+"?"+"page="+(pageNumber-1)+"&"+"size="+$scope.noticesPerPage;        
        var url =  $scope.crudTableName+"/paged"+"?"+"page="+(pageNumber-1)+"&"+"size="+$scope.noticesPerPage;  
//console.log($rootScope.reportStart);        
        $scope.startDate = $rootScope.reportStart || moment().format('YYYY-MM-DD');
        $scope.endDate = $rootScope.reportEnd || moment().format('YYYY-MM-DD');  
//console.log($scope.startDate);                
        getNotices(url, $scope.startDate, $scope.endDate, $scope.selectedObjects, $scope.selectedNoticeTypes, $scope.isNew).get(function(data){                  
                        var result = [];
                        $scope.data= data;
//console.log($scope.data);             
                        var oneNotice = {};
                        $scope.totalNotices = data.totalElements;
                        var tmp = dataParse(data.objects);
                        $scope.notices = tmp;
//console.log($scope.notices);            
                    });
    };
    
    $scope.dateFormat = function(date){
        return (new Date(date)).toLocaleString();
    };

    $scope.selectObjectsClick = function(){
        $('#selectObjectsModal').modal('show');
    };
    
    $scope.selectNoticeTypesClick = function(){
        $('#selectNoticeTypesModal').modal('show');
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
    
    $scope.selectNoticeTypes = function(){
        $scope.selectedNoticeTypes_list = "";
        $scope.selectedNoticeTypes = [];
        $('#selectNoticeTypesModal').modal('hide');
        $scope.noticeTypes.map(function(el){
          if(el.selected){
              $scope.selectedNoticeTypes_list+=el.fullName+"; ";
              $scope.selectedNoticeTypes.push(el.id);
          }
        });
        $scope.selectedNoticeTypes_list = $scope.selectedNoticeTypes.length;
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
        crudGridDataFactory($scope.objectsUrl).query(function(data){
            $scope.objects = data;
//console.log("getObjects");            
              $scope.getResultsPage(1);
        });
    };
    
    //click the main checkbox - select/ deselect notices
    $scope.performAllNoticesOnPage = function(){
        $scope.notices.forEach(function(element){
            element.selected = $scope.allSelected;
        });
    };
    
    $scope.$watch('reportStart', function (newDates) {
console.log("watch");        
        $scope.getObjects();                              
    }, false);
    
    $scope.getNoticeTypes = function(url){
       $http.get(url)
            .success(function(data){
                $scope.noticeTypes = data;
//console.log($scope.noticeTypes);           
            })
            .error(function(e){
                console.log(e);
            });
    };
    
    $scope.getNoticeTypes($scope.noticeTypesUrl);
    
    //new/all filter
    $scope.getAllNotices = function(){
        $scope.isNew = null;
        $scope.getResultsPage(1);
    };
    
    $scope.getOnlyNewNotices = function(){
        $scope.isNew = true;
        $scope.getResultsPage(1);
    };
    
    
    //notice type filters
    $scope.noticeFilterCritical = function(notice){
        if (notice.isCriticalEvent===true){
            return notice
        };
    };
    
    $scope.noticeFilterNoCritical = function(notice){
        if (notice.isCriticalEvent===false){
            return notice
        };
    };
    
    $scope.noticeFilterUndefinedCritical = function(notice){
        if (!(notice.hasOwnProperty('isCriticalEvent'))||(notice.isCriticalEvent==null)){
            return notice
        };
    };
    
    //selection notice types
    $scope.selectAllCritical = function(){       
        if ($scope.criticalTypes_flag){               
            $scope.noticeTypes.forEach(function(notice){        
                if (notice.isCriticalEvent===true){
                    notice.selected = true;
                };
            });
        }else{                               
            $scope.noticeTypes.forEach(function(notice){        
                if (notice.isCriticalEvent===true){
                    notice.selected = false;
                };
            });
        };      
    };
    
    $scope.selectAllNoCritical = function(){       
        if ($scope.noCriticalTypes_flag){               
            $scope.noticeTypes.forEach(function(notice){        
                if (notice.isCriticalEvent===false){
                    notice.selected = true;
                };
            });
        }else{                               
            $scope.noticeTypes.forEach(function(notice){        
                if (notice.isCriticalEvent===false){
                    notice.selected = false;
                };
            });
        };      
    };
    
    $scope.selectAllUndefinedCritical = function(){       
        if ($scope.undefinedCriticalTypes_flag){               
            $scope.noticeTypes.forEach(function(notice){        
                if ((!notice.hasOwnProperty('isCriticalEvent'))||(notice.isCriticalEvent==null)){
                    notice.selected = true;
                };
            });
        }else{                               
            $scope.noticeTypes.forEach(function(notice){        
                if ((!notice.hasOwnProperty('isCriticalEvent'))||(notice.isCriticalEvent==null)){
                    notice.selected = false;
                };
            });
        };      
    };
    
    //Clear all filters
    $scope.clearAllFilters = function(){
        $scope.selectedNoticeTypes_list='Нет';
        $scope.selectedNoticeTypes=[];
        $scope.selectedObjects_list='Нет';
        $scope.selectedObjects=[];
        $scope.isNew = null;
        $scope.getResultsPage(1);
    };
    
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