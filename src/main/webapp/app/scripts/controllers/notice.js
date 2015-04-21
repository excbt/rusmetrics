'use strict';

/**
 * @ngdoc function
 * @name portalNMK.controller:NotifiCtrl
 * @description
 * # MainCtrl
 * Controller of the portalNMK
 */
angular.module('portalNMK')
  .controller('NoticeCtrl',['$scope', 'crudGridDataFactory', function ($scope, crudGridDataFactory, DTOptionsBuilder, DTColumnBuilder) {

    $scope.crudTableName= "../api/subscr/contObjects";
    
//    $scope.cols = [ 'Тип', 'Сообщение', 'Дата', 'Объект', 'Точка учета'];
      
    //Определяем оформление для таблицы уведомлений
    $scope.tableDef = {
					tableClass : "crud-grid table table-lighter table-condensed table-hover table-striped",
					hideHeader : false,
					headerClassTR : "info",
					columns : [ 
                        {
						fieldName : "noticeCat",
						header : "Категория",
						headerClass : "col-md-1",
						dataClass : "col-md-1",
						autoincrement : "false"
					}, {
						fieldName : "noticeType",
						header : "Тип",
						headerClass : "col-md-2",
						dataClass : "col-md-2"
					}, {
						fieldName : "noticeText",
						header : "Уведомление",
						headerClass : "col-md-3",
						dataClass : "col-md-3"
					}, {
						fieldName : "noticeDate",
						header : "Дата",
						headerClass : "col-md-2",
						dataClass : "col-md-2"
					}, {
						fieldName : "noticeObject",
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
        $scope.getData = function () {
            var table =  $scope.crudTableName+"/events";    
            crudGridDataFactory(table).query(function (data) {

                var tempArr = [];
                var noticesByAbonent = data;

                console.log("Table = "+table);
//        Заглушка. Будем брать из полученного массива  уведомлений первую попавшуюся тысячу и выводить ее       
                var ROW_COUNT_TO_PAGE = 1000;
                var tmpRowCount = 0;
                var tmp1 = data.slice(0,999);
                
// ........................................................................                
                var tmp = tmp1.map(function(el) {
                    var result = {};
                    result.noticeCheckbox = " ";
                    result.noticeType = el.contEventType.name;
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
              
                    return result;
                    
                    
                });

                $scope.data = tmp;

            });
        };

      //get Events
 //       $scope.getData();
      
      //get Objects
        $scope.objects = [];
        $scope.getObjects = function(){
            crudGridDataFactory($scope.crudTableName).query(function(data){
                $scope.objects = data;
console.log("data = "+data);                
            });
        };
        $scope.getObjects();
      
      
      $scope.selectObjectsClick = function(){
          $('#selectObjectsModal').modal('show');
      };
      
      $scope.selectObjects = function(){
          $scope.selectedObjects_list = "";
          $('#selectObjectsModal').modal('hide');
          $scope.objects.map(function(el){
              if(el.selected){
                  $scope.selectedObjects_list+=el.fullName+"; ";
                  
              }
          });
console.log($scope.selectedObjects_list);          
      };
      
      
      
      
      
      
      
      
      
      
      //use angular-datatable
//      $scope.dtOptions = DTOptionsBuilder.fromFnPromise(function(){
//          var table =  $scope.crudTableName+"/events";
//          return crudGridDataFactory(table).query().$promise;
//      }).withPaginationType('full_numbers');
//      $scope.dtColumns = [
//        DTColumnBuilder.newColumn('noticeCat').withTitle('Категория').notVisible(),
//        DTColumnBuilder.newColumn('noticeType').withTitle('Тип'),
//        DTColumnBuilder.newColumn('noticeText').withTitle('Уведомление')
//        ,DTColumnBuilder.newColumn('noticeDate').withTitle('Дата')  
//      ];
      

  }]);
