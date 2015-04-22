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

    $scope.TEXT_CAPTION_LENGTH = 20*4; //length of message visible part. Koef 4 for class 'col-md-4', for class 'col-md-3' koef = 3
    $scope.TYPE_CAPTION_LENGTH = 20*3; //length of type visible part  
    $scope.crudTableName= "../api/subscr/contObjects";
      
    $scope.currentObject = {};  
    
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

      //Получаем уведомления
        $scope.data = {};
      
        
        $scope.selectedObjects = [];
        var getNotices = function(table, startDate, endDate, objectArray){
            return $resource(table, {},
                             {'get':{method:'GET', params:{startDate: startDate, endDate: endDate, contObjectIds: objectArray}}
            });
        };
      
        $scope.getData = function () {
$scope.startGetData = new Date();            
        //Устанавливаем временной интервал для получения уведомлений
            $scope.startDate = $rootScope.reportStart || moment().format('YYYY-MM-DD');
            $scope.endDate = $rootScope.reportEnd || moment().format('YYYY-MM-DD');
            
            var table =  $scope.crudTableName+"/eventsFilter";  
           // table = "events.json";
            getNotices(table, $scope.startDate, $scope.endDate, $scope.selectedObjects).get(function (data) {

//                var tempArr = [];
//                var noticesByAbonent = data.objects;

//                console.log("Table = "+table);
//        Заглушка. Будем брать из полученного массива  уведомлений первую попавшуюся тысячу и выводить ее       
//                var ROW_COUNT_TO_PAGE = 1000;
//                var tmpRowCount = 0;
//                var tmp1 = data.slice(0,999);
                
// ........................................................................                
                $scope.totalElements = data.totalElements;
                $scope.totalPages = data.totalPages;
                $scope.startModificateData = new Date();
                var tmp = data.objects.map(function(el) {
                    var result = {};
                    result.noticeCheckbox = " ";
                    result.noticeType = el.contEventType.name;
                    
                    if (el.message.length > $scope.TEXT_CAPTION_LENGTH){
                        result.noticeCaption = el.message.substr(0, $scope.TEXT_CAPTION_LENGTH)+"...";
                    }else{
                        result.noticeCaption = el.message;
                    }
                    if (el.contEventType.name.length > $scope.TYPE_CAPTION_LENGTH){
                        result.noticeTypeCaption = el.contEventType.name.substr(0, $scope.TYPE_CAPTION_LENGTH)+"...";
                    }else{
                        result.noticeTypeCaption = el.contEventType.name;
                    };
                    
                    result.noticeText = el.message;
                    result.noticeDate = (new Date(el.eventTime)).toLocaleString();                      
                    result.noticeObject = el.contObjectId;
                //Преобразование типа точки учета в значение, которое сможет прочитать пользователь
                    switch (el.contServiceType)
                    {
                            case "heat" : result.noticeZpoint = "ТС"; break;
                            case "hw" : result.noticeZpoint = "ГВС"; break;
                            case "cw" : result.noticeZpoint = "ХВ"; break;
                            default: result.noticeZpoint  = el.contServiceType;
                    }
                    result.noticeObjectName = "Не определено";
                    for (var i=0; i<$scope.objects.length; i++){
//console.log("$scope.objects[i].id = "+$scope.objects[i].id);
//console.log("el.contObjectId = "+el.contObjectId);                        
                        if ($scope.objects[i].id == el.contObjectId ){
//console.log("Bingo");                            
                            result.noticeObjectName = $scope.objects[i].fullName;
//console.log("result.noticeObjectName ="+result.noticeObjectName);                            
                            break;
                        };
                        
                    }
                    return result;
                    
                    
                });
                
                $scope.endModificateData = new Date();
                $scope.data = tmp;
                


            });
        };
      
//      $scope.setFilter = function(){
//          
//      };
      

      
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
console.log($scope.selectedObjects_list);          
      };
      
      $scope.selectedItem = function (item) {
          var curObject = angular.copy(item);
          $scope.currentObject = curObject;
          //Ищем объект и возвращаем его название
          // в уведомлениях приходят только id объекта, поэтому чтобы вывести пользователю 
          // название объекта, мы загружаем все объекты и по имеющемуся id ищем в массиве объектов нужный нам объект и выводим его название пользователю
//          var tmpEl = $scope.objects.find(function(element, index, array){
//              return element.id = $scope.currentObject.contObjectId;
//          });
//          $scope.currentObject.objectName = tempEl.fullName;  
      };
      
    //Set options for page  
    //var vm = this;

      
      
      
      
      
      
      
      
      //use angular-datatable
//      $scope.dtOptions = DTOptionsBuilder.fromFnPromise(function(){
//          var table =  $scope.crudTableName+"/events";
//          return crudGridDataFactory(table).query().$promise;
//      }).withPaginationType('full_numbers');

      
        $scope.dtOptions = DTOptionsBuilder
            .fromFnPromise(newPromise)
            .withDataProp('data.objects')
            .withPaginationType('full_numbers')
            .withLanguageSource('vendor_components/DataTables-1.10.6/plugins/i18n/Russian.json')
            .withDOM('pitrfl')
        ;

        $scope.dtColumns = [
            DTColumnBuilder.newColumn('id').notVisible()
            ,DTColumnBuilder.newColumn('contEventType.name').withTitle('Тип').withClass($scope.tableDef.columns[0].headerClass).renderWith(function(data, type, full){
                if (full.contEventType.name.length > $scope.TYPE_CAPTION_LENGTH){
                        return full.contEventType.name.substr(0, $scope.TYPE_CAPTION_LENGTH)+"...";
                    }else{
                        return full.contEventType.name;
                };
            })
            ,DTColumnBuilder.newColumn('message').withTitle('Уведомление').withClass($scope.tableDef.columns[1].headerClass).renderWith(function(data, type, full){
                if (full.message.length > $scope.TEXT_CAPTION_LENGTH){
                        return full.message.substr(0, $scope.TEXT_CAPTION_LENGTH)+"...";
                    }else{
                        return full.message;
                };
            })
            ,DTColumnBuilder.newColumn('eventTime').withTitle('Дата').withClass($scope.tableDef.columns[2].headerClass).renderWith(function(data, type, full){
                return moment(full.eventTime).format('YYYY-MM-DD HH:mm:ss')
            })
            ,DTColumnBuilder.newColumn('contObjectId').withTitle('Объект').withClass($scope.tableDef.columns[3].headerClass).renderWith(function(data, type, full){
//console.log(data);                
//console.log(type);                
//console.log(full);       
//console.log($scope.objects);                
                if (($scope.objects == []) || ($scope.objects.length==0)||(typeof $scope.objects=='undefined')){
                    return data;
                }
                for (var i=0; i<$scope.objects.length; i++){
//console.log("$scope.objects[i].id = "+$scope.objects[i].id);
//console.log("el.contObjectId = "+el.contObjectId);                        
                        if ($scope.objects[i].id == data ){
//console.log("Bingo");                            
                            return $scope.objects[i].fullName;
//console.log("result.noticeObjectName ="+result.noticeObjectName);                            
                           
                        };   
                    }                
            })
            ,DTColumnBuilder.newColumn('contServiceType').withTitle('Точка учета').withClass($scope.tableDef.columns[4].headerClass).renderWith(function(data, type, full){
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
      
          $scope.newPromise = newPromise;
          function newPromise(){
              $scope.startDate = $rootScope.reportStart || moment().format('YYYY-MM-DD');
              $scope.endDate = $rootScope.reportEnd || moment().format('YYYY-MM-DD');
              var table =  $scope.crudTableName+"/eventsFilter";
              return getNotices(table, $scope.startDate, $scope.endDate, $scope.selectedObjects).get().$promise;
          };
      
          DTInstances.getLast().then(function (dtInstance) {
                $scope.dtInstance = dtInstance;
          });
      
      
                //get Objects
        $scope.objects = [];
        $scope.getObjects = function(){
            crudGridDataFactory($scope.crudTableName).query(function(data){
                $scope.objects = data;
                
                //get Events
  //              $scope.getData();  
                if (($scope.dtInstance != {}) && (typeof $scope.dtInstance != 'undefined')){
//console.log($scope.dtInstance);                    
                    $scope.dtInstance.changeData($scope.newPromise)
                };
//console.log("data = "+data);                
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

///////////////////////////////test
//angular.module('portalNMK').controller('NoticeDatatableCtrl', NoticeDatatableCtrl);
//
//function NoticeDatatableCtrl(DTOptionsBuilder, DTColumnBuilder, $resource) {
//    var crudTableName= "../api/subscr/contObjects";
//    
//    var vm = this;
//    vm.dtOptions = DTOptionsBuilder.fromFnPromise(function() {
//        var table =  crudTableName+"/events";
//        table = "events.json";
//          return $resource(table).query().$promise;
//    }).withPaginationType('full_numbers')
//    .withLanguageSource('vendor_components/DataTables-1.10.6/plugins/i18n/Russian.json')
//    .withDOM('pitrfl')
//    ;
//
//    vm.dtColumns = [
//        DTColumnBuilder.newColumn('id').withTitle('id').notVisible(),
//        DTColumnBuilder.newColumn('contObjectId').withTitle('Тип'),
//        DTColumnBuilder.newColumn('message').withTitle('Уведомление')
//        ,DTColumnBuilder.newColumn('noticeDate').withTitle('Дата') 
//        DTColumnBuilder.newColumn('id').withTitle('id').notVisible(),
//        DTColumnBuilder.newColumn('fullName').withTitle('Название'),
//        DTColumnBuilder.newColumn('fullAddress').withTitle('Адрес')
        
//        DTColumnBuilder.newColumn('noticeCat').withTitle('Категория').notVisible(),
//        DTColumnBuilder.newColumn('contEventType.name').withTitle('Тип'),
//        DTColumnBuilder.newColumn('message').withTitle('Уведомление')
//        ,DTColumnBuilder.newColumn('eventTime').withTitle('Дата')
//    ];
//}
////////////////////////////////// end test
