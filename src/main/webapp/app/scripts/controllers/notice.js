'use strict';

/**
 * @ngdoc function
 * @name portalNMK.controller:NotificationCtrl
 * @description
 * # MainCtrl
 * Controller of the portalNMK
 */
angular.module('portalNMK')
  .controller('NoticeCtrl',['$scope', 'crudGridDataFactory', function ($scope, crudGridDataFactory) {

    $scope.crudTableName= "../api/subscr/contObjects";
    
    $scope.tableDef = {
					tableClass : "crud-grid table table-lighter table-bordered table-condensed table-hover",
					hideHeader : false,
					headerClassTR : "info",
					columns : [ {
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
						headerClass : "col-md-3",
						dataClass : "col-md-3"
					} , {
						fieldName : "noticeZpoint",
						header : "Точка учета",
						headerClass : "col-md-1",
						dataClass : "col-md-1"
					} ]
			};
    
    
            $scope.getData = function (cb) {
                    crudGridDataFactory($scope.crudTableName).query(function (data) {
                        $scope.objects = data;
                    
                        if (cb) cb();
                    });
                };

            $scope.getData(
                    function () {
                         $scope.loading = false;
                         $scope.getAllNotices();
                    });
    
    
                $scope.notices = [];            
                $scope.noticeIter = 0;
                
                $scope.getNoticesByObject = function(tableName, object){
                    
                        var table = tableName+"/"+object.id+"/events";
                        crudGridDataFactory(table).query(function (data) {
                           
                            var tempArr = [];
                            $scope.noticesByObject = data;
                            
                            console.log("Table = "+table);

                                                        for(var j=0;j<6;j++){                                                                            
                                                      
                                                            if (typeof $scope.noticesByObject[j] == 'undefined'){
                                                                continue;
                                                            };
                                                            var notice = {};
                                                            notice.noticeType = $scope.noticesByObject[j].contEventType.name;
                                                            notice.noticeText = $scope.noticesByObject[j].message;
                                                            notice.noticeDate = new Date($scope.noticesByObject[j].eventTime);
                                                            notice.noticeObject = object.fullName;
                                                            notice.noticeZpoint  = $scope.noticesByObject[j].contServiceType;   

                                                            tempArr[$scope.noticeIter] = notice;
                                                            $scope.noticeIter=$scope.noticeIter+1;
                                                        }
                         $scope.notices = tempArr;   
                        });
                    
                    
                };
                
                $scope.getAllNotices = function(){
                    $scope.oldObjects = $scope.objects;
                    for (var i=0;i<4; i++){
                        //
                        
                        $scope.getNoticesByObject($scope.crudTableName, $scope.oldObjects[i]);                
                        
                        $scope.objects1 = $scope.notices;
                        //
                        
                            
                    }
                    
                    
                }; //end getAllNotices
    
    
    
    $scope.data = [{operationId : 1,
				operationText : "Тест Тест Тест",
				operationUser : "User User User"}];
    
  }]);
