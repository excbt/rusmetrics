'use strict'

var app = angular.module('portalNMC');

app.controller('NoticeCtrl', function($scope, $http, $resource, $rootScope, $cookies, $location, crudGridDataFactory, objectSvc, notificationFactory){
console.log("Load NoticeCtrl.");    
//console.log("$('#div-main-area').width()=");    
//console.log($('#div-main-area').width()); 
//    
//console.log("$('td.col-md-4').width()=");    
//console.log(Math.round($('#div-main-area').width()*8.333/100*4));  
//    
//console.log("$('td.col-md-3').width()=");    
//console.log(Math.round($('#div-main-area').width()*8.333/100*3));     
//    Math.round($('#div-main-area').width()*8.333/100)= 130 = 20*x; => x= 130/20
    //Math.round($('#div-main-area').width()*8.333/100/6.5) //dynamic coef
    $scope.TEXT_CAPTION_LENGTH = 20*4-5; //length of message visible part. Koef 4 for class 'col-md-4', for class 'col-md-3' koef = 3 and etc.
    $scope.TYPE_CAPTION_LENGTH = 20*3-5; //length of type visible part 
    $scope.objectsUrl= "../api/subscr/contObjects";
    $scope.crudTableName= "../api/subscr/contEvent/notifications";
    $scope.noticeTypesUrl= "../api/contEvent/types";
    //the path template of notice icon
    $scope.imgPathTmpl = "images/notice-state-";
    
    //get url params
    var loca = $location.search();
console.log(loca);    
    
    
    //messages for user
    $scope.messages = {};
    $scope.messages.markSelectedAsRevision = "Пометить выделенные как прочитанные";
    $scope.messages.markSelectedAsNew = "Пометить выделенные как непрочитанные";
    $scope.messages.markOnPageAsRevision = "Пометить все на странице как прочитанные";
    $scope.messages.markAllAsRevision = "Пометить все как прочитанные";
    $scope.messages.markAllAsNew = "Пометить все как непрочитанные";
    
    $scope.states = {};//object, which keep the states of the notice objects
    $scope.states.applyObjects_flag = false; //flag, which keep state of object filter: true - the objects had been selected and to need them at the filter.
    $scope.states.applyTypes_flag = false; //flag, which keep state of types filter: true - the types had been selected and to need them at the filter.
    $scope.states.criticalTypes_flag = false;
    $scope.states.noCriticalTypes_flag = false;
    $scope.states.undefinedCriticalTypes_flag = false;
    //This flags keep the states in the selection types window
    $scope.states.tempCriticalTypes_flag = false;
    $scope.states.tempNoCriticalTypes_flag = false;
    $scope.states.tempUndefinedCriticalTypes_flag = false;
    
    $scope.allSelected = false;
    
    $scope.tableDef = {
        tableClass : "crud-grid table table-lighter table-condensed table-hover table-striped",
        hideHeader : false,
        headerClassTR : "nmc-main-table-header",
        columns : [ 

        {
            fieldName : "noticeDate",
            header : "Дата",
            headerClass : "col-md-1",
            dataClass : "col-md-1"
        },{
            fieldName : "noticeType",
            header : "Тип",
            headerClass : "col-md-1 nmc-hide",
            dataClass : "col-md-1 nmc-hide"
        }, {
            fieldName : "noticeMessage",
            header : "Уведомление",
            headerClass : "col-md-1 nmc-hide",
            dataClass : "col-md-1 nmc-hide"
        },
        {
            fieldName : "noticeTypeCaption",
            header : "Тип",
            headerClass : "col-md-3",
            dataClass : "col-md-3"
        }, {
            fieldName : "noticeCaption",
            header : "Уведомление",
            headerClass : "col-md-4",
            dataClass : "col-md-4"
        },  {
            fieldName : "noticeObjectName",
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
    
    $scope.data = {};
    
    $scope.currentObject = {};
    $scope.selectedObjects = [];
    
    $scope.notices = [];
    $scope.totalNotices = 0;
    $scope.noticesPerPage = 25; // this should match however many results your API puts on one page
    
    //Controller initialization
    //use to redirect from the Monitor page
    $scope.initCtrl = function(){
console.log("initCtrl"); 
//for(var k in $cookies){        
//    console.log("$cookies["+k+"]="+$cookies[k]); 
//};
//        if ((angular.isDefined($cookies))&&($cookies.hasOwnProperty('monitorFlag'))&&($cookies.monitorFlag)){
        if ((angular.isDefined(loca))&&(loca.hasOwnProperty('monitorFlag'))&&(loca.monitorFlag==="true")){    
//            loca.monitorFlag = false;
//            $rootScope.reportStart=$rootScope.monitor.fromDate;
//            $rootScope.reportEnd=$rootScope.monitor.toDate;
            $scope.objectsInWindow = angular.copy($scope.objects);           
            var curIndex = -1; 
            $scope.objectsInWindow.some(function(element, index){
                if (element.id === Number(loca.objectMonitorId)){
                    curIndex = index;
                    return true;
                }
            });  
//console.log("curIndex = "+curIndex);            
            if (curIndex>-1){//object is need - else is absurd
                //object
                $scope.objectsInWindow[curIndex].selected=true;
                performObjectsFilter();               
                //new / revision               
                $scope.isNew = loca.isNew==="null"?null:Boolean(loca.isNew); 
//console.log($scope.isNew);                
                if ($scope.isNew===true){
                    $scope.visibleText='Только новые';
                };
                //types
                var tmpTypesArr = [Number(loca.typeId)];
//console.log(tmpTypesArr);                
                if ((angular.isDefined(tmpTypesArr))&&(tmpTypesArr.hasOwnProperty('length'))&&(tmpTypesArr.length>0)){
                    $scope.typesInWindow = angular.copy($scope.noticeTypes);
//console.log($scope.noticeTypes);                    
                    tmpTypesArr.forEach(function(element){
                        var curTypeIndex = -1;
                        $scope.typesInWindow.some(function(elem, index){
                            if (elem.id === element){
                                curTypeIndex = index;
                                return true;
                            };  
                        });
//console.log("curTypeIndex = "+curTypeIndex);                         
                        if (curTypeIndex>-1){
                            $scope.typesInWindow[curTypeIndex].selected = true;
                        };
                    });
                    performNoticeTypesFilter();
                };
//                $scope.getResultsPage(1);
            };
        };
    };
    
    var getNotices = function(table, startDate, endDate, objectArray, eventTypeArray, isNew){
//console.log("Run GetNotices");              
        if (isNew==null){
            return $resource(table, {},
                         {'get':{method:'GET', params:{fromDate: startDate, toDate: endDate, contEventTypeIds: eventTypeArray, contObjectIds: objectArray}}
            });
        }else{
            return $resource(table, {},
                         {'get':{method:'GET', params:{fromDate: startDate, toDate: endDate, contEventTypeIds: eventTypeArray, contObjectIds: objectArray, isNew: isNew}}
            });
        };
    }; 
    
    $scope.pagination = {
        current: 1
    };

    $scope.pageChanged = function(newPage) {       
//console.log("pageChanged");        
        $scope.getResultsPage(newPage);
    };
    //Преобразуем полученные уведомления в формат, который будет отображаться пользователю
    var dataParse = function(arr){
        var oneNotice = {};
        var tmp = arr.map(function(el){
//console.log(el);            
            oneNotice = {};
            oneNotice.id = el.id;
            oneNotice.noticeType = el.contEvent.contEventType.caption;
            oneNotice.isBaseEvent = el.contEvent.contEventType.isBaseEvent;
            oneNotice.noticeMessage = el.contEvent.message;                        
            if (el.contEvent.contEventType.caption.length > $scope.TYPE_CAPTION_LENGTH){
                    oneNotice.noticeTypeCaption= el.contEvent.contEventType.caption.substr(0, $scope.TYPE_CAPTION_LENGTH)+"...";
                }else{
                     oneNotice.noticeTypeCaption= el.contEvent.contEventType.caption;
            };
            if (el.contEvent.message == null){
                oneNotice.noticeCaption = "";
            }else{
                if (el.contEvent.message.length > $scope.TEXT_CAPTION_LENGTH){
                        oneNotice.noticeCaption= el.contEvent.message.substr(0, $scope.TEXT_CAPTION_LENGTH)+"...";
                    }else{
                         oneNotice.noticeCaption= el.contEvent.message;
                };
            };

            for (var i=0; i<$scope.objects.length; i++){                       
                if ($scope.objects[i].id == el.contObjectId ){
                    oneNotice.noticeObjectName = $scope.objects[i].fullName;  
                };   
            };

            oneNotice.noticeDate = $scope.dateFormat(el.contEvent.eventTime);
            oneNotice.contEventLevelColor = el.contEventLevelColor;
            oneNotice.imgpath = $scope.imgPathTmpl+el.contEventLevelColor.toLowerCase()+".png";
            oneNotice.imgclass = el.contEventLevelColor==="GREEN"?"":"nmc-img-critical-indicator";
            oneNotice.isNew = el.isNew;
            switch (el.contEvent.contServiceType)
            {
                case "heat" : oneNotice.noticeZpoint = "Теплоснабжение"; 
                    oneNotice.imgSTPath = "vendor_components/glyphicons_free/glyphicons/png/glyphicons-85-heat.png";
                    break;
                case "hw" : oneNotice.noticeZpoint = "ГВС";
                    oneNotice.imgSTPath = "vendor_components/glyphicons_free/glyphicons/png/glyphicons-93-tint.png";
                    break;
                case "cw" : oneNotice.noticeZpoint = "ХВС"; 
                    oneNotice.imgSTPath = "vendor_components/glyphicons_free/glyphicons/png/glyphicons-22-snowflake.png";
                    break;
                case "gas" : oneNotice.noticeZpoint = "Газ"; 
                    oneNotice.imgSTPath = "vendor_components/glyphicons_free/glyphicons/png/glyphicons-23-fire.png";
                    break;
                case "env" : oneNotice.noticeZpoint = "Климат"; 
                    oneNotice.imgSTPath = "images/es.png";
                    break;
                case "el" : oneNotice.noticeZpoint = "Элка"; 
                    oneNotice.imgSTPath = "images/es.png";
                    break;    
                case null : oneNotice.noticeZpoint = ""; 
                    oneNotice.imgSTPath = "null";
                    break;
                default: oneNotice.noticeZpoint  = ""+el.contServiceType+"";
                    oneNotice.imgSTPath = ""+el.contServiceType+"";
             }
//console.log(oneNotice);            
            return oneNotice;
        });
        return tmp;
    };
    
//    $(window).resize(function() {
//        $scope.TEXT_CAPTION_LENGTH = Math.round($('#div-main-area').width()*8.333/100/6.5)*4-3; //length of message visible part. Koef 4 for class 'col-md-4', for class 'col-md-3' koef = 3 and etc.
//        $scope.TYPE_CAPTION_LENGTH = Math.round($('#div-main-area').width()*8.333/100/6.5)*3-3; //length of type visible part 
//console.log("Re parse");
//console.log($scope.TEXT_CAPTION_LENGTH);  
//console.log($scope.TYPE_CAPTION_LENGTH);         
//        var tmp =dataParse($scope.data.objects);
//        $scope.notices = tmp;
//       
//    });
    
    $scope.getResultsPage = function(pageNumber) {
        $scope.pagination.current = pageNumber;        
//old version        var url =  $scope.crudTableName+"/eventsFilterPaged"+"?"+"page="+(pageNumber-1)+"&"+"size="+$scope.noticesPerPage;        
        var url =  $scope.crudTableName+"/paged"+"?"+"page="+(pageNumber-1)+"&"+"size="+$scope.noticesPerPage;  
//console.log($rootScope.reportStart); 
console.log(loca);        
        if ((angular.isDefined(loca))){
            $scope.startDate = loca.fromDate;
            $scope.endDate = loca.toDate;  
        }else{
            $scope.startDate = $rootScope.reportStart || moment().format('YYYY-MM-DD');
            $scope.endDate = $rootScope.reportEnd || moment().format('YYYY-MM-DD');  
        };
console.log("****************** Запрос *****************");
console.log(url);        
console.log($scope.startDate);
console.log($scope.endDate);        
console.log($scope.selectedObjects); 
console.log($scope.selectedNoticeTypes);  
console.log($scope.isNew);    
console.log("88888888888888888888 the end ***********************");        
        getNotices(url, $scope.startDate, $scope.endDate, $scope.selectedObjects, $scope.selectedNoticeTypes, $scope.isNew).get(function(data){                  
                        var result = [];
                        $scope.data= data;
//console.log($scope.data);             
                        var oneNotice = {};
                        $scope.totalNotices = data.totalElements;
                        var tmp = dataParse(data.objects);
                        $scope.notices = tmp;
//console.log($scope.notices);            
                    });
    };
    
    $scope.dateFormat = function(date){
        return (new Date(date)).toLocaleString();
    };

    // Открыть окно выбора объектов
    $scope.selectObjectsClick = function(){
//console.log($scope.objects);        
        $scope.objectsInWindow = angular.copy($scope.objects);
        //Если флаг состояния объектов = "ложь" (это означает, что либо объекты еще не выбирались либо выбор объектов не был подтвержден - не была нажата кнопка "Применить"), то сбросить флаги у выбранных объектов

//        if (!$scope.states.applyObjects_flag){
//            $scope.objects.forEach(function(el){
//                el.selected = false;
//            });
//        }else{
            //иначе
//            $scope.states.applyObjects_flag = false;// сбрасываем флаг, чтобы отследить нажатие кнопки "Применить" 
//        };
        $('#selectObjectsModal').modal('show');
    };
    
    $scope.selectNoticeTypesClick = function(){
        //create the copy of category states
        $scope.states.tempCriticalTypes_flag = angular.copy($scope.states.criticalTypes_flag);
        $scope.states.tempNoCriticalTypes_flag = angular.copy($scope.states.noCriticalTypes_flag);
        $scope.states.tempUndefinedCriticalTypes_flag = angular.copy($scope.states.undefinedCriticalTypes_flag);
        //Create the copy of types
        $scope.typesInWindow = angular.copy($scope.noticeTypes);
        //аналогично функции $scope.selectObjectsClick
//        if (!$scope.states.applyTypes_flag){
//            $scope.noticeTypes.forEach(function(el){
//                el.selected = false;
//            });
//        }else{
            //иначе
//            $scope.states.applyTypes_flag = false;// сбрасываем флаг, чтобы отследить нажатие кнопки "Применить" 
//        };
        $('#selectNoticeTypesModal').modal('show');
    };
    
    function performObjectsFilter(){
console.log("performObjectsFilter");        
        $scope.objects = $scope.objectsInWindow;
        $scope.selectedObjects_list = "";
        $scope.selectedObjects = [];
        $('#selectObjectsModal').modal('hide');
        $scope.objects.map(function(el){
          if(el.selected){
              $scope.selectedObjects_list+=el.fullName+"; ";
              $scope.selectedObjects.push(el.id);
          }
        });
        if ($scope.selectedObjects.length == 0){
            $scope.selectedObjects_list = "Нет";
        }else{
            $scope.selectedObjects_list = $scope.selectedObjects.length;
        };
    };
      
    $scope.selectObjects = function(){
         performObjectsFilter();
//        $scope.states.applyObjects_flag = true;
        //Объекты были выбраны и их выбор был подтвержден нажатием кнопки "Применить"
        $scope.getResultsPage(1);

    };
    
    function performNoticeTypesFilter(){
        $scope.noticeTypes = $scope.typesInWindow;

        $scope.states.criticalTypes_flag = $scope.states.tempCriticalTypes_flag;
        $scope.states.noCriticalTypes_flag = $scope.states.tempNoCriticalTypes_flag;
        $scope.states.undefinedCriticalTypes_flag = $scope.states.tempUndefinedCriticalTypes_flag;
        
        $scope.selectedNoticeTypes_list = "";
        $scope.selectedNoticeTypes = [];
        $('#selectNoticeTypesModal').modal('hide');
        $scope.noticeTypes.map(function(el){
          if(el.selected){
              $scope.selectedNoticeTypes_list+=el.fullName+"; ";
              $scope.selectedNoticeTypes.push(el.id);
          }
        });
        $scope.selectedNoticeTypes_list = $scope.selectedNoticeTypes.length;
        if ($scope.selectedNoticeTypes.length == 0){
            $scope.selectedNoticeTypes_list = "Нет";
        }else{
            $scope.selectedNoticeTypes_list = $scope.selectedNoticeTypes.length;
        };
    };
    
    $scope.selectNoticeTypes = function(){
        performNoticeTypesFilter();
//        $scope.states.applyTypes_flag = true;
        //Типы были выбраны и их выбор был подтвержден нажатием кнопки "Применить"
        $scope.getResultsPage(1);

    };
      
      $scope.selectedItem = function (item) {
          var curObject = angular.copy(item);
          $scope.currentObject = curObject;
  
      };
    
    $scope.showNoticeDetail = function(object){
        $scope.selectedItem(object);
        $('#showNoticeModal').modal();
    };
    
        // sort the object array by the fullname
//    function sortObjectsByFullName(array){
//        array.sort(function(a, b){
//            if (a.fullName>b.fullName){
//                return 1;
//            };
//            if (a.fullName<b.fullName){
//                return -1;
//            };
//            return 0;
//        }); 
//    };    
    
             //get Objects
    $scope.getObjects = function(){
//        crudGridDataFactory($scope.objectsUrl).query(function(data){
        objectSvc.promise.then(function(response){
            $scope.objects = response.data;
            objectSvc.sortObjectsByFullName($scope.objects);
            $scope.$broadcast('notices:getNoticeTypes');   
console.log("getObjects");            
//            $scope.getResultsPage(1);
        });
    };
    
    $scope.getObjects();
    
    //click the main checkbox - select/ deselect notices
    $scope.performAllNoticesOnPage = function(){
        $scope.notices.forEach(function(element){
            element.selected = $scope.allSelected;
        });
    };
    
//    $scope.$watch('r_e_p_o_r_t_S_t_a_r_t123', function (newDates, oldDates) {
//console.log("watch notice");        
//console.log(newDates); 
//console.log(oldDates);
//        if (angular.isUndefined(oldDates)){
//            return;
//        };
//        if(oldDates === newDates){
//            return;
//        };
//console.log("oldDates !== newDates");        
//        if ((!angular.isDefined($scope.objects))||($scope.objects.length == 0)){
//console.log("if = true");            
//            $scope.getObjects();                              
//        }else{
//            $scope.getResultsPage(1);
//        };
//    }, false);
    
    $scope.getNoticeTypes = function(url){
       $http.get(url)
            .success(function(data){
                $scope.noticeTypes = data;
                $scope.initCtrl();
                $scope.getResultsPage(1);
console.log("$scope.noticeTypes");           
            })
            .error(function(e){
                console.log(e);
            });
    };
    
    $scope.$on('notices:getNoticeTypes',function(){
        $scope.getNoticeTypes($scope.noticeTypesUrl);
//        $scope.getResultsPage(1);
    });
    
    //new/all filter
    $scope.getAllNotices = function(){
        $scope.isNew = null;
        $scope.getResultsPage(1);
    };
    
    $scope.getOnlyNewNotices = function(){
        $scope.isNew = true;
        $scope.getResultsPage(1);
    };
    
    
    //notice type filters
    $scope.noticeFilterCritical = function(notice){
        if (notice.isCriticalEvent===true){
            return notice
        };
    };
    
    $scope.noticeFilterNoCritical = function(notice){
        if (notice.isCriticalEvent===false){
            return notice
        };
    };
    
    $scope.noticeFilterUndefinedCritical = function(notice){
        if (!(notice.hasOwnProperty('isCriticalEvent'))||(notice.isCriticalEvent==null)){
            return notice
        };
    };
    
    //selection notice types
    $scope.selectAllCritical = function(){       
        if ($scope.states.tempCriticalTypes_flag){               
            $scope.typesInWindow.forEach(function(notice){        
                if (notice.isCriticalEvent===true){
                    notice.selected = true;
                };
            });
        }else{                               
            $scope.typesInWindow.forEach(function(notice){        
                if (notice.isCriticalEvent===true){
                    notice.selected = false;
                };
            });
        };      
    };
    
    $scope.selectAllNoCritical = function(){       
        if ($scope.states.tempNoCriticalTypes_flag){               
            $scope.typesInWindow.forEach(function(notice){        
                if ((notice.isCriticalEvent===false)||(!notice.hasOwnProperty('isCriticalEvent'))||(notice.isCriticalEvent==null)){
                    notice.selected = true;
                };
            });
        }else{                               
            $scope.typesInWindow.forEach(function(notice){        
                if ((notice.isCriticalEvent===false)||(!notice.hasOwnProperty('isCriticalEvent'))||(notice.isCriticalEvent==null)){
                    notice.selected = false;
                };
            });
        };      
    };
    
    $scope.selectAllUndefinedCritical = function(){       
        if ($scope.states.tempUndefinedCriticalTypes_flag){               
            $scope.typesInWindow.forEach(function(notice){        
                if ((!notice.hasOwnProperty('isCriticalEvent'))||(notice.isCriticalEvent==null)){
                    notice.selected = true;
                };
            });
        }else{                               
            $scope.typesInWindow.forEach(function(notice){        
                if ((!notice.hasOwnProperty('isCriticalEvent'))||(notice.isCriticalEvent==null)){
                    notice.selected = false;
                };
            });
        };      
    };
    
//control revision / new notices (Просмотренные/новые увеодомления)
//*****************************************************************************************    
    //get notice id array
    function getNoticesIds(notices, noticesIds){       
        if ((!notices.hasOwnProperty('length'))||(notices.length==0)||(typeof noticesIds == 'undefined')){
            return;
        };
        notices.forEach(function(el){
            if (el.selected){             
                noticesIds.push(el.id);
            };
        });       
    };
    //set revision of selected notices
    $scope.revisionNotices = function(flagIsNew){
        var noticesIds = [];
        var url = $scope.crudTableName+"/revision";
        getNoticesIds($scope.notices, noticesIds);   
        if ((typeof noticesIds == 'undefined')||(!noticesIds.hasOwnProperty('length'))|| (noticesIds.length==0)){
            return;
        };

        $http({
            url: url, 
            method: "PUT",
            params: { notificationIds:noticesIds, isNew: flagIsNew },
            data: null
        })
        .then(function(response) {
            $('#confirmActionModal').modal('hide');
            $scope.getResultsPage(1);
            notificationFactory.success();
        })
        .catch(function(e){
            notificationFactory.errorInfo(e.statusText,e);
        });
    };
    //set revision of all notices 
    $scope.revisionAllNotices = function(flagIsNew){
//        var noticesIds = [];
        var url = $scope.crudTableName+"/revision/all";
//        getNoticesIds($scope.notices, noticesIds);   
//        if ((typeof noticesIds == 'undefined')||(!noticesIds.hasOwnProperty('length'))|| (noticesIds.length==0)){
//            return;
//        };
//console.log($scope.isNew);
        $http({
            url: url, 
            method: "PUT",
            params: { fromDate: $scope.startDate,
                     toDate: $scope.endDate,
                     contObjectIds: $scope.selectedObjects,
                     contEventTypeIds: $scope.selectedNoticeTypes,
                     isNew: $scope.isNew,
                     revisionIsNew: flagIsNew },
            data: null
        })
        .then(function(response) {
            $('#confirmActionModal').modal('hide');
            $scope.getResultsPage(1);
            notificationFactory.success();
        })
        .catch(function(e){
            notificationFactory.errorInfo(e.statusText,e);
        });
    };
    
    //mark the notices on the current page as revision
    $scope.revisionNoticesOnPage = function(){
        $scope.notice.forEach(function(el){
            el.selected;
        });
        $scope.revisionNotices(false);
    };
    
    //Confirm the selected action
    $scope.confirmAction = function(){
        if ($scope.confirmationText===$scope.messages.markOnPageAsRevision){
            $scope.revisionNoticesOnPage();
        };
        if ($scope.confirmationText===$scope.messages.markAllAsRevision){
            $scope.revisionAllNotices(false);
        };
    };

    //Clear all filters
    $scope.clearAllFilters = function(){
console.log("Clear all filters.");        
        $scope.clearObjectFilter();
        $scope.clearTypeFilter();
        $scope.isNew = null;
        $scope.getResultsPage(1);
    };
    
    $scope.clearObjectFilter = function(){  
console.log("Clear Object filters.");                
        $scope.objects.forEach(function(el){
            el.selected = false;
        });
        $scope.selectedObjects_list='Нет';
        $scope.selectedObjects=[];
//        $scope.getResultsPage(1);
    };
    
    $scope.clearTypeFilter = function(){
console.log("Clear Type filters.");         
        $scope.states.criticalTypes_flag = false;
        $scope.states.noCriticalTypes_flag = false;
        $scope.states.undefinedCriticalTypes_flag = false;
        $scope.noticeTypes.forEach(function(el){
            el.selected = false;
        });
        $scope.selectedNoticeTypes_list='Нет';
        $scope.selectedNoticeTypes=[];

//        $scope.getResultsPage(1);
    };
    
    
    //chart
    $scope.runChart = function(){
        var data = [];//, series = Math.floor(Math.random() * 6) + 3;

//		for (var i = 0; i < series; i++) {
//			data[i] = {
//				label: "Series" + (i + 1),
//				data: Math.floor(Math.random() * 100) + 1
//			}
//		};
        var noticeTypes = [];
        var noticeCountEveryType = [];
//        get notice types.
        for (var i = 0; i< $scope.notices.length; i++){
            if (noticeTypes.length>0){
                var flag = false;
                for (var j = 0; j<noticeTypes.length; j++){
//console.log("noticeType = "+noticeTypes[j]);
//console.log("$scope.notices =");
//console.log($scope.notices[i]);                    
                    if(noticeTypes[j]===$scope.notices[i].noticeType){
                        noticeCountEveryType[j] +=1;
                        flag =true;
                        continue;
                    };
                };
                if (!flag){
                    noticeTypes.push($scope.notices[i].noticeType);
                    noticeCountEveryType.push(1);
                };
            }else{
                noticeTypes.push($scope.notices[i].noticeType);
                noticeCountEveryType.push(1);
            };
        };
        for (var i = 0; i < noticeTypes.length; i++) {
			data[i] = {
				label: noticeTypes[i],
				data: noticeCountEveryType[i]
			}
		};
        
        // выводим график
        $("#noticeChart-area").width(300);
        $("#noticeChart-area").height(300);
        $("#chartModal.modal-dialog").width(700);
        $('#chartModal').modal();
        
        $.plot('#noticeChart-area', data,{
            series: {
                pie: {
                    show: true,
                    label :{
                        show : false,
                        formatter: labelFormatter
                    }
                }
            },
            legend: {
                show: true,
                labelFormatter: labelFormatter,
                position: "ne",
                margin: [-400, 0]
            }
        });
        
    };
    
    function labelFormatter(label, series) {
		return "<div style='font-size:8pt; text-align:center; padding:2px; color:black;'>" + label + " (" + Math.round(series.percent) + "%)</div>";
	}
});