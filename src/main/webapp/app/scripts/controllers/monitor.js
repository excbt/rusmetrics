angular.module('portalNMC')
  .controller('MonitorCtrl', ['$rootScope', '$http', '$scope', '$compile', '$interval', '$cookies', '$location', 'monitorSvc','mainSvc',function($rootScope, $http, $scope, $compile, $interval, $cookies, $location, monitorSvc, mainSvc){
         
console.log("Monitor Controller.");      
    //object url
    var noticesUrl = "#/notices/list/";
    var notificationsUrl = "../api/subscr/contEvent/notifications"; 
    var objectUrl = notificationsUrl+"/contObject";//"resource/objects.json";  
    var monitorUrl = notificationsUrl+"/monitorColor";
    //objects array
    $scope.objects = monitorSvc.getAllMonitorObjects();//[];
//console.log($scope.objects);      
//console.log("Monitor ctrl. Objects are got."); 
//var time = new Date();
//console.log(time);        
//console.log("================== $scope.objects================");            
//console.log($scope.objects);      
//console.log("====================== end $scope.objects=================");            

    //default date interval settings
    $rootScope.monitorStart = $location.search().fromDate || monitorSvc.getMonitorSettings().fromDate || moment().subtract(6, 'days').startOf('day').format('YYYY-MM-DD');
    $rootScope.monitorEnd =  $location.search().toDate || monitorSvc.getMonitorSettings().toDate || moment().endOf('day').format('YYYY-MM-DD');    
    
    //monitor settings
    $scope.monitorSettings = {};
    $scope.monitorSettings.refreshPeriod = monitorSvc.getMonitorSettings().refreshPeriod;//"180";
    $scope.monitorSettings.createRoundDiagram = false;
    $scope.monitorSettings.loadingFlag = true;//monitorSvc.monitorSvcSettings.loadingFlag;
//console.log($scope.monitorSettings.loadingFlag);      
    //flag: false - get all objectcs, true - get only  red, orange and yellow objects.
    $scope.monitorSettings.noGreenObjectsFlag = false;
    
    $scope.monitorSettings.objectsPerScroll = 34;//the pie of the object array, which add to the page on window scrolling
    $scope.monitorSettings.objectsOnPage = $scope.monitorSettings.objectsPerScroll;//50;//current the count of objects, which view on the page
    $scope.monitorSettings.currentScrollYPos = window.pageYOffset || document.documentElement.scrollTop; 
    $scope.monitorSettings.objectTopOnPage =0;
    $scope.monitorSettings.objectBottomOnPage =34;
      
    $scope.monitorSettings.isCtrlEnd = false;
      
    $scope.monitorSettings.ctxId = "monitor_page";
      
    $scope.monitorSettings.dateRangeSettings = mainSvc.getDateRangeOptions("monitor-ru");
    $scope.monitorSettings.monitorDates = {
        startDate :  $rootScope.monitorStart,
        endDate :  $rootScope.monitorEnd
    }; 
//console.log($scope.monitorSettings.monitorDates);      

//      tempArr =  $scope.objects.slice(0, $scope.objectCtrlSettings.objectsPerScroll);
//                    $scope.objectsOnPage = tempArr;
    $scope.objectsOnPage = ($scope.objects.length===0)?[]:$scope.objects.slice(0, $scope.monitorSettings.objectsPerScroll);
                    $scope.objectsOnPage.forEach(function(element){
//                        if ((element.statusColor === "RED") ||(element.statusColor === "ORANGE")){
                            monitorSvc.getMonitorEventsByObject(element);
//                        }else if((element.statusColor === "YELLOW")){
//                            element.monitorEvents = "На объекте нет нештатных ситуаций";
//                            $rootScope.$broadcast('monitorObjects:getObjectEvents',{"obj":element});
//                        };
                    });
      
    
    //monitor state
    $scope.monitorState = {};
    $scope.getMonitorState = function(){
        var url = monitorUrl+"?fromDate="+$rootScope.monitorStart+"&toDate="+$rootScope.monitorEnd;
        $http.get(url)
            .success(function(data){
                $scope.monitorState = data;
                var monitorTab = document.getElementById('monitorTab');
                monitorTab.style.backgroundColor = $scope.monitorState.statusColor.toLowerCase();
                if ($scope.monitorState.statusColor==="RED" || $scope.monitorState.statusColor=="ORANGE"){
                    monitorTab.style.color = "#eee";
                };
                monitorTab.title = $scope.monitorState.colorDescription;
            })
            .error(function(e){
                console.log(e);
            });
    };
//    $scope.getMonitorState();
    
    $scope.eventColumns = [
             {"name":"typeCategory", "header" : " ", "class":""},
            {"name":"typeEventCount", "header" : " ", "class":""},
            {"name":"typeName", "header" : "Типы уведомлений", "class":"col-md-10"}
            ];
    
    function findObjectById(objId){
        var obj = null;
        $scope.objects.some(function(element){
            if (element.contObject.id === objId){
                obj = element;
                return true;
            }
        });        
        return obj;
    };
    
    //get event types by object
    $scope.getEventTypesByObject = function(objId, isChart){
        var obj = findObjectById(objId);    
        //if cur object = null => exit function
        if (obj == null){
            return;
        };
      
        var url = objectUrl+"/"+obj.contObject.id+"/eventTypes/statusCollapse"+"?fromDate="+$rootScope.monitorStart+"&toDate="+$rootScope.monitorEnd;
//console.log(url);          
        $http.get(url)
            .success(function(data){
console.log(data);            
            //if data is not array - exit
                if (!data.hasOwnProperty('length')||(data.length == 0)){
                    return;
                };
                //temp array
                var tmpTypes = [];
                //make the new array of the types wich formatted to display
                data.forEach(function(element){
                    var tmpType = {};
                    tmpType.id = element.contEventType.id;
                    tmpType.isBaseEvent = element.contEventType.isBaseEvent;
                    tmpType.typeCategory = element.statusColor.toLowerCase();
                    tmpType.typeEventCount = element.totalCount;
                    tmpType.typeName = element.contEventType.caption;
                    tmpTypes.push(tmpType);
                });
                tmpTypes.sort(function(a, b){
                    if (a.typeEventCount > b.typeEventCount){
                        return -1;
                    };
                    if (a.typeEventCount < b.typeEventCount){
                        return 1;
                    };
                    return 0;
                });
                obj.eventTypes = tmpTypes;
                //If need diagram - don't create child table
                if (isChart){
                    $scope.runChart(obj.contObject.id);
                }else{
                    makeEventTypesByObjectTable(obj);
                };
            })
            .error(function(e){
                console.log(e);
            });        
    };
    
    $scope.toggleShowGroupDetails = function(objId){//switch option: current goup details   
        var curObject = null;
        $scope.objects.some(function(element){
            if (element.contObject.id === objId){
                curObject = element;
                return true;
            }
        });        
        //if cur object = null => exit function
        if (curObject == null){
            return;
        };
        //else
        
        var eventTable = document.getElementById("eventTable"+curObject.contObject.id);
        if ((curObject.showGroupDetails==true) && (eventTable==null)){
            curObject.showGroupDetails =true;
        }else{
            curObject.showGroupDetails =!curObject.showGroupDetails;
        };                     
        //if curObject.showGroupDetails = true => get zpoints data and make zpoint table
//console.log(curObject.showGroupDetails);        
        if (curObject.showGroupDetails === true){
            $scope.getEventTypesByObject(curObject.contObject.id, false);
            
            var btnDetail = document.getElementById("btnDetail"+curObject.contObject.id);
            btnDetail.classList.remove("glyphicon-chevron-right");
            btnDetail.classList.add("glyphicon-chevron-down");
        }//else if curObject.showGroupDetails = false => hide child zpoint table
        else{
            var trObj = document.getElementById("obj"+curObject.contObject.id);
            var trObjEvents = trObj.getElementsByClassName("nmc-tr-object-events")[0];//.getElementById("trObjZp");
//            var trObjEvents = document.getElementById("trObjEvents"+curObject.contObject.id);
            trObjEvents.innerHTML = "";
            var btnDetail = document.getElementById("btnDetail"+curObject.contObject.id);
            btnDetail.classList.remove("glyphicon-chevron-down");
            btnDetail.classList.add("glyphicon-chevron-right");
        };
    };
    
      //<a href> right click performer
    $scope.getNoticesByObjectOnRightClick = function(objId){
        $scope.setNoticeFilterByObject(objId);
        window.open(noticesUrl);
    };
      
    $scope.getNoticesByObjectAndRevisionOnRightClick = function(objId){
        $scope.setNoticeFilterByObjectAndRevision(objId);
        window.open(noticesUrl);
    };

    //Рисуем таблицу с объектами
    function makeObjectTable121212(objectArray, isNewFlag){
        var objTable = document.getElementById('objectMonitorTable').getElementsByTagName('tbody')[0];       
//        var temptableHTML = "";
        var tableHTML = "";
        if (!isNewFlag){
            tableHTML = objTable.innerHTML;
        };
//        var tmpArray = $scope.objects;
//console.log("Monitor. Make objects table."); 
//console.log(objTable);        
//console.log($scope.objects);                
        objectArray.forEach(function(element, index){
            var globalElementIndex = $scope.monitorSettings.objectBottomOnPage-objectArray.length+index;
            var trClass= globalElementIndex%2>0?"":"nmc-tr-odd"; //Подкрашиваем разным цветом четные / нечетные строки
            var imgSize = 16; //размер иконки состояния объекта
            if(element.statusColor.toLowerCase()=="green"){//если объет "зеленый", то размер уменьшаем до 1пх, чтобы ничего не выводилось 
                imgSize = 1;
            };
//            temptableHTML += "<tr class=\""+trClass+"\" id=\"obj"+element.contObject.id+"\">";
//            temptableHTML +="<td class=\"nmc-td-for-buttons\"> <i id=\"btnDetail"+element.contObject.id+"\" class=\"btn btn-xs noMargin glyphicon glyphicon-chevron-right nmc-button-in-table\" ng-click=\"toggleShowGroupDetails("+element.contObject.id+")\"></i>";
//            temptableHTML += "<img id=\"imgObj"+element.contObject.id+"\" title=\"\" height=\""+imgSize+"\" width=\""+imgSize+"\" src=\""+"images/object-state-"+element.statusColor.toLowerCase()+".png"+"\" />";
//            temptableHTML+= "</td>";
//            temptableHTML += "<td class=\"col-md-1\"><a title=\"Всего уведомлений\" href=\""+noticesUrl+"\" ng-click=\"setNoticeFilterByObject("+element.contObject.id+")\">"+element.eventsCount+" / "+element.eventsTypesCount+"</a> (<a title=\"Новые уведомления\" href=\""+noticesUrl+"\" ng-click=\"setNoticeFilterByObjectAndRevision("+element.contObject.id+")\">"+element.newEventsCount+"</a>)";
//            
//            temptableHTML += "</td>";
//            temptableHTML +="<td>hi</td><td>amigo</td></tr>";
//            temptableHTML+="<tr><td><table></table></td></tr>";
            
            tableHTML += "<tr class=\""+trClass+"\" id=\"obj"+element.contObject.id+"\">";
            tableHTML += "<td>";
            tableHTML += "<table>";
            tableHTML += "<tr>";
            tableHTML +="<td class=\"nmc-td-for-buttons\"> <i title=\"Показать/Скрыть список типов событий\" id=\"btnDetail"+element.contObject.id+"\" class=\"btn btn-xs noMargin glyphicon glyphicon-chevron-right nmc-button-in-table\" ng-click=\"toggleShowGroupDetails("+element.contObject.id+")\"></i>";
            tableHTML += "<img id=\"imgObj"+element.contObject.id+"\" title=\"\" height=\""+imgSize+"\" width=\""+imgSize+"\" src=\""+"images/object-state-"+element.statusColor.toLowerCase()+".png"+"\" />";
//            ng-mouseover=\"getMonitorEventsByObject("+element.contObject.id+")\"
//            if (element.statusColor.toLowerCase()!="green"){
//                tableHTML +="<i title=\"Узнать причину оценки\" class=\"btn btn-xs glyphicon glyphicon-bookmark\" ng-click=\"getNoticesByObject("+element.contObject.id+")\" data-target=\"#showNoticesModal\" data-toggle=\"modal\"></i>";
//            };
            tableHTML+= "</td>";
//        "?objectMonitorId="+objId+"&monitorFlag=true&fromDate="+$rootScope.monitorStart+"&toDate="+$rootScope.monitorEnd;            
            tableHTML += "<td class=\"col-md-1\"><a title=\"Всего уведомлений\" href=\""+noticesUrl+"?objectMonitorId="+element.contObject.id+"&monitorFlag=true&fromDate="+$rootScope.monitorStart+"&toDate="+$rootScope.monitorEnd+"\" ng-click=\"setNoticeFilterByObject("+element.contObject.id+")\" ng-right-click=\"getNoticesByObjectOnRightClick("+element.contObject.id+")\">"+element.eventsCount+" / "+element.eventsTypesCount+"</a> (<a title=\"Новые уведомления\" href=\""+noticesUrl+"\" ng-click=\"setNoticeFilterByObjectAndRevision("+element.contObject.id+")\" ng-right-click=\"getNoticesByObjectAndRevisionOnRightClick("+element.contObject.id+")\">"+element.newEventsCount+"</a>)";
            
            tableHTML += "</td>";
            tableHTML += "<td class=\"nmc-td-for-buttons\"><i title=\"Показать диаграмму уведомлений\" class=\"btn btn-xs\" ng-click=\"getEventTypesByObject("+element.contObject.id+", true)\"><img height=\"16\" width=\"16\" src='images/roundDiagram4.png'/></i></td>";
            tableHTML += "<td class=\"col-md-3\" ng-click=\"toggleShowGroupDetails("+element.contObject.id+")\">"+element.contObject.fullName;
            if ($scope.isSystemuser()){
                tableHTML+= " <span>(id = "+element.contObject.id+")</span>";
            };
            tableHTML+="</td>";
            tableHTML += "<td class=\"col-md-8\"></td></tr>";
//            tableHTML += "</tr>";
            tableHTML += "</table>";
            tableHTML += "</td>";
            tableHTML +="</tr>";
            tableHTML +="<tr id=\"trObjEvents"+element.contObject.id+"\">";
            tableHTML += "</tr>";
        });
//console.log(tableHTML);
        
//console.log(temptableHTML); 
        objTable.innerHTML = tableHTML;
//        objTable.innerHTML = tableHTML;
        $compile(objTable)($scope);
    };
    
    //Формируем таблицу с событиями объекта
    function makeEventTypesByObjectTable(obj){ 
        var trObj = document.getElementById("obj"+obj.contObject.id);
//console.log(obj);        
        var trObjEvents = trObj.getElementsByClassName("nmc-tr-object-events")[0];
//        var trObjEvents = document.getElementById("trObjEvents"+obj.contObject.id);  
        if (angular.isUndefined(trObjEvents)){
            return;
        };
        var trHTML = "";
        trHTML = "<td colspan='5' style=\"padding-top: 2px !important;\"><table id=\"eventTable"+obj.contObject.id+"\" class=\"crud-grid table table-lighter table-bordered table-condensed table-hover nmc-child-table\">";
        trHTML+="<thead>";
        trHTML+="<tr class=\"nmc-child-table-header\">";
        trHTML+="<th class=\"nmc-td-for-buttons\">";
        trHTML+="</th>";
        $scope.eventColumns.forEach(function(element){
            trHTML+="<th>";
            trHTML+=""+(element.header || element.name)+"";
            trHTML+="</th>";
        });
        trHTML+="</tr>";
        trHTML+="</thead>";
        
        if (angular.isUndefined(obj)||!obj.hasOwnProperty('eventTypes')||(obj.eventTypes.length==0)){            
            return;
        };      
//        "?objectMonitorId="+objId+"&monitorFlag=true&fromDate="+$rootScope.monitorStart+"&toDate="+$rootScope.monitorEnd;
        obj.eventTypes.forEach(function(event){
            trHTML +="<tr id=\"trEvent"+event.id+"\" >";
            trHTML +="<td class=\"nmc-td-for-buttons\">"+
                    "<a href=\""+noticesUrl+"?objectMonitorId="+obj.contObject.id+"&monitorFlag=true&fromDate="+$rootScope.monitorStart+"&toDate="+$rootScope.monitorEnd+"&typeId="+event.id+"\" ng-mousedown=\"setNoticeFilterByObjectAndType("+obj.contObject.id+","+event.id+")\"> <i class=\"btn btn-xs glyphicon glyphicon-list nmc-button-in-table\""+
//                        "ng-click=\"getNoticesByObjectAndType("+obj.contObject.id+","+event.id+")\""+
                        "title=\"Посмотреть уведомления\">"+
                    "</i></a>"+
                "</td>";
            $scope.eventColumns.forEach(function(column){
                switch (column.name){
                    case "typeName": trHTML += "<td class=\"col-md-11\" ng-class=\"{'nmc-positive-notice':"+(!event.isBaseEvent)+"}\">"+event[column.name];
                        if ($scope.isSystemuser()){
                            trHTML +="<span>(id = "+event.id+")</span>";
                        };
                        trHTML+="</td>"; 
                        break;
                    case "typeCategory" : 
                        var size = 16;
                        var title = "";
                        if (event[column.name]=="green"){
                            size = 1;
                        };
                        switch (event[column.name]){
                            case "red": title = "Критическая ситуация"; break;
                            case "orange": title = "Некритическая ситуация"; break;
                            case "yellow": title = "Уведомление"; break;
                                
                        };
                        trHTML +="<td><img title=\""+title+"\" height=\""+size+"\" width=\""+size+"\" src=\""+"images/object-state-"+event[column.name]+".png"+"\"/></td>"; break;   
                    default : trHTML += "<td ng-class=\"{'nmc-positive-notice':"+(!event.isBaseEvent)+"}\"> "+event[column.name]+"</td>"; break;
                };
            });
            trHTML +="</tr>";
        });    
        trHTML += "</table></td>";   
        trObjEvents.innerHTML = trHTML;
        $compile(trObjEvents)($scope);
    };
    
    //Set filters for notice window
    $scope.setNoticeFilterByObject = function(objId){
//        $scope.paramsString = "?objectMonitorId="+objId+"&monitorFlag=true&fromDate="+$rootScope.monitorStart+"&toDate="+$rootScope.monitorEnd;
        $rootScope.monitor = {};
//        $cookies.monitorFlag = true;
//        $cookies.objectMonitorId = objId;
//        $cookies.isNew = null;
//        $cookies.typeIds = null;
//        $cookies.fromDate = $rootScope.monitorStart;
//        $cookies.toDate = $rootScope.monitorEnd;
        monitorSvc.setMonitorSettings({objectMonitorId:objId});
        $rootScope.reportStart = $rootScope.monitorStart;
        $rootScope.reportEnd = $rootScope.monitorEnd;
//console.log($cookies);        
    };
    
    $scope.setNoticeFilterByObjectAndRevision = function(objId){
        $scope.setNoticeFilterByObject(objId);        
        $cookies.isNew = true;        
//        $scope.paramsString+="&isNew=true";
    };
    
    $scope.setNoticeFilterByObjectAndType = function(objId, typeId){
        $scope.setNoticeFilterByObject(objId);
        $cookies.typeIds = [typeId];
//        $scope.paramsString+="&typeIds="+typeId;
    };
    
    $scope.getNoticesByObjectAndType = function(objId, typeId){
//console.log("getNoticesByObjectAndType");        
        $scope.setNoticeFilterByObjectAndType(objId, typeId);
//        window.location.assign(noticesUrl);
        window.open(noticesUrl);
    };
    
    
    $scope.isSystemuser = function(){
        $scope.userInfo = $rootScope.userInfo;
//console.log($rootScope.userInfo);        
        return $scope.userInfo._system;
    };
    //call get objects function
//    $scope.getObjects(objectUrl);
    
    $scope.refreshData = function(){
        $scope.monitorSettings.loadingFlag = true;
//console.log("request");                
        $rootScope.$broadcast('monitor:updateObjectsRequest');
//        $scope.getObjects(objectUrl);
    };
    
    $scope.getNoGreenObjects= function(){
        $scope.monitorSettings.loadingFlag = true;
        $scope.monitorSettings.noGreenObjectsFlag = true;
        monitorSvc.setMonitorSettings({noGreenObjectsFlag: true});
//        monitorSvc.monitorSvcSettings.noGreenObjectsFlag = true;
//console.log("request");                
        $rootScope.$broadcast('monitor:updateObjectsRequest');
//        $scope.getObjects(objectUrl);
    };
    
    //Watching for the change period  
    $scope.$watch('monitorSettings.monitorDates', function (newDates, oldDates) {
//console.log("monitorDates watch");  
//console.log(newDates);        
//console.log(oldDates);   
        if (oldDates===newDates){
            return;
        };
        $scope.monitorSettings.loadingFlag = true;
        
        $rootScope.monitorStart = moment(newDates.startDate).format('YYYY-MM-DD');
        $rootScope.monitorEnd = moment(newDates.endDate).format('YYYY-MM-DD'); 
        monitorSvc.setMonitorSettings({
            fromDate:$rootScope.monitorStart,
            toDate:$rootScope.monitorEnd
        });
//console.log($rootScope.monitorStart);        
//console.log($rootScope.monitorEnd);         
        $scope.monitorSettings.dateRangeSettings.startDate = moment($rootScope.monitorStart).startOf('day');
        $scope.monitorSettings.dateRangeSettings.endDate = moment($rootScope.monitorEnd).endOf('day');
        
        $location.search("fromDate",$rootScope.monitorStart);
        $location.search("toDate",$rootScope.monitorEnd);
        //perform url address
//        var $location
//        monitorSvc.monitorSvcSettings.fromDate = $rootScope.monitorStart;
//        monitorSvc.monitorSvcSettings.toDate = $rootScope.monitorEnd;
//console.log("request");        
        $rootScope.$broadcast('monitor:updateObjectsRequest');
//        $rootScope.$broadcast('monitor:periodChanged');
//        $scope.getObjects(objectUrl);                              
    }, false);
      
      
    $scope.$on('monitorObjects:getObjectEvents',function(event, args){
//console.log('On monitorObjects:getObjectEvents');        
//console.log(event);
//console.log(args);   
        var obj = args.obj;
        var imgObj = "#imgObj"+obj.contObject.id; 
//console.log(obj); 
//console.log($(imgObj));        
        $(imgObj).qtip({
            content:{
                text: obj.monitorEvents
            },
            style:{
                classes: 'qtip-bootstrap qtip-nmc-monitor-tooltip'
            }
        }); 
    });
      
//    $(window).bind("onscroll",function(){
//        console.log("on scroll");
//        
//    });
      
//    window.onscroll = 
        var fnc = function(){
//        console.log("Window. On scroll");
        if(angular.isUndefined($scope.objects) || ($scope.objects.length===0)){
            return;
        };
        var scrollTop = window.pageYOffset || document.documentElement.scrollTop;
//console.log("scrollTop = "+scrollTop);
//        $scope.monitorSettings.currentScrollYPos = window.pageYOffset || document.documentElement.scrollTop; 
//        $scope.monitorSettings.objectTopOnPage =0;
//        $scope.monitorSettings.objectBottomOnPage =49;
        
        var deltaScroll = scrollTop - $scope.monitorSettings.currentScrollYPos;
//console.log(deltaScroll);        
        if (deltaScroll >=22){//down to 1 row
//console.log("deltaScroll +");            
            
            var rowCount = Math.round(deltaScroll/22);
            $scope.monitorSettings.objectTopOnPage+=rowCount;
            $scope.monitorSettings.objectBottomOnPage+=rowCount;
            //draw new table
            var tempArr = $scope.objects.slice($scope.monitorSettings.objectBottomOnPage-rowCount, $scope.monitorSettings.objectBottomOnPage);
//            makeObjectTable(tempArr, false);
            
//            window.

            $scope.monitorSettings.currentScrollYPos = scrollTop;
        };
        
        for(var i = 0; i<=$scope.monitorSettings.objectBottomOnPage; i++){
            var obj = $scope.objects[i];
            var imgObj = "#imgObj"+obj.contObject.id;          
//            $(imgObj).qtip({
//                content:{
//                    text: obj.monitorEvents
//                },
//                style:{
//                    classes: 'qtip-bootstrap qtip-nmc-monitor-tooltip'
//                }
//            }); 
        };
        //up to some row
//        if (deltaScroll <=-22){
//console.log("deltaScroll -"); 
//            var rowCount = Math.round(Math.abs(deltaScroll/22));
//            $scope.monitorSettings.objectTopOnPage-=rowCount;
//            $scope.monitorSettings.objectBottomOnPage-=rowCount;            
//            //draw new table
//            var tempArr = $scope.objects.slice($scope.monitorSettings.objectTopOnPage, $scope.monitorSettings.objectBottomOnPage);
//            makeObjectTable(tempArr, true);
//
//            $scope.monitorSettings.currentScrollYPos = scrollTop;
//        };

//        if ($scope.monitorSettings.objectsOnPage>=$scope.objects.length){
//            return;
//        };
//        var startPos = $scope.monitorSettings.objectsOnPage;
//        var endPos = $scope.monitorSettings.objectsOnPage + $scope.monitorSettings.objectsPerScroll;
//        var tempArr = $scope.objects.slice(startPos, endPos);
//        makeObjectTable(tempArr, false);
//        $scope.monitorSettings.objectsOnPage = endPos;
    };
    
    
//The control of the period monitor refresh(Управление перодическим обновлением монитора)
//**************************************************************************  
    $scope.$on('monitorObjects:updated',function(){
        $scope.objects = monitorSvc.getAllMonitorObjects();
//console.log($scope.objects);        
//console.log("Monitor ctrl. Objects are got."); 
//var time = new Date();
//console.log(time);  
        
        //reset position
        window.scrollTo(0,0);
        $scope.monitorSettings.currentScrollYPos = window.pageYOffset || document.documentElement.scrollTop; 
        $scope.monitorSettings.objectTopOnPage =0;
        $scope.monitorSettings.objectBottomOnPage =34;
        var tempArr = $scope.objects.slice(0,$scope.monitorSettings.objectsPerScroll);
//        makeObjectTable(tempArr, true);
        $scope.monitorSettings.loadingFlag = monitorSvc.getMonitorSettings().loadingFlag;//false;
        $scope.monitorSettings.noGreenObjectsFlag = monitorSvc.getMonitorSettings().noGreenObjectsFlag;
        $scope.monitorSettings.objectsOnPage=$scope.monitorSettings.objectsPerScroll;
        $scope.objectsOnPage = tempArr;
//$scope.objectsOnPage = $scope.objectsOnPage.splice(0,4);
//console.log($scope.objectsOnPage);        
        $scope.objectsOnPage.forEach(function(element){
//            if ((element.statusColor === "RED") ||(element.statusColor === "ORANGE")){
                monitorSvc.getMonitorEventsByObject(element);
//            }else if((element.statusColor === "YELLOW")){
//console.log(element);                
//                element.monitorEvents = "На объекте нет нештатных ситуаций";
//                $rootScope.$broadcast('monitorObjects:getObjectEvents',{"obj":element});
//            };
        });
        
        //refresh qtip
        var qtips = document.getElementsByClassName("qtip");
        for (var index = 0; index<qtips.length; index++){
            qtips[index].style.display = "none";
        };
    });
    
//    var interval;
//    
//    function stopRefreshing(){
//        if (angular.isDefined(interval)){
//            $interval.cancel(interval);
//            interval = undefined;
//        };
//    };
    
//    $scope.$on('$destroy', function() {
//        alert("Ушли со страницы?");
//        stopRefreshing();
//    });
    $scope.$on('$destroy', function() {
    //        alert("Way out");
    //        $cookies.objectMonitorId = null;
    //console.log("Objects page destroy");        
    //                    window.onscroll = undefined;
        window.onkeydown = undefined;
    }); 

    window.onkeydown = function(e){
    //console.log("Window key down");                                            
        if ((e.ctrlKey && e.keyCode == 35) && ($scope.monitorSettings.objectsOnPage<$scope.objects.length)){
//            console.log("Ctrl + End");
//                $scope.loading =  true;    
//            console.log($scope.loading);                        
            //                        $scope.$apply();
                var tempArr =  $scope.objects.slice($scope.monitorSettings.objectsOnPage,$scope.objects.length);
                tempArr.forEach(function(element){
//                        if ((element.statusColor === "RED") ||(element.statusColor === "ORANGE")){
                            monitorSvc.getMonitorEventsByObject(element);
//                        }else if((element.statusColor === "YELLOW")){
//console.log(element);                
//                            element.monitorEvents = "На объекте нет нештатных ситуаций";
//                            $rootScope.$broadcast('monitorObjects:getObjectEvents',{"obj":element});
//                        };
                    
                });
                Array.prototype.push.apply($scope.objectsOnPage, tempArr);
                $scope.monitorSettings.objectsOnPage+=$scope.objects.length;

                $scope.monitorSettings.isCtrlEnd = true;

            //                        $scope.objectsOnPage = $scope.objects;
            };
    };
                
    $scope.onTableLoad = function(){
//                    var time = new Date();
//console.log("On table load");                                        
//console.log(time.toLocaleString());                    

        if ($scope.monitorSettings.isCtrlEnd === true){                    
            var pageHeight = (document.body.scrollHeight>document.body.offsetHeight)?document.body.scrollHeight:document.body.offsetHeight;
            window.scrollTo(0, Math.round(pageHeight));
            $scope.monitorSettings.isCtrlEnd = false;
//            $scope.loading =  false;
        };
    };
    
    //watch for the change of the refresh period
    $scope.$watch('monitorSettings.refreshPeriod', function (newPeriod, oldPeriod) {
//console.log("Refresh period watch");        
//console.log(newPeriod);
//console.log(oldPeriod);  
        if (newPeriod===oldPeriod){
            return;
        };
        monitorSvc.setMonitorSettings({refreshPeriod:$scope.monitorSettings.refreshPeriod});
//        monitorSvc.monitorSvcSettings.refreshPeriod = $scope.monitorSettings.refreshPeriod;
        $rootScope.$broadcast('monitor:periodChanged');
//        $rootScope.$broadcast('monitor:updateObjectsRequest');
//console.log("monitorSettings.refreshPeriod watch");
//console.log("new period = "+newPeriod);        
        //cancel previous interval
//        stopRefreshing();
        //set new interval
//        interval = $interval(function(){
//            var time = (new Date()).toLocaleString();
//console.log("new interval");            
//console.log(time);
//console.log(Number($scope.monitorSettings.refreshPeriod)); 
//            $scope.monitorSettings.loadingFlag = true;
//            $scope.getObjects(objectUrl);
//        },Number($scope.monitorSettings.refreshPeriod)*1000);
        
    }, false);
    
    //Вызвываем с заданным периодом обновление монитора
//    interval = $interval(function(){
//        var time = (new Date()).toLocaleString();
//console.log(time);
//console.log(Number($scope.monitorSettings.refreshPeriod));
//        $scope.monitorSettings.loadingFlag = true;
//        $scope.getObjects(objectUrl);
//    },Number($scope.monitorSettings.refreshPeriod)*1000);
      
          //check object array
    if ($scope.objects.length!=0)  {
        //if array is not empty -> make table
//console.log("$scope.objects.length!=0")    
        var tempArr = $scope.objects.slice(0,$scope.monitorSettings.objectsPerScroll);
//        makeObjectTable(tempArr, true);
        $scope.monitorSettings.loadingFlag = monitorSvc.getMonitorSettings().loadingFlag;//false;
//console.log($cookies.objectMonitorId);          
        if (angular.isDefined(monitorSvc.getMonitorSettings().objectMonitorId) && monitorSvc.getMonitorSettings().objectMonitorId!=="null"){
//console.log("angular.isDefined($cookies.objectMonitorId) && $cookies.objectMonitorId!==null" + $cookies.objectMonitorId)             
            $scope.getEventTypesByObject(Number(monitorSvc.getMonitorSettings().objectMonitorId), false);
//            $cookies.objectMonitorId = null;
            monitorSvc.setMonitorSettings({objectMonitorId:null});
//console.log($cookies.objectMonitorId);            
        };
//        $scope.objects.forEach(function(obj){
//            var imgObj = "#imgObj"+obj.contObject.id;          
//            $(imgObj).qtip({
//                content:{
//                    text: obj.monitorEvents
//                },
//                style:{
//                    classes: 'qtip-bootstrap qtip-nmc-monitor-tooltip'
//                }
//            });
//        });
        
    }else if($scope.monitorSettings.loadingFlag===false){//else -> send request
//console.log("$scope.objects.length!=0");        
        $rootScope.$broadcast('monitor:updateObjectsRequest');
    };
      
    $scope.$on('$destroy', function() {
//        alert("Way out");
//        $cookies.objectMonitorId = null;
console.log("Monitor destroy");        
        window.onscroll = undefined;
    }); 
    
    $scope.setMonitorEventsForObject = function(obj){
console.log(obj);        
        if (angular.isUndefined(obj.monitorEvents)){
            return;
        };
        var imgObj = "#imgObj"+obj.contObject.id; 
//            imgObj = "#"+imgObj;
        $(imgObj).ready(function(){
console.log(imgObj+" ready!");
            $(imgObj).qtip({
                content:{
                    text: obj.monitorEvents
                },
                style:{
                    classes: 'qtip-bootstrap qtip-nmc-monitor-tooltip'
                }
            });
        });
console.log($(imgObj));            
//            $(imgObj).qtip({
//                content:{
//                    text: obj.monitorEvents
//                },
//                style:{
//                    classes: 'qtip-bootstrap qtip-nmc-monitor-tooltip'
//                }
//            });
    };
      
    $scope.addMoreObjectsForMonitor = function(){
//console.log("addMoreObjectsForMonitor. Run");
        if (($scope.objects.length<=0)){
            return;
        };
        var endIndex = $scope.monitorSettings.objectsOnPage+$scope.monitorSettings.objectsPerScroll;
//console.log($scope.objects.length);                    
        if((endIndex >= $scope.objects.length)){
            endIndex = $scope.objects.length;
        };
//console.log($scope.objects);        
        var tempArr =  $scope.objects.slice($scope.monitorSettings.objectsOnPage,endIndex);
//console.log(tempArr);        
        //add tooltip for next the part of the objects
//console.log($scope.objectCtrlSettings.objectsOnPage);                    
//console.log(endIndex);                    
//console.log(tempArr);                    
        Array.prototype.push.apply($scope.objectsOnPage, tempArr);
                    tempArr.forEach(function(element){
//                        if ((element.statusColor === "RED") ||(element.statusColor === "ORANGE")){
                            monitorSvc.getMonitorEventsByObject(element);
//                        }else if((element.statusColor === "YELLOW")){
//console.log(element);                
//                            element.monitorEvents = "На объекте нет нештатных ситуаций";
//                            $rootScope.$broadcast('monitorObjects:getObjectEvents',{"obj":element});
//                        };
                    });
        if(endIndex >= ($scope.objects.length)){
            $scope.monitorSettings.objectsOnPage = $scope.objects.length;
        }else{
            $scope.monitorSettings.objectsOnPage+=$scope.monitorSettings.objectsPerScroll;
        };
    };
      
    $("#divWithMonitorTable").scroll(function(){

        $scope.addMoreObjectsForMonitor();
        $scope.$apply();
    });
         
    //control visibles
    var setVisibles = function(ctxId){
        var ctxFlag = false;
        var tmp = mainSvc.getContextIds();
        tmp.forEach(function(element){
            if(element.permissionTagId.localeCompare(ctxId)==0){
                ctxFlag = true;
            };
            var elDOM = document.getElementById(element.permissionTagId);//.style.display = "block";
            if (angular.isUndefined(elDOM)||(elDOM==null)){
                return;
            };              
            $('#'+element.permissionTagId).removeClass('nmc-hide');
        });
        if (ctxFlag == false){
            window.location.assign('#/notices/list');
        };
    };
    setVisibles($scope.monitorSettings.ctxId);
    //listen change of service list
    $rootScope.$on('servicePermissions:loaded', function(){
        setVisibles($scope.monitorSettings.ctxId);
    });    
    
        //chart
    $scope.runChart = function(objId){
//        $scope.monitorSettings.createRoundDiagram = true;
        
        var curObjIndex = -1;
        $scope.objects.some(function(element, index){
            if(element.contObject.id == objId){
                curObjIndex = index;
                return true;
            };
        });
        if (curObjIndex==-1){
            return;
        };
//        $scope.getEventTypesByObject($scope.objects[curObjIndex]);
        var data = [];//, series = Math.floor(Math.random() * 6) + 3;
//console.log($scope.objects);        
        for (var i = 0; i < $scope.objects[curObjIndex].eventTypes.length; i++) {
			data[i] = {
				label: $scope.objects[curObjIndex].eventTypes[i].typeName,
				data: $scope.objects[curObjIndex].eventTypes[i].typeEventCount
			}
		};
        $scope.monitorSettings.createRoundDiagram = false;
        
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
}]);