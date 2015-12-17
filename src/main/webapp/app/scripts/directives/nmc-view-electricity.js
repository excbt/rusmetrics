angular.module('portalNMC')
.directive('nmcViewElectricity', function(){
    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'scripts/directives/templates/nmc-view-electricity.html',
        controller: function($scope, $element, $attrs, $http, notificationFactory){
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
console.log($scope.dataUrl);            
console.log($scope.columns);                                    
            var successCallback = function(response){
                $scope.data = response.data;
            };
            var errorCallback = function(e){
                console.log(e);
                notificationFactory.errorInfo(e.statusText, e.data.description || e.data);
            };
            var getData = function(){
                $http.get(url).then(successCallback, errorCallback);
            };
            
            $(document).ready(function() {
console.log($('#input'+$scope.electroKind+'Date'));                
//                  $('#input'+$scope.electroKind+'Date').datepicker({
                $('#inputElectroDate').datepicker({
                      dateFormat: "dd.mm.yy",
                      firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
                      dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
                      monthNames: $scope.dateOptsParamsetRu.locale.monthNames
                  });
            });
        }
    };
});