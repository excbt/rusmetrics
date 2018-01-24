/*global angular*/
(function() {
    'use strict';

    angular
        .module('objectTreeModule')
        .controller('objectTreeWidgetContainer', Controller);

    Controller.$inject = ['$scope'];

    /* @ngInject */
    function Controller($scope){
        var vm = this;
        vm.property = 'Controller';        
        
        vm.isLoading = true;

        $scope.$on('exportPropertiesUpdated', function (event, props) {
            if (angular.isDefined(props) && angular.isDefined(props.title)) {
                $scope.title = props.title;
            }
            if (angular.isDefined(props) && angular.isDefined(props.contObjectId) && angular.isDefined(props.contZpointId) && angular.isDefined(props.action) && props.action === "openIndicators") {
//                console.log('on export getindicators');
                if (angular.isDefined($scope.getIndicators)) {
                    $scope.getIndicators(Number(props.contObjectId), Number(props.contZpointId));
                }
            }
            if (angular.isDefined(props) && angular.isDefined(props.contObjectId) && angular.isDefined(props.action) && props.action === "openNotices") {
                if (angular.isDefined($scope.openNotices)) {
                    $scope.openNotices(Number(props.contObjectId));
                }
            }
        });
        $scope.$on('requestToGetIndicators', function (event, props) {
//            console.log('on requestToGetIndicators');
            if (angular.isDefined($scope.getIndicators)) {
                $scope.getIndicators(props.contObjectId, props.contZpointId);
            }
        });
    
        $scope.$on('widgetLoaded', function () {
            $scope.isLoading = false;
            $scope.isError = false;
        });
        $scope.$on('widgetError', function () {
            $scope.isLoading = false;
            $scope.isError = true;
        });

        $scope.reload = function () {
            $scope.isLoading = true;
            $scope.isError = false;
            $scope.$broadcast('reloadWidget');
        };
        
        activate();

        ////////////////

        function activate() {
        }
    }
})();