angular.module('portalNMC')
.directive('nmcViewElectricity', function(){
    return {
        restrict: 'EA',
        scope: {
            dataUrl: "=url",
            columns: "=columns",
            type: "=type"
        },
        templateUrl: 'scripts/directives/templates/nmc-view-electricity.html',
        controller: function($scope, $element, $attrs, $http, notificationFactory){
            
            $scope.curDate = moment().endOf('day').format("YYYY-MM-DD");
            
            $scope.dirSettings = {};
            $scope.dirSettings.dateFormat = "DD.MM.YYYY"; //date format
            $scope.dateOptsParamsetRu ={
                locale : {
                    daysOfWeek : [ 'Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб' ],
                    firstDay : 1,
                    monthNames : [ 'Январь', 'Февраль', 'Март', 'Апрель',
                            'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь',
                            'Октябрь', 'Ноябрь', 'Декабрь' ]
                },
                singleDatePicker: true,
                format: $scope.dirSettings.dateFormat
            };
//            var url = $attrs.url;
//            $scope.columns = $attrs.columns;
//console.log($scope.dataUrl);            
//console.log($scope.columns);             
            var successCallback = function(response){
                var tmp = angular.copy(response.data);
                tmp.forEach(function(el){
                    var result  = {};
                    for(var i in $scope.columns){
                        if ($scope.columns[i].fieldName == "dataDate"){
            //console.log("Indicator id = "+el.id);                            
            //console.log("Indicator timestamp in millisec, which get from server = "+el.dataDate);
            //console.log("Indicator timestamp +3 hours in sec = "+(Math.round(el.dataDate/1000.0)+3*3600));                            
            //                          var datad = DateNMC(el.dataDate);
            //console.log(datad.getTimezoneOffset());
            //console.log(datad.toLocaleString());                            
                            //el.dataDate=el.dataDateString;//printDateNMC(datad);
                            continue;
                        };
                        if ((el[$scope.columns[i].fieldName]!=null)&&($scope.columns[i].fieldName !== "dataDate")){
                            el[$scope.columns[i].fieldName] = el[$scope.columns[i].fieldName].toFixed(3);
                        };

                    };                    
                });
                $scope.data = tmp;
            };
            var errorCallback = function(e){
                console.log(e);
                notificationFactory.errorInfo(e.statusText, e.data.description || e.data);
            };
            
            var checkUrl = function(url){
                var undefinedIndex = url.indexOf('undefined');               
                if (undefinedIndex >= 0){
                    var error = {
                        statusText : "Некорректный запрос",
                        data: {
                            request: url,
                            description: "Запрос содержит неопределенные занчения.\n Обратитесь к администратору системы"                             
                        }
                    };
                    errorCallback(error);
                    return false;
                };
                return true;
            };
            
            var getData = function(url){
                if (!checkUrl(url)) { return "request error" };
                $http.get(url).then(successCallback, errorCallback);
            };
            
            $scope.refreshData = function(){               
               var url = $scope.dataUrl + "/?beginDate=" + $scope.curDate + "&endDate=" + $scope.curDate;
                getData(url);
            };
            
            $scope.refreshData();
            
            $scope.onTableLoad= function(){
                $('#input'+$scope.type+'Date').datepicker({
                      dateFormat: "dd.mm.yy",
                      firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
                      dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
                      monthNames: $scope.dateOptsParamsetRu.locale.monthNames
                  });
            };
        }
    };
});