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
        controller: function($scope, $element, $attrs, $http, notificationFactory, mainSvc){            
            
            $scope.dirSettings = {};
            $scope.dirSettings.userFormat = "DD.MM.YYYY"; //date format
            $scope.dirSettings.requestFormat = "YYYY-MM-DD"; //date format
            $scope.dirSettings.dataDate = moment().endOf('day').format($scope.dirSettings.userFormat);
            
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
            
            var successCallback = function(response){
//console.log(response.data);                
                var tmp = angular.copy(response.data);
                tmp.forEach(function(el){
                    var result  = {};
                    for(var i in $scope.columns){
                        if ((el[$scope.columns[i].fieldName]!=null)&&($scope.columns[i].type !== "string")){
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
//console.log(moment($scope.dirSettings.dataDate, $scope.dirSettings.userFormat).format($scope.dirSettings.requestFormat));                
                var requestDate = moment($scope.dirSettings.dataDate, $scope.dirSettings.userFormat).format($scope.dirSettings.requestFormat)
                var url = $scope.dataUrl + "/?beginDate=" + requestDate + "&endDate=" + requestDate;
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
            
            $scope.isSystemuser = function(){
                return mainSvc.isSystemuser();
            };
        }
    };
});