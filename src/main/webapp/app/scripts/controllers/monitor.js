angular.module('portalNMC')
  .controller('MonitorCtrl', function($rootScope, $http, $scope, $compile, $interval, $cookies){
    //object url
    var noticesUrl = "#/notices/list/";
    var notificationsUrl = "../api/subscr/contEvent/notifications"; 
    var objectUrl = notificationsUrl+"/contObject";//"resource/objects.json";  
    var monitorUrl = notificationsUrl+"/monitorColor";
    //objects array
    $scope.objects = [];
    //default date interval settings
    $rootScope.monitorStart = moment().startOf('day').format('YYYY-MM-DD');
    $rootScope.monitorEnd =  moment().endOf('day').format('YYYY-MM-DD');    
    
    //monitor settings
    $scope.monitorSettings = {};
    $scope.monitorSettings.refreshPeriod = "180";
    $scope.monitorSettings.createRoundDiagram = false;
    $scope.monitorSettings.loadingFlag = true;
    
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
    
    //flag: false - get all objectcs, true - get only  red, orange and yellow objects.
    $scope.noGreenObjects_flag = false;
    
    //get objects function
    $scope.getObjects = function(url){ 
        var targetUrl = url+"?fromDate="+$rootScope.monitorStart+"&toDate="+$rootScope.monitorEnd+"&noGreenColor="+$scope.noGreenObjects_flag;
console.log(targetUrl);  
        
        $http.get(targetUrl)
            .success(function(data){
                $scope.objects = data;
//console.log(data);            
                //sort objects by name
                $scope.objects.sort(function(a, b){
                    if (a.contObject.fullName>b.contObject.fullName){
                        return 1;
                    };
                    if (a.contObject.fullName<b.contObject.fullName){
                        return -1;
                    };
                    return 0;
                });  
                //get the list of the events, which set the object color
                $scope.objects.forEach(function(element){
                    if ((element.statusColor === "RED") ||(element.statusColor === "ORANGE") ){
                        $scope.getMonitorEventsByObject(element);
                    }
                });
//                $scope.objects.forEach(function(element){
//                    if ((element.statusColor === "RED") ||(element.statusColor === "ORANGE") ){
//                        $scope.getMonitorEventsByObjectAW(element);
//                    }
//                });
//                 for(var i=0; i<$scope.objects.length;i++){
//                     var element = $scope.objects[i];
//                    if ((element.statusColor === "RED") ||(element.statusColor === "ORANGE") ){
//                        $scope.getMonitorEventsByObject(element);
//                    }
//                };
                makeObjectTable();
            $scope.monitorSettings.loadingFlag = false;//data has been loaded
            if (angular.isDefined($rootScope.monitor) && $rootScope.monitor.objectId!==null){
                $scope.getEventTypesByObject($rootScope.monitor.objectId, false);
                $rootScope.monitor.objectId = null;
            };
            })
            .error(function(e){
                console.log(e);
            });
        $scope.noGreenObjects_flag = false; //reset flag
    };
    
    $scope.eventColumns = [
             {"name":"typeCategory", "header" : " ", "class":""},
            {"name":"typeEventCount", "header" : " ", "class":""},
            {"name":"typeName", "header" : "Типы уведомлений", "class":"col-md-10"}
            ];
    
    //get monitor events -alter way
    $scope.getMonitorEventsByObjectAW = function(obj){
console.log("getMonitorEventsByObjectAW");        
        var url = objectUrl+"/"+obj.contObject.id+"/monitorEvents";
        var imgObj = "#imgObj"+obj.contObject.id;          
        $(imgObj).qtip({
            content:{
                text: function(event, api){
                    $.ajax({url: url})
                        .done(function(data){
console.log(data);                        
                            api.set('content.text', data)
                        })
                        .fail(function(xhr, status, error){
console.log(error);                        
                            api.set('content.text', status+'; '+error)
                        })
                    return 'Загрузка ... ';
                }
            },
            position:{
                viewport: $(window)
            },
            style:{
                classes: 'qtip-bootstrap qtip-nmc-monitor-tooltip'
            }
        });
    };
    //get monitor events
    $scope.getMonitorEventsByObject = function(obj){       
//        var obj = findObjectById(objId);    
        //if cur object = null => exit function
//        if (obj == null){
//            return;
//        };
        var url = objectUrl+"/"+obj.contObject.id+"/monitorEvents";//+"?fromDate="+$rootScope.monitorStart+"&toDate="+$rootScope.monitorEnd;
        $http.get(url)
            .success(function(data){
//console.log("success");
            //if data is not array - exit
                if (!data.hasOwnProperty('length')||(data.length == 0)){
                    return;
                };
                //temp array
                var tmpMessage = "";
//                var tmpMessageEx = "";
                //make the new array of the types wich formatted to display
                data.forEach(function(element){
                    var tmpEvent = "";
//                    var tmpEventEx = "";
//                    tmpType.id = element.contEventType.id;
//                    tmpType.typeCategory = element.statusColor.toLowerCase();
//                    tmpType.typeEventCount = element.totalCount;
//                    tmpType.typeName = element.contEventType.name;
                    var contEventTime = new Date(element.contEventTime);
                    tmpEvent = contEventTime.toLocaleString()+", "+element.contEventType.name+"<br/><br/>";
                    tmpMessage+=tmpEvent;
                });
//                tmpTypes.sort(function(a, b){
//                    if (a.typeEventCount > b.typeEventCount){
//                        return -1;
//                    };
//                    if (a.typeEventCount < b.typeEventCount){
//                        return 1;
//                    };
//                    return 0;
//                });
                obj.monitorEvents = tmpMessage;
                //Display message
//                var imgObj = document.getElementById("imgObj"+obj.contObject.id);
//                imgObj.title = obj.monitorEvents;
            
                var imgObj = "#imgObj"+obj.contObject.id;          
                $(imgObj).qtip({
                    content:{
                        text: obj.monitorEvents
                    },
                    style:{
                        classes: 'qtip-bootstrap qtip-nmc-monitor-tooltip'
                    }
                });         
//                makeEventTypesByObjectTable(obj);
            })
            .error(function(e){
                console.log(e);
            });        
    };
    
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
        var url = objectUrl+"/"+obj.contObject.id+"/eventTypes"+"?fromDate="+$rootScope.monitorStart+"&toDate="+$rootScope.monitorEnd;
        $http.get(url)
            .success(function(data){
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
            var trObjEvents = document.getElementById("trObjEvents"+curObject.contObject.id);
            trObjEvents.innerHTML = "";
            var btnDetail = document.getElementById("btnDetail"+curObject.contObject.id);
            btnDetail.classList.remove("glyphicon-chevron-down");
            btnDetail.classList.add("glyphicon-chevron-right");
        };
    };
    

    //Рисуем таблицу с объектами
    function makeObjectTable(){
        var objTable = document.getElementById('objectTable');
//        var temptableHTML = "";
        var tableHTML = "";
//        var tmpArray = $scope.objects;
        $scope.objects.forEach(function(element, index){
            var trClass= index%2>0?"":"nmc-tr-odd"; //Подкрашиваем разным цветом четные / нечетные строки
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
            tableHTML +="<td class=\"nmc-td-for-buttons\"> <i id=\"btnDetail"+element.contObject.id+"\" class=\"btn btn-xs noMargin glyphicon glyphicon-chevron-right nmc-button-in-table\" ng-click=\"toggleShowGroupDetails("+element.contObject.id+")\"></i>";
            tableHTML += "<img id=\"imgObj"+element.contObject.id+"\" title=\"\" height=\""+imgSize+"\" width=\""+imgSize+"\" src=\""+"images/object-state-"+element.statusColor.toLowerCase()+".png"+"\" />";
//            ng-mouseover=\"getMonitorEventsByObject("+element.contObject.id+")\"
//            if (element.statusColor.toLowerCase()!="green"){
//                tableHTML +="<i title=\"Узнать причину оценки\" class=\"btn btn-xs glyphicon glyphicon-bookmark\" ng-click=\"getNoticesByObject("+element.contObject.id+")\" data-target=\"#showNoticesModal\" data-toggle=\"modal\"></i>";
//            };
            tableHTML+= "</td>";
            tableHTML += "<td class=\"col-md-1\"><a title=\"Всего уведомлений\" href=\""+noticesUrl+"\" ng-mouseover=\"setNoticeFilterByObject("+element.contObject.id+")\">"+element.eventsCount+" / "+element.eventsTypesCount+"</a> (<a title=\"Новые уведомления\" href=\""+noticesUrl+"\" ng-mouseover=\"setNoticeFilterByObjectAndRevision("+element.contObject.id+")\">"+element.newEventsCount+"</a>)";
            
            tableHTML += "</td>";
            tableHTML += "<td class=\"nmc-td-for-buttons\"><i class=\"btn btn-xs\" ng-click=\"getEventTypesByObject("+element.contObject.id+", true)\"><img height=\"16\" width=\"16\" src='images/roundDiagram4.png'/></i></td>";
            tableHTML += "<td class=\"col-md-3\">"+element.contObject.fullName+" <span ng-show=\"isSystemuser()\">(id = "+element.contObject.id+")</span></td>";
            tableHTML += "<td class=\"col-md-8\"></td></tr>";
//            tableHTML += "</tr>";
            tableHTML += "</table>";
            tableHTML += "</td>";
            tableHTML +="<tr id=\"trObjEvents"+element.contObject.id+"\">";
            tableHTML += "</tr>";                       
        });
//console.log(temptableHTML); 
        objTable.innerHTML = tableHTML;
//        objTable.innerHTML = tableHTML;
        $compile(objTable)($scope);
    };
    
    //Формируем таблицу с событиями объекта
    function makeEventTypesByObjectTable(obj){        
        var trObjEvents = document.getElementById("trObjEvents"+obj.contObject.id);      
        var trHTML = "<td style=\"padding-top: 2px !important;\"><table id=\"eventTable"+obj.contObject.id+"\" class=\"crud-grid table table-lighter table-bordered table-condensed table-hover nmc-child-table\">"+
            "<thead>"+
            "<tr class=\"nmc-child-table-header\">"+
                "<!--       Шапка таблицы-->"+
"<!--                        Колонка для кнопок-->"+
                "<th class=\"nmc-td-for-buttons\">"+
                "</th>"+
                "<th ng-repeat=\"eventColumn in eventColumns track by $index\" ng-class=\"eventColumn.class\" ng-click=\"setOrderBy(eventColumn.name)\" class=\"nmc-text-align-left\">"+
                        "{{eventColumn.header || eventColumn.name}}"+
                        "<i class=\"glyphicon\" ng-class=\"{'glyphicon-sort-by-alphabet': orderBy.asc, 'glyphicon-sort-by-alphabet-alt': !orderBy.asc}\" ng-show=\"orderBy.field == '{{eventColumn.name}}'\"></i>"+
                "</th>"+
            "</tr>"+
            "</thead>    ";
        if (angular.isUndefined(obj)||!obj.hasOwnProperty('eventTypes')||(obj.eventTypes.length==0)){            
            return;
        };      
        obj.eventTypes.forEach(function(event){
            trHTML +="<tr id=\"trEvent"+event.id+"\" >";
            trHTML +="<td class=\"nmc-td-for-buttons\">"+
                    "<a href=\""+noticesUrl+"\" ng-mouseover=\"getNoticesByObjectAndType("+obj.contObject.id+","+event.id+")\"> <i class=\"btn btn-xs glyphicon glyphicon-list nmc-button-in-table\""+
//                        "ng-click=\"getNoticesByObjectAndType("+obj.contObject.id+","+event.id+")\""+
                        "title=\"Посмотреть уведомления\">"+
                    "</i></a>"+
                "</td>";
            $scope.eventColumns.forEach(function(column){
                switch (column.name){
                    case "typeName": trHTML += "<td class=\"col-md-11\" ng-class=\"{'nmc-positive-notice':"+(!event.isBaseEvent)+"}\">"+event[column.name]+"<span ng-show=\"isSystemuser()\">(id = "+event.id+")</span></td>"; break;
                    case "typeCategory" : 
                        var size = 16;
                        var title = "";
                        if (event[column.name]=="green"){
                            size = 1;
                        };
                        switch (event[column.name]){
                            case "red": title = "Критическая ситуация"; break;
                            case "orange": title = "Некритическая ситуация"; break;
                                
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
        $rootScope.monitor = {};
        $cookies.monitorFlag = true;
        $cookies.objectId = objId;
        $cookies.isNew = null;
        $cookies.typeIds = null;
        $cookies.fromDate = $rootScope.monitorStart;
        $cookies.toDate = $rootScope.monitorEnd;
        $rootScope.reportStart = $rootScope.monitorStart;
        $rootScope.reportEnd = $rootScope.monitorEnd;
    };
    
    $scope.setNoticeFilterByObjectAndRevision = function(objId){
        $scope.setNoticeFilterByObject(objId);
        $cookies.isNew = true;

    };
    
    $scope.setNoticeFilterByObjectAndType = function(objId, typeId){
        $scope.setNoticeFilterByObject(objId);
        $cookies.typeIds = [typeId];
    };
    
    $scope.getNoticesByObjectAndType = function(objId, typeId){
console.log("getNoticesByObjectAndType");        
        $scope.setNoticeFilterByObjectAndType(objId, typeId);
//        window.location.assign(noticesUrl);
    };
    
    
    $scope.isSystemuser = function(){
        $scope.userInfo = $rootScope.userInfo;
        return $scope.userInfo._system;
    };
    //call get objects function
//    $scope.getObjects(objectUrl);
    
    $scope.refreshData = function(){
        $scope.getObjects(objectUrl);
    };
    
    $scope.getNoGreenObjects= function(){
        $scope.noGreenObjects_flag = true;
        $scope.getObjects(objectUrl);
    };
    
    //Watching for the change period 
    $scope.$watch('monitorStart', function (newDates) {
console.log("monitorStart watch");        
        $scope.getObjects(objectUrl);                              
    }, false);
    
    
//The control of the period monitor refresh(Управление перодическим обновлением монитора)
//**************************************************************************  
    var interval;
    
    function stopRefreshing(){
        if (angular.isDefined(interval)){
            $interval.cancel(interval);
            interval = undefined;
        };
    };
    
    $scope.$on('$destroy', function() {
//        alert("Ушли со страницы?");
        stopRefreshing();
    });
    
    
    //watch for the change of the refresh period
    $scope.$watch('monitorSettings.refreshPeriod', function (newPeriod) {
console.log("monitorSettings.refreshPeriod watch");
//console.log("new period = "+newPeriod);        
        //cancel previous interval
        stopRefreshing();
        //set new interval
        interval = $interval(function(){
            var time = (new Date()).toLocaleString();
//console.log("new interval");            
console.log(time);
//console.log(Number($scope.monitorSettings.refreshPeriod));        
            $scope.getObjects(objectUrl);
        },Number($scope.monitorSettings.refreshPeriod)*1000);
        
    }, false);
    
    //Вызвываем с заданным периодом обновление монитора
    interval = $interval(function(){
        var time = (new Date()).toLocaleString();
console.log(time);
//console.log(Number($scope.monitorSettings.refreshPeriod));        
        $scope.getObjects(objectUrl);
    },Number($scope.monitorSettings.refreshPeriod)*1000);
    
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
});