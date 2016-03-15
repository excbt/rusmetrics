angular.module('portalNMC')
    .controller('TempSchCtrl', ['$scope','$rootScope', '$cookies', '$window', '$http', '$location', 'crudGridDataFactory', 'FileUploader', 'notificationFactory', 'indicatorSvc', 'mainSvc',function($scope, $rootScope, $cookies, $window, $http, $location, crudGridDataFactory, FileUploader, notificationFactory, indicatorSvc, mainSvc){
        //The temperatures schedule    
        $scope.ctrlSettings = {};
        $scope.ctrlSettings.ctxId = "temp_sch_page";
        
        $scope.extraProps = {"idColumnName" : "id", "defaultOrderBy" : "place", "nameColumnName" : "name"}; 
        $scope.orderBy = { field: $scope.extraProps["defaultOrderBy"], asc: true};
        
        //organization columns
        $scope.ctrlSettings.tempSchColumns =[
            {
                "name": "place",
                "caption": "Населенный пункт",
                "class": "col-xs-3 col-md-3",
                "type": "name",
                "sortable": true
            },
            {
                "name": "rso",
                "caption": "РСО",
                "class": "col-xs-2 col-md-2",
                "type": "name",
                "sortable": true
            },
            {
                "name": "name",
                "caption": "Наименование",
                "class": "col-xs-3 col-md-3",
                "type": "name",
                "sortable": true
            },
            {
                "name": "correctivePump",
                "caption": "Кор. нас",
                "class": "col-xs-1 col-md-1",
                "type": "checkbox",
                "sortable": false
            },
            {
                "name": "elevator",
                "caption": "Элеватор",
                "class": "col-xs-1 col-md-1",
                "type": "checkbox",
                "sortable": false
            }

        ];
        
        $scope.setOrderBy = function(field){    
            if (field.sortable == false){return "The field is not sortable."};
            var asc = $scope.orderBy.field === field.name ? !$scope.orderBy.asc : true;
            $scope.orderBy = { field: field.name, asc: asc };
        };
        
    }]);