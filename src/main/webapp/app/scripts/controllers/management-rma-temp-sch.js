angular.module('portalNMC').directive("fileread", [function () {
    return {
        scope: {
            fileread: "="
        },
        link: function (scope, element, attributes) {
            element.bind("change", function (changeEvent) {
                var reader = new FileReader();
                reader.onload = function (loadEvent) {
                    scope.$apply(function () {
                        scope.fileread = loadEvent.target.result;
                    });
                }                
                reader.readAsText(changeEvent.target.files[0]);
            });
        }
    }
}]);
angular.module('portalNMC')
    .controller('TempSchCtrl', ['$scope','$rootScope', '$cookies', '$window', '$http', '$location', 'crudGridDataFactory', 'FileUploader', 'notificationFactory', 'indicatorSvc', 'mainSvc', '$timeout', 'objectSvc', function($scope, $rootScope, $cookies, $window, $http, $location, crudGridDataFactory, FileUploader, notificationFactory, indicatorSvc, mainSvc, $timeout, objectSvc){
        //The temperatures schedule    
        $scope.ctrlSettings = {};
        $scope.ctrlSettings.ctxId = "temp_sch_page";

        $scope.ctrlSettings.dateFormat = "DD.MM.YYYY"; //date format
        $scope.ctrlSettings.systemDateFormat = "YYYY-MM-DD"; //date format
        $scope.ctrlSettings.isTempSchSort = true;
        
        $scope.ctrlSettings.localPlacesUrl = "../api/rma/localPlaces";
        $scope.ctrlSettings.tempSchUrl = "../api/rma/temperatureCharts";
        
        $scope.extraProps = {"idColumnName" : "id", "defaultOrderBy" : "place", "nameColumnName" : "name"}; 
        $scope.orderBy = { field: $scope.extraProps["defaultOrderBy"], asc: true};
        
        //temperature schedules columns
        $scope.ctrlSettings.tempSchColumns =[
            {
                "name": "localPlaceName",
                "caption": "Населенный пункт",
                "class": "col-xs-3 col-md-3",
                "type": "name",
                "sortable": true
            },
            {
                "name": "rsoOrganizationName",
                "caption": "РСО",
                "class": "col-xs-4 col-md-4",
                "type": "name",
                "sortable": true
            },
            {
                "name": "chartName",
                "caption": "Наименование",
                "class": "col-xs-3 col-md-3",
                "type": "name",
                "sortable": true
            },
            {
                "name": "flagAdjPump",
                "caption": "Кор. нас",
                "class": "nmc-td-for-buttons",
                "type": "checkbox",
                "sortable": false
            },
            {
                "name": "flagElevator",
                "caption": "Элеватор",
                "class": "nmc-td-for-buttons",
                "type": "checkbox",
                "sortable": false
            }

        ];
        
                //average envirment temperature columns
        $scope.ctrlSettings.sstColumns =[
            {
                "name": "sstDate",
                "caption": "Дата",
                "class": "col-xs-3 col-md-3",
                "type": "name",
                "sortable": true
            },
            {
                "name": "sstCalcValue",
                "caption": "Расчитанное значение",
                "class": "col-xs-4 col-md-4",
                "type": "outnumber",
                "sortable": true,
                "temperature": true
            },
            {
                "name": "sstValue",
                "caption": "Внесенное значение",
                "class": "col-xs-4 col-md-4",
                "type": "input",
                "editable": true,
                "temperature": true
            }
//            ,
//            {
//                "name": "",
//                "caption": "",
//                "class": "col-xs-7 col-md-7",
//                "type": "hidden"
//            }
        ];
        
        $scope.ctrlSettings.emptySchedule = {
            t_Ambience: "",
            t_In: "",
            t_Out: "",
            isDeleted: false
        };
        
        $scope.data = {};
        $scope.data.currentTempSch = {};
        $scope.data.currentTempSch.schedules = [angular.copy($scope.ctrlSettings.emptySchedule)];
        
        $scope.data.tempSchedules = [];
        
        //the average temperature - среднесуточная температура
        $scope.data.currentLocalPlace = {};
//        $scope.date.currentSSTDate = moment().format();
        $scope.data.aveTemps = [];
        
        $scope.data.rsoOrganizations = [];
        $scope.data.localPlaces = [];
        $scope.data.currentLocalPlace = {};
        
        var successCallback = function(){
            $('#showTempSchModal').modal('hide');
            $('#deleteWindowModal').modal('hide');
            getTempSchedules();
        };
        
        var errorCallback = function (e) {
            console.log(e);
            var errorCode = "-1";
            if (mainSvc.checkUndefinedNull(e) || mainSvc.checkUndefinedNull(e.data)){
                errorCode = "ERR_CONNECTION";
            };
            if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || !mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode))){
                errorCode = e.resultCode || e.data.resultCode;
            };
            var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
            notificationFactory.errorInfo(errorObj.caption, errorObj.description);
        };
        
        var getOrganizations = function(){        
            objectSvc.getRsoOrganizations().then(function(resp){
                $scope.data.rsoOrganizations = resp.data;
                getTempSchedules();
            }, errorCallback);
        };
        
        var getAllLocalPlaces = function(){
            $http.get($scope.ctrlSettings.localPlacesUrl + "/all").then(function(resp){
                $scope.data.localPlaces = resp.data;
                if ($scope.data.localPlaces.length > 0){
                    $scope.data.currentLocalPlace.id = $scope.data.localPlaces[0].id;
                };
                getOrganizations();
            }, errorCallback);
        };
        
        var findItemById = function(itemId, sourceArray){
            var item = null;
            sourceArray.some(function(elem){
                if (elem.id == itemId){
                    item = angular.copy(elem);
                    return true;
                };
            });
            return item;
        };
        
        $scope.getSST = function(localPlaceId, dateString){
//console.log(localPlaceId);
//console.log(dateString);            
            if (mainSvc.checkUndefinedNull(dateString)){
                return "dateString is undefined or null!";
            };
            if (mainSvc.checkUndefinedNull(localPlaceId)){
                return "localPlaceId is undefined or null!";
            };
            var url = $scope.ctrlSettings.localPlacesUrl + "/" + localPlaceId + "/sst?sstDateStr=" + dateString;
            $http.get(url).then(function(resp){
                $scope.data.aveTemps = resp.data;                
                $scope.orderBy = {'field' : "sstDate", 'asc' : true}; //set order by for SST
                $timeout(function(){            
                    $('.nmc-input-numeric').inputmask();
                }, 10);
            }, errorCallback);
        };
        
        $scope.saveSST = function(localPlaceId, dateString){
            var url = $scope.ctrlSettings.localPlacesUrl + "/" + localPlaceId + "/sst/array?sstDateStr=" + dateString;
            $http.put(url, $scope.data.aveTemps).then(function(resp){
                notificationFactory.success();
                $scope.getSST(localPlaceId, dateString);                
            }, errorCallback);
        };
        
        $scope.sstKeydown = function(ev, arr, index){
            if (ev.keyCode == 13 /* Enter */){                
                if (index < (arr.length-1)){
                    $('#inputsstValue'+arr[index+1].id).focus();
                };
            };           
        };
        
        var getTempSchedulesItems = function(sch){
            $http.get($scope.ctrlSettings.tempSchUrl + "/" + sch.id + "/items").then(function(resp){
                sch.schedules = resp.data;
                sch.schedules.forEach(function(sch){
                    sch.isDeleted = false;
                });
            }, errorCallback);
        };
        
        var getTempSchedules = function(){
            $http.get($scope.ctrlSettings.tempSchUrl).then(function(resp){
                $scope.data.tempSchedules = angular.copy(resp.data);                
                $scope.data.tempSchedules.forEach(function(elem){
                    var localPlace = findItemById(elem.localPlaceId, $scope.data.localPlaces);
                    elem.localPlaceName = (localPlace == null) ? "Неопределено" : localPlace.localPlaceName;
                    var rso = findItemById(elem.rsoOrganizationId, $scope.data.rsoOrganizations)
                    elem.rsoOrganizationName = (rso == null) ? "Неопределено" : rso.organizationName;
                    getTempSchedulesItems(elem);
                });
                
            }, errorCallback);
        };
        
        $scope.selectedItem = function(item){           
            setConfirmCode();
            $scope.data.currentTempSch = angular.copy(item);           
        };
                
        $scope.setOrderBy = function(field){    
            if (field.sortable == false){return "The field is not sortable."};
            var asc = $scope.orderBy.field === field.name ? !$scope.orderBy.asc : true;
            $scope.orderBy = { field: field.name, asc: asc };
        };
        
        $scope.addTempSch = function(){
            $scope.data.currentTempSch = {};
            $scope.data.currentTempSch.chartDeviationValue = 3;//по умолчанию отклонение = 3 - требование заказчика
            $scope.data.currentTempSch.schedules = [angular.copy($scope.ctrlSettings.emptySchedule)];
        };
        
        var postItem = function(tempSchId, item){
            var url = $scope.ctrlSettings.tempSchUrl + "/" + tempSchId + "/items";
            $http.post(url, item).then(function(resp){                                  
            }, errorCallback);
        };
        
        var putItem = function(tempSchId, item){
            var url = $scope.ctrlSettings.tempSchUrl + "/" + tempSchId + "/items/" + item.id;
            $http.put(url, item).then(function(resp){                                    
            }, errorCallback);
        };
        
        var deleteItem = function(tempSchId, item){
            var url = $scope.ctrlSettings.tempSchUrl + "/" + tempSchId + "/items/" + item.id;
            $http.delete(url).then(function(resp){                                   
            }, errorCallback);
        };
        
        $scope.addSchedule = function(tempSch){ 
            tempSch.schedules.push(angular.copy($scope.ctrlSettings.emptySchedule));
            $scope.ctrlSettings.isTempSchSort = false;
        };
        
        $scope.sortSchedulesByTAmbience = function(schedules){          
            if (!angular.isArray(schedules)){
                return "Schedules is not array;";
            };
            schedules.sort(function(a, b){
                if (Number(a.t_Ambience) > Number(b.t_Ambience)){
                    return -1;
                };
                if (Number(a.t_Ambience) < Number(b.t_Ambience)){
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
        
        $scope.delSchItem = function(item){
            item.isDeleted = true;
        };
        
        $scope.isNumeric = function(num){
            return mainSvc.isNumeric(num);
        };
        
        $scope.checkSchRow = function(schRow){
//console.log(schRow);            
//console.log($scope.isNumeric(schRow.t_Ambience));            
//console.log(schRow.t_In > '');            
//console.log(schRow.t_Out > '');
//console.log(Number(schRow.t_In) > 0);
//console.log(Number(schRow.t_Out) > 0);
//console.log(Number(schRow.t_In) >= Number(schRow.t_Out));            
            return $scope.isNumeric(schRow.t_Ambience) && 
                schRow.t_In > '' && 
                schRow.t_Out > '' && 
                Number(schRow.t_In) > 0 &&
                Number(schRow.t_Out) > 0 &&
                Number(schRow.t_In) >= Number(schRow.t_Out)
        };
        
        $scope.isAddButtonEnabled = function(pos, schedules){
            var result = false;
            var lenSchedulesWitoutDeleted = 0;
            schedules.forEach(function(elem){
                if (elem.isDeleted != true){
                    lenSchedulesWitoutDeleted++;
                };
            });
//console.log(pos);            
//console.log(lenSchedulesWitoutDeleted);
//console.log(schedules.length);            
            return pos == (lenSchedulesWitoutDeleted - 1);
        };
        
        $scope.isRemoveButtonEnabled = function(schedules){
            var result = false;
            var lenSchedulesWitoutDeleted = 0;
            schedules.forEach(function(elem){
                if (elem.isDeleted != true){
                    lenSchedulesWitoutDeleted++;
                };
            });
            if (lenSchedulesWitoutDeleted > 1){
                result = true;
            };
            return result;
        };
        
        var checkTempSch = function(tempSch){
//console.log(tempSch);            
            var result = true;
            //Check tMax, tMin, name, localPlace, rso and schedules
            if (!mainSvc.checkPositiveNumberValue(tempSch.maxT)){
                notificationFactory.errorInfo("Ошибка", "Максимальная температура должна быть положительным числом!");
                result = false;
            };
            if (!mainSvc.checkPositiveNumberValue(tempSch.minT)){
                notificationFactory.errorInfo("Ошибка", "Минимальная температура должна быть положительным числом!");
                result = false;
            };
            if (Number(tempSch.maxT) < Number(tempSch.minT)){
                notificationFactory.errorInfo("Ошибка", "Максимальная температура должна быть больше минимальной!");
                result = false;
            };
            if (mainSvc.checkUndefinedEmptyNullValue(tempSch.chartName)){
                notificationFactory.errorInfo("Ошибка", "Поле \"Наименование\" должно быть заполнено!");
                result = false;
            };
            if (mainSvc.checkUndefinedNull(tempSch.localPlaceId)){
                notificationFactory.errorInfo("Ошибка", "Поле \"Населенный пункт\" должно быть заполнено!");
                result = false;
            };
            if (mainSvc.checkUndefinedNull(tempSch.rsoOrganizationId)){
                notificationFactory.errorInfo("Ошибка", "Поле \"РСО\" должно быть заполнено!");
                result = false;
            };
            if (mainSvc.checkUndefinedEmptyNullValue(tempSch.chartDeviationValue)){
                notificationFactory.errorInfo("Ошибка", "Поле \"Допустимое отклонение\" должно быть заполнено!");
                result = false;
            };
//            if (!mainSvc.checkPositiveNumberValue(tempSch.chartDeviationValue)){
//                notificationFactory.errorInfo("Ошибка", "Поле \"Допустимое отклонение\" должно быть заполнено!");
//                result = false;
//            };
            tempSch.schedules.some(function(sch){
                if (sch.isDeleted != true && $scope.checkSchRow(sch) == false){
                    notificationFactory.errorInfo("Ошибка", "Неправильно заполнен температурный график!");
                    result = false;
                    return true;
                }
            });
            return result;
            
        };
        
        var convertTempSchItemFromStrToNum = function(sch){
            sch.t_Ambience = Number(sch.t_Ambience);
            sch.t_In = Number(sch.t_In);
            sch.t_Out = Number(sch.t_Out);
        };
        
        var successPostCallback = function(resp){            
            var tempSchId = resp.data.id;
            if (angular.isArray($scope.data.currentTempSch.schedules)){
                $scope.sortSchedulesByTAmbience($scope.data.currentTempSch.schedules);
                $scope.data.currentTempSch.schedules.forEach(function(sch){
                    if (sch.isDeleted != true) {
                        convertTempSchItemFromStrToNum(sch);
                        postItem(tempSchId, sch);
                    };
                });
            };
            successCallback();
        };
        
        var successPutCallback = function(resp){            
            var tempSchId = resp.data.id;
            if (angular.isArray($scope.data.currentTempSch.schedules)){
                $scope.sortSchedulesByTAmbience($scope.data.currentTempSch.schedules);
                $scope.data.currentTempSch.schedules.forEach(function(sch){
                    if ((sch.isDeleted == true) && !mainSvc.checkUndefinedNull(sch.id)){
                        deleteItem(tempSchId, sch);
                        return "Item (id = " + sch.id + ") of tempSch ("+ tempSchId +") was deleted!";
                    };
                    convertTempSchItemFromStrToNum(sch);
                    if (mainSvc.checkUndefinedNull(sch.id)){                        
                        postItem(tempSchId, sch);
                    }else{
                        putItem(tempSchId, sch);
                    };                    
                });
            };
            successCallback();
        };
        
        $scope.saveTempSch = function(tempSch, isOk){
            //check tempSch
            var isTempSchChecked = checkTempSch(tempSch);
            if (isTempSchChecked == false){
                return "Temperature schedule is not valid!";
            };
                
            var url = $scope.ctrlSettings.tempSchUrl;
            //tempSch.id - null (post) or not (put)
            var method = 'POST';
            var sucCallback = successPostCallback;
            if (!mainSvc.checkUndefinedNull(tempSch.id)){
                method = 'PUT';
                sucCallback = successPutCallback;                    
                url += "/" + tempSch.id;
                tempSch.localPlaceInfo = null;
                tempSch.rsoOrganizationInfo = null;
            };
            //prop 'isOk' = true
            if (!mainSvc.checkUndefinedNull(isOk)){
                tempSch.isOk = isOk;
            };
//                $http.put(url, tempSch).then(successCallback, errorCallback);
            $http({
                url: url,
                method: method,
                data: tempSch,
            }).then(sucCallback, errorCallback);            
        };
        
                // Проверка пользователя - системный/ не системный
        $scope.isSystemuser = function(){
            return mainSvc.isSystemuser();
        };
        
        var setConfirmCode = function(){
            $scope.confirmCode = null;
            var tmpCode = mainSvc.getConfirmCode();
            $scope.confirmLabel = tmpCode.label;
            $scope.sumNums = tmpCode.result;                    
        };
        
        $scope.deleteTempSch = function(tempSch){
            $http.delete($scope.ctrlSettings.tempSchUrl + "/" + tempSch.id).then(successCallback, errorCallback);
        };
        
        $scope.inputTempBlur =  function(sch){            
            if (mainSvc.checkUndefinedNull(sch.maxT) || mainSvc.checkUndefinedNull(sch.minT)){
                return "Tmax or Tmin is null or undefined";
            };            
            if (mainSvc.checkUndefinedNull(sch.chartName) || (sch.chartName == "")){
                sch.chartName = sch.maxT + "/" + sch.minT;
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
            //keyCode = 13 - Enter
            if (e.keyCode == 107 || e.keyCode == 187/*нажата кнопка "+" или "="*/){
                $scope.addSchedule($scope.data.currentTempSch);
                $scope.$apply();
            };
            if (e.shiftKey == false && e.keyCode == 9 /* no shift && Tab*/){                    
                //Если текущее редактируемое поле "Тобр", то добавляем следующую строку графика
                if (isInputTOutFocus == true){                       
                    $scope.addSchedule($scope.data.currentTempSch);
                    $scope.$apply();
                };
            };
//            if (e.keyCode == 13 /* Enter*/){                    
//                //Если текущее редактируемое поле "Тобр", то добавляем следующую строку графика
//                if (isInputTOutFocus == true){                       
//                    $scope.addSchedule($scope.data.currentTempSch);
//                    $scope.$apply();
//                };
//            };
        };
        
        $scope.scheduleKeydown = function(ev, index, length){
            if (ev.keyCode == 13 /* Enter */){                
                if (index == (length-1)){
                    $scope.addSchedule($scope.data.currentTempSch);
                }else{
                    $('#inputtAmbience'+(index+1)).focus();
                };
            };           
        };
        
        $scope.initSchedule = function(index){
            $timeout(function(){
                $("#inputtAmbience" + index).inputmask();
                $("#inputtAmbience" + index).focus();//set focus on t env input
                $("#inputTIn" + index).inputmask();
                $("#inputTOut" + index).inputmask();
                // set focus flag
                    //if it is not last row - then exit
                if (index < ($scope.data.currentTempSch.schedules.length-1)){
                    return;
                };
                $("#inputTOut" + index).focus(inputTOutFocusHandler);
                $("#inputTOut" + index).blur(inputTOutBlurHandler);
            }, 10);
        };
        
        $('#showTempSchModal').on('shown.bs.modal', function(){
            $('#inputTmax').focus();
            $('#showTempSchModal').keydown(tempSchModalKeydownHandler);            
            $('#inputChartDeviation').inputmask();
        });
        
        $('#showTempSchModal').on('hidden.bs.modal', function(){            
            $('#showTempSchModal').off("keydown", tempSchModalKeydownHandler);
        });
        
                //date picker
        $scope.dateOptsParamsetRu ={
            locale : {
                daysOfWeek : [ 'Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб' ],
                firstDay : 1,
                monthNames : [ 'Январь', 'Февраль', 'Март', 'Апрель',
                        'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь',
                        'Октябрь', 'Ноябрь', 'Декабрь' ]
            },
            singleDatePicker: true,
            format: $scope.ctrlSettings.dateFormat
        };
            //set mask for price value
        $(document).ready(function(){
            $(':input').inputmask();                       
            $('#inputSSTDate').datepicker({
              dateFormat: "MM, yy",
              firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
              dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
              monthNames: $scope.dateOptsParamsetRu.locale.monthNames,
                monthNamesShort: ['Янв','Фев','Мар','Апр','Май','Июн','Июл','Авг','Сен','Окт','Ноя','Дек'],
                changeMonth: true,
                changeYear: true,
                showButtonPanel: true,
                closeText: "Готово",
                currentText: "Сегодня",
                onClose: function(dateText, inst) { 
                    $(this).datepicker('setDate', new Date(inst.selectedYear, inst.selectedMonth, 1));
                    $scope.data.currentSSTDate = moment(new Date(inst.selectedYear, inst.selectedMonth, 1)).format($scope.ctrlSettings.systemDateFormat);
                    $scope.getSST($scope.data.currentLocalPlace.id, $scope.data.currentSSTDate);
                    setTimeout(function(){
                        $('.ui-datepicker-calendar').addClass("nmc-hide");
                    }, 1);
                },
                beforeShow: function(){
                    setTimeout(function(){
                        $('.ui-datepicker-calendar').addClass("nmc-hide");
                    }, 1);
                },
                onChangeMonthYear: function(){
                    setTimeout(function(){
                        $('.ui-datepicker-calendar').addClass("nmc-hide");
                    }, 1);
                }
            });
        });     
        
                //Upload file 
        $scope.uploadFile = function(){
//console.log($scope.data.dataFile);             
//console.log(typeof $scope.data.dataFile);
            if (mainSvc.checkUndefinedNull($scope.data.dataFile)){
                notificationFactory.errorInfo("Загрузка файла", "Файл не выбран.");
                return "Management temperature schedules. File loading is failed";
            };
            var fileLoadedFlag = false;
            var strArray = $scope.data.dataFile.split('\n');
//console.log(strArray);                                    
            strArray.forEach(function(dataStr){
                var strWords = dataStr.split(',');
                if (!angular.isArray(strWords)){
                    console.log(dataStr + " is not a correct data string!");
                    return dataStr + " is not a correct data string!";
                };
                var dataDate = strWords[0];                    
                var fd = new Date("\""+dataDate+"\"");          
//console.log(fd);                                        
                $scope.data.aveTemps.some(function(sst){                                                       
                    var td = new Date("\""+sst.sstDate+"\"");
                    if (fd.getTime() == td.getTime()){
//console.log(td);                        
                        var dataValue = strWords[1];
                        if (mainSvc.isNumeric(dataValue)){
                            sst.sstValue = Number(dataValue);
                            sst.isChanged = true;
                            fileLoadedFlag = true;
                        }else{                                                      
                            console.log(dataValue + " is not a number");
                        };
                        return true;
                    };
                });                
            });
            if (fileLoadedFlag == true){
                notificationFactory.successInfo("Файл успешно загружен.");
            }else{
                notificationFactory.errorInfo("Загрузка файла", "Некорректный формат файла");
            };
            $('#upLoadFileModal').modal('hide');
        };
        
        var initCtrl = function(){
//console.log(new Date());            
            getAllLocalPlaces();
//            getOrganizations();
//            getTempSchedules();
        };
        
        initCtrl();
    }]);