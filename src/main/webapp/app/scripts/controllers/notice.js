'use strict'

var app = angular.module('portalNMK');

app.controller('NoticeCtrl', function($scope, $http, $resource, $rootScope, crudGridDataFactory){
    $scope.TEXT_CAPTION_LENGTH = 20*4-3; //length of message visible part. Koef 4 for class 'col-md-4', for class 'col-md-3' koef = 3 and etc.
    $scope.TYPE_CAPTION_LENGTH = 20*3-3; //length of type visible part 
    $scope.crudTableName= "../api/subscr/contObjects";
    
    $scope.tableDef = {
        tableClass : "crud-grid table table-lighter table-condensed table-hover table-striped",
        hideHeader : false,
        headerClassTR : "info",
        columns : [ 

        {
            fieldName : "noticeType",
            header : "Тип",
            headerClass : "col-md-1 hideM",
            dataClass : "col-md-1 hideM"
        }, {
            fieldName : "noticeMessage",
            header : "Уведомление",
            headerClass : "col-md-1 hideM",
            dataClass : "col-md-1 hideM"
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
        }, {
            fieldName : "noticeDate",
            header : "Дата",
            headerClass : "col-md-2",
            dataClass : "col-md-2"
        }, {
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
    
    $scope.currentObject = {};
    $scope.selectedObjects = [];
    
    $scope.notices = [];
    $scope.totalNotices = 0;
    $scope.noticesPerPage = 10; // this should match however many results your API puts on one page
    
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
    
    $scope.getResultsPage = function(pageNumber) {
        $scope.pagination.current = pageNumber;        
        var url =  $scope.crudTableName+"/eventsFilterPaged"+"?"+"page="+(pageNumber-1)+"&"+"size="+$scope.noticesPerPage;        
        $scope.startDate = $rootScope.reportStart || moment().format('YYYY-MM-DD');
        $scope.endDate = $rootScope.reportEnd || moment().format('YYYY-MM-DD');   
        getNotices(url, $scope.startDate, $scope.endDate, $scope.selectedObjects).get(function(data){                  
                        var result = [];
                        var oneNotice = {};
                        $scope.totalNotices = data.totalElements;
                        var tmp = data.objects.map(function(el){
                            oneNotice = {};
                            oneNotice.noticeType = el.contEventType.name;
                            oneNotice.noticeMessage = el.message;
                            
                            if (el.contEventType.name.length > $scope.TYPE_CAPTION_LENGTH){
                                    oneNotice.noticeTypeCaption= el.contEventType.name.substr(0, $scope.TYPE_CAPTION_LENGTH)+"...";
                                }else{
                                     oneNotice.noticeTypeCaption= el.contEventType.name;
                            };
                            
                            if (el.message.length > $scope.TEXT_CAPTION_LENGTH){
                                    oneNotice.noticeCaption= el.message.substr(0, $scope.TEXT_CAPTION_LENGTH)+"...";
                                }else{
                                     oneNotice.noticeCaption= el.message;
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
                                        default: oneNotice.noticeZpoint  = data;
                             }
                            return oneNotice;
                        });
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
          $scope.getResultsPage(1);
         
      };
      
      $scope.selectedItem = function (item) {
          var curObject = angular.copy(item);
          $scope.currentObject = curObject;
  
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
});