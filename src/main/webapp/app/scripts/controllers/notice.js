'use strict';

/**
 * @ngdoc function
 * @name portalNMK.controller:NotifiCtrl
 * @description
 * # MainCtrl
 * Controller of the portalNMK
 */
angular.module('portalNMK')
  .controller('NoticeCtrl',['$scope', 'crudGridDataFactory', function ($scope, crudGridDataFactory) {

    $scope.crudTableName= "../api/subscr/contObjects";
    
//    $scope.cols = [ 'Тип', 'Сообщение', 'Дата', 'Объект', 'Точка учета'];
      
    //Определяем оформление для таблицы уведомлений
    $scope.tableDef = {
					tableClass : "crud-grid table table-lighter table-bordered table-condensed table-hover",
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


                var tmp = data.map(function(el) {
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
                            default: result.noticeZpoint  = el.contServiceType;
                    }
                    return result;
                });

                $scope.data = tmp;

            });
        };

        $scope.getData();

  }]);
