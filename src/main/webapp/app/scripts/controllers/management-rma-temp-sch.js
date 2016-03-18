angular.module('portalNMC')
    .controller('TempSchCtrl', ['$scope','$rootScope', '$cookies', '$window', '$http', '$location', 'crudGridDataFactory', 'FileUploader', 'notificationFactory', 'indicatorSvc', 'mainSvc', '$timeout', function($scope, $rootScope, $cookies, $window, $http, $location, crudGridDataFactory, FileUploader, notificationFactory, indicatorSvc, mainSvc, $timeout){
        //The temperatures schedule    
        $scope.ctrlSettings = {};
        $scope.ctrlSettings.ctxId = "temp_sch_page";

        $scope.ctrlSettings.isTempSchSort = true;
        
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
        
        $scope.ctrlSettings.emptySchedule = {
            tEnv: "",
            tIn: "",
            tOut: ""
        };
        
        $scope.data = {};
        $scope.data.currentTempSch = {};
        $scope.data.currentTempSch.schedules = [angular.copy($scope.ctrlSettings.emptySchedule)];
        
        $scope.data.tempSchedules = [
            {
                place: "Москва",
                rso: "Первая столичная РСО",
                name: "График 1",
                correctivePump: true,
                elevator: true
            },
            {
                place: "Москва",
                rso: "Первая столичная РСО",
                name: "105 / 70",
                correctivePump: false,
                elevator: false
            }
        ];
                
        $scope.setOrderBy = function(field){    
            if (field.sortable == false){return "The field is not sortable."};
            var asc = $scope.orderBy.field === field.name ? !$scope.orderBy.asc : true;
            $scope.orderBy = { field: field.name, asc: asc };
        };
        
        $scope.addTempSch = function(){
            $scope.data.currentTempSch = {};
            $scope.data.currentTempSch.schedules = [angular.copy($scope.ctrlSettings.emptySchedule)];
        };
        
        $scope.addSchedule = function(tempSch){          
            tempSch.schedules.push(angular.copy($scope.ctrlSettings.emptySchedule));
            $scope.ctrlSettings.isTempSchSort = false;
        };
        
        $scope.sortSchedulesByTEnv = function(schedules){          
            if (!angular.isArray(schedules)){
                return "Schedules is not array;";
            };
            schedules.sort(function(a, b){
                if (Number(a.tEnv) > Number(b.tEnv)){
                    return -1;
                };
                if (Number(a.tEnv) < Number(b.tEnv)){
                    return 1;
                };
                return 0;
            });
            $scope.ctrlSettings.isTempSchSort = true;    
        };
        
        $scope.deleteSchedule = function(index, targetArr){
            var curInd = mainSvc.checkUndefinedNull(index) ? -1 : index;          
            if (curInd!=-1){
                targetArr.splice(curInd, 1);
            };
        };
        
        $scope.checkSchRow = function(schRow){
            return schRow.tEnv > '' && 
                schRow.tIn > '' && 
                schRow.tOut > '' && 
                Number(schRow.tIn) > 0 &&
                Number(schRow.tOut) > 0 &&
                Number(schRow.tIn) >= Number(schRow.tOut)
        };
        
        var checkTempSch = function(tempSch){
console.log(tempSch);            
            var result = true;
            //Check tMax, tMin, name, localePlace, rso and schedules
            if (!mainSvc.checkPositiveNumberValue(tempSch.tMax)){
                notificationFactory.errorInfo("Ошибка", "Максимальная температура должна быть положительным числом!");
                result = false;
            };
            if (!mainSvc.checkPositiveNumberValue(tempSch.tMin)){
                notificationFactory.errorInfo("Ошибка", "Минимальная температура должна быть положительным числом!");
                result = false;
            };
            if (Number(tempSch.tMax) < Number(tempSch.tMin)){
                notificationFactory.errorInfo("Ошибка", "Максимальная температура должна быть больше минимальной!");
                result = false;
            };
            if (mainSvc.checkUndefinedEmptyNullValue(tempSch.name)){
                notificationFactory.errorInfo("Ошибка", "Поле \"Наименование\" должно быть заполнено!");
                result = false;
            };
            if (mainSvc.checkUndefinedNull(tempSch.localePlace)){
                notificationFactory.errorInfo("Ошибка", "Поле \"Населенный пункт\" должно быть заполнено!");
                result = false;
            };
            if (mainSvc.checkUndefinedNull(tempSch.rso)){
                notificationFactory.errorInfo("Ошибка", "Поле \"РСО\" должно быть заполнено!");
                result = false;
            };
            tempSch.schedules.some(function(sch){
                if ($scope.checkSchRow(sch) == false){
                    notificationFactory.errorInfo("Ошибка", "Неправильно заполнен температурный график!");
                    result = false;
                    return true;
                }
            });
            return result;
            
        };
        
        $scope.saveTempSch = function(tempSch){
            //check tempSch
            var isTempSchChecked = checkTempSch(tempSch);
            if (isTempSchChecked == false){
                notificationFactory.successInfo("Проверка не пройдена.");
            }else{
                notificationFactory.successInfo("Проверка пройдена.");
            };
            //save
        };
        
        $scope.inputTempBlur =  function(sch){            
            if (mainSvc.checkUndefinedNull(sch.tMax) || mainSvc.checkUndefinedNull(sch.tMin)){
                return "Tmax or Tmin is null or undefined";
            };            
            if (mainSvc.checkUndefinedNull(sch.name) || (sch.name == "")){
                sch.name = sch.tMax + "/" + sch.tMin;
            };
        };
        
        var isInputTOutFocus = false;
        var inputTOutFocusHandler = function(){
               isInputTOutFocus = true;
        };
        var inputTOutBlurHandler = function(event){                                               
            isInputTOutFocus = false;
            $("#"+event.currentTarget.id).off("focus", inputTOutFocusHandler);
        };
        
        var tempSchModalKeydownHandler = function(e){
//console.log(e);                
            if (e.keyCode == 107 || e.keyCode == 187/*нажата кнопка "+" или "="*/){
                $scope.addSchedule($scope.data.currentTempSch);
                $scope.$apply();
            };
            if (e.keyCode == 9 /* Tab*/){                    
                //Если текущее редактируемое поле "Тобр", то добавляем следующую строку графика
                if (isInputTOutFocus == true){                       
                    $scope.addSchedule($scope.data.currentTempSch);
                    $scope.$apply();
                };
            };
        };
        
        $scope.initSchedule = function(index){
            $timeout(function(){
                $("#inputTEnv" + index).inputmask();
                $("#inputTEnv" + index).focus();//set focus on t env input
                $("#inputTIn" + index).inputmask();
                $("#inputTOut" + index).inputmask();
                // set focus flag
                $("#inputTOut" + index).focus(inputTOutFocusHandler);
                $("#inputTOut" + index).blur(inputTOutBlurHandler);
            }, 10);
        };
        
        $('#showTempSchModal').on('shown.bs.modal', function(){
            $('#inputTmax').focus();
            $('#showTempSchModal').keydown(tempSchModalKeydownHandler);
        });
        
        $('#showTempSchModal').on('hidden.bs.modal', function(){            
            $('#showTempSchModal').off("keydown", tempSchModalKeydownHandler);
        });
        
            //set mask for price value
        $(document).ready(function(){
            $(':input').inputmask();
        });
        
    }]);