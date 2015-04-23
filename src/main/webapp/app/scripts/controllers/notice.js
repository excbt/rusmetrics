'use strict';

/**
 * @ngdoc function
 * @name portalNMK.controller:NotifiCtrl
 * @description
 * # MainCtrl
 * Controller of the portalNMK
 */
angular.module('portalNMK')
  .controller('NoticeCtrl',['$scope', '$rootScope', '$resource', 'crudGridDataFactory', 'DTOptionsBuilder', 'DTColumnBuilder', 'DTInstances', function ($scope, $rootScope, $resource, crudGridDataFactory, DTOptionsBuilder, DTColumnBuilder, DTInstances) {

    $scope.TEXT_CAPTION_LENGTH = 20*4; //length of message visible part. Koef 4 for class 'col-md-4', for class 'col-md-3' koef = 3 and etc.
    $scope.TYPE_CAPTION_LENGTH = 20*3; //length of type visible part  
    $scope.crudTableName= "../api/subscr/contObjects";
      
    $scope.currentObject = {};  
    $scope.data = {};  
    $scope.objects = [];
    $scope.selectedObjects = [];  
    
//    $scope.cols = [ 'Тип', 'Сообщение', 'Дата', 'Объект', 'Точка учета'];
      
    //Определяем оформление для таблицы уведомлений
    $scope.tableDef = {
					tableClass : "crud-grid table table-lighter table-condensed table-hover table-striped",
					hideHeader : false,
					headerClassTR : "info",
					columns : [ 
//                        {
//						fieldName : "noticeCat",
//						header : "Категория",
//						headerClass : "col-md-1",
//						dataClass : "col-md-1",
//						autoincrement : "false"
//					}, 
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
 
        var getNotices = function(table, startDate, endDate, objectArray){
            return $resource(table, {},
                             {'get':{method:'GET', params:{startDate: startDate, endDate: endDate, contObjectIds: objectArray}}
            });
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
         
      };
      
      $scope.selectedItem = function (item) {
          var curObject = angular.copy(item);
          $scope.currentObject = curObject;
  
      };

      
      //use angular-datatable for notices

      //define options
        $scope.dtOptions = DTOptionsBuilder
            .fromFnPromise(newPromise)
            .withDataProp('data.objects')
            .withPaginationType('full_numbers')
            .withOption('rowCallback', rowCallback)
            .withLanguageSource('bower_components/datatables/plugins/i18n/Russian.json')
            .withDOM('pitrfl')
        ;

        //define columns
        $scope.dtColumns = [
            DTColumnBuilder.newColumn('id').notVisible()
            ,DTColumnBuilder.newColumn('contEventType.name').withTitle('Тип').withClass($scope.tableDef.columns[0].headerClass)
            .renderWith(function(data, type, full){
                if (full.contEventType.name.length > $scope.TYPE_CAPTION_LENGTH){
                        return full.contEventType.name.substr(0, $scope.TYPE_CAPTION_LENGTH)+"...";
                    }else{
                        return full.contEventType.name;
                };
            })
            ,DTColumnBuilder.newColumn('message').withTitle('Уведомление').withClass($scope.tableDef.columns[1].headerClass)
            .renderWith(function(data, type, full){
                if (full.message.length > $scope.TEXT_CAPTION_LENGTH){
                        return full.message.substr(0, $scope.TEXT_CAPTION_LENGTH)+"...";
                    }else{
                        return full.message;
                };
            })
            ,DTColumnBuilder.newColumn('eventTime').withTitle('Дата').withClass($scope.tableDef.columns[2].headerClass)
            .renderWith(function(data, type, full){
                return moment(full.eventTime).format('YYYY-MM-DD HH:mm:ss')
            })
            ,DTColumnBuilder.newColumn('contObjectId').withTitle('Объект').withClass($scope.tableDef.columns[3].headerClass)
            .renderWith(function(data, type, full){                
                if (($scope.objects == []) || ($scope.objects.length==0)||(typeof $scope.objects=='undefined')){
                    return data;
                }
                for (var i=0; i<$scope.objects.length; i++){                       
                        if ($scope.objects[i].id == data ){
                            return $scope.objects[i].fullName;  
                        };   
                    }                
            })
            ,DTColumnBuilder.newColumn('contServiceType').withTitle('Точка учета').withClass($scope.tableDef.columns[4].headerClass)
            .renderWith(function(data, type, full){
                var result = "";
                 switch (data)
                    {
                            case "heat" : result = "ТС"; break;
                            case "hw" : result = "ГВС"; break;
                            case "cw" : result = "ХВ"; break;
                            default: result  = data;
                    }
                return result;
            })
            
          ];
      
      //function get notices
          $scope.newPromise = newPromise;
      
        DTInstances.getLast().then(function (dtInstance) {
//console.log(dtInstance);              
                $scope.dtInstance = dtInstance;
          });
      
          function newPromise(){
//console.log("Run new promise");              
              $scope.startDate = $rootScope.reportStart || moment().format('YYYY-MM-DD');
              $scope.endDate = $rootScope.reportEnd || moment().format('YYYY-MM-DD');
              var table =  $scope.crudTableName+"/eventsFilter";
              return getNotices(table, $scope.startDate, $scope.endDate, $scope.selectedObjects).get(
                            function(data){
                                $scope.data = data.objects;
                                $scope.totalElements = data.totalElements;
                                $scope.totalPages = data.totalPages;})
                    .$promise;
          };
      
          
      //function get notice details and show notice card to user
        $scope.getNotice = function(info){
            var tmp = $scope.data;
            if ((tmp==[])||(typeof tmp == 'undefined')){
                return;
            };
            for(var i=0; i<tmp.length; i++){
                if (info.id == tmp[i].id){
                    $scope.currentObject = {};
                    $scope.currentObject.noticeType = tmp[i].contEventType.name;
                    $scope.currentObject.noticeText = tmp[i].message;
                    if (($scope.objects == []) || ($scope.objects.length==0)||(typeof $scope.objects=='undefined')){
                         $scope.currentObject.noticeObjectName = info.contObjectId;
                    }else{
                        for (var i=0; i<$scope.objects.length; i++){                   
                                if ($scope.objects[i].id == info.contObjectId ){
                                    $scope.currentObject.noticeObjectName = $scope.objects[i].fullName;
                                };   
                            }  
                    }
                    
                    $scope.currentObject.noticeDate = info.eventTime;
                    $scope.currentObject.noticeZpoint = info.contServiceType;
                    $('#showNoticeModal').modal();
                    break;
                };
            };
        };
        
        function rowCallback(nRow, aData, iDisplayIndex, iDisplayIndexFull){
            $('td', nRow).unbind('click');
            $('td', nRow).bind('click', function() {
                $scope.$apply(function() {
                    $scope.getNotice(aData);
                });
            });
            return nRow;
        };
      
      
         //get Objects
        
        $scope.getObjects = function(){
            crudGridDataFactory($scope.crudTableName).query(function(data){
                $scope.objects = data;
                
                //get Events
   
                if (($scope.dtInstance != {}) && (typeof $scope.dtInstance != 'undefined')){                   
                    $scope.dtInstance.changeData($scope.newPromise)
                };
              
            });
        };
        $scope.getObjects();     

  }]);



//set DataTable loading param
angular.module('portalNMK').
factory('DTLoadingTemplate', dtLoadingTemplate);
function dtLoadingTemplate() {
    return {
        html: '<h3>Загрузка...</h3>'
    };
}

