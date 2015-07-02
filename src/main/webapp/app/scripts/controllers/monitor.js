angular.module('portalNMC')
  .controller('MonitorCtrl', function($rootScope, $http, $scope, $compile, $interval){
    //object url
    var noticesUrl = "#/notices/list/";
    var notificationsUrl = "../api/subscr/contEvent/notifications"; 
    var objectUrl = notificationsUrl+"/contObject";//"resource/objects.json";  
    var monitorUrl = notificationsUrl+"/monitorColor";
    //objects array
    $scope.objects = [];
    //default date interval settings
    $rootScope.reportStart = moment().startOf('day').format('YYYY-MM-DD');
    $rootScope.reportEnd =  moment().endOf('day').format('YYYY-MM-DD');    
    
    //monitor settings
    $scope.monitorSettings = {};
    $scope.monitorSettings.refreshPeriod = "60";
    $scope.monitorSettings.createRoundDiagram = false;
    
    //monitor state
    $scope.monitorState = {};
    $scope.getMonitorState = function(){
        var url = monitorUrl+"?fromDate="+$rootScope.reportStart+"&toDate="+$rootScope.reportEnd;
        $http.get(url)
            .success(function(data){
                $scope.monitorState = data;
                var monitorTab = document.getElementById('monitorTab');
                monitorTab.style.backgroundColor = $scope.monitorState.statusColor.toLowerCase();
                monitorTab.title = $scope.monitorState.colorDescription;
            })
            .error(function(e){
                console.log(e);
            });
    };
    $scope.getMonitorState();
    
    //flag: false - get all objectcs, true - get only  red, orange and yellow objects.
    $scope.noGreenObjects_flag = false;
    
    //get objects function
    $scope.getObjects = function(url){ 
        var targetUrl = url+"?fromDate="+$rootScope.reportStart+"&toDate="+$rootScope.reportEnd+"&noGreenColor="+$scope.noGreenObjects_flag;
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
                $scope.objects.forEach(function(element){
                    $scope.getMonitorEventsByObject(element);
                });
                makeObjectTable();
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
    
    //get monitor events
    $scope.getMonitorEventsByObject = function(obj){
        var url = objectUrl+"/"+obj.contObject.id+"/monitorEvents"+"?fromDate="+$rootScope.reportStart+"&toDate="+$rootScope.reportEnd;
        $http.get(url)
            .success(function(data){
            //if data is not array - exit
                if (!data.hasOwnProperty('length')||(data.length == 0)){
                    return;
                };
                //temp array
                var tmpMessage = "";
                //make the new array of the types wich formatted to display
                data.forEach(function(element){
                    var tmpEvent = "";
//                    tmpType.id = element.contEventType.id;
//                    tmpType.typeCategory = element.statusColor.toLowerCase();
//                    tmpType.typeEventCount = element.totalCount;
//                    tmpType.typeName = element.contEventType.name;
                    var contEventTime = new Date(element.contEventTime);
                    tmpEvent = contEventTime.toLocaleString()+", "+element.contEventType.name+"\n";
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
                var imgObj = document.getElementById("imgObj"+obj.contObject.id);
                imgObj.title = obj.monitorEvents;
//                makeEventTypesByObjectTable(obj);
            })
            .error(function(e){
                console.log(e);
            });        
    };
    
    //get event types by object
    $scope.getEventTypesByObject = function(objId){
        var obj = null;
        $scope.objects.some(function(element){
            if (element.contObject.id === objId){
                obj = element;
                return true;
            }
        });        
        //if cur object = null => exit function
        if (obj == null){
            return;
        };
        var url = objectUrl+"/"+obj.contObject.id+"/eventTypes"+"?fromDate="+$rootScope.reportStart+"&toDate="+$rootScope.reportEnd;
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
                    tmpType.typeCategory = element.statusColor.toLowerCase();
                    tmpType.typeEventCount = element.totalCount;
                    tmpType.typeName = element.contEventType.name;
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
                if ($scope.monitorSettings.createRoundDiagram){
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
            $scope.getEventTypesByObject(curObject.contObject.id);
            
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
        var tableHTML = "";
        $scope.objects.forEach(function(element, index){
            var trClass= index%2>0?"":"nmc-tr-odd"; //Подкрашиваем разным цветом четные / нечетные строки
            var imgSize = 16; //размер иконки состояния объекта
            if(element.statusColor.toLowerCase()=="green"){//если объет "зеленый", то размер уменьшаем до 1пх, чтобы ничего не выводилось 
                imgSize = 1;
            };
            tableHTML += "<tr class=\""+trClass+"\" id=\"obj"+element.contObject.id+"\">";
            tableHTML += "<td>";
            tableHTML += "<table>";
            tableHTML += "<tr>";
            tableHTML +="<td class=\"nmc-td-for-buttons\"> <i id=\"btnDetail"+element.contObject.id+"\" class=\"btn btn-xs noMargin glyphicon glyphicon-chevron-right nmc-button-in-table\" ng-click=\"toggleShowGroupDetails("+element.contObject.id+")\"></i>";
            tableHTML += "<img id=\"imgObj"+element.contObject.id+"\" title=\"\" height=\""+imgSize+"\" width=\""+imgSize+"\" src=\""+"images/object-state-"+element.statusColor.toLowerCase()+".png"+"\"/>";
            tableHTML+= "</td>";
            tableHTML += "<td class=\"col-md-1\"><a title=\"Всего уведомлений\" href=\""+noticesUrl+"\" ng-click=\"setNoticeFilterByObject("+element.contObject.id+")\">"+element.eventsCount+" / "+element.eventsTypesCount+"</a> (<a title=\"Новые уведомления\" href=\""+noticesUrl+"\" ng-click=\"setNoticeFilterByObjectAndRevision("+element.contObject.id+")\">"+element.newEventsCount+"</a>)";
            
            tableHTML += "</td>";
            tableHTML += "<td class=\"nmc-td-for-buttons\"><i class=\"btn btn-xs\" ng-click=\"getEventTypesByObject("+element.contObject.id+")\"><img height=\"16\" width=\"16\" src='images/roundDiagram4.png'/></i></td>";
            tableHTML += "<td class=\"col-md-3\">"+element.contObject.fullName+" <span ng-show=\"isSystemuser()\">(id = "+element.contObject.id+")</span></td>";
            tableHTML += "<td class=\"col-md-8\"></td></tr>";
//            tableHTML += "</tr>";
            tableHTML += "</table>";
            tableHTML += "</td>";
            tableHTML +="<tr id=\"trObjEvents"+element.contObject.id+"\">";
            tableHTML += "</tr>";                       
        });
//console.log(tableHTML); 
        objTable.innerHTML = tableHTML;
        $compile(objTable)($scope);
    };
    
    //Формируем таблицу с событиями объекта
    function makeEventTypesByObjectTable(obj){        
        var trObjEvents = document.getElementById("trObjEvents"+obj.contObject.id);      
        var trHTML = "<td><table id=\"eventTable"+obj.contObject.id+"\" class=\"crud-grid table table-lighter table-bordered table-condensed table-hover nmc-child-table\">"+
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
                    "<i class=\"btn btn-xs glyphicon glyphicon-list nmc-button-in-table\""+
                        "ng-click=\"getNoticesByObjectAndType("+obj.contObject.id+","+event.id+")\""+
                        "title=\"Посмотреть уведомления\">"+
                    "</i>"+
                "</td>";
            $scope.eventColumns.forEach(function(column){
                switch (column.name){
                    case "typeName": trHTML += "<td class=\"col-md-11\">"+event[column.name]+"<span ng-show=\"isSystemuser()\">(id = "+event.id+")</span></td>"; break;
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
                    default : trHTML += "<td>"+event[column.name]+"</td>"; break;
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
        $rootScope.monitor.monitorFlag = true;
        $rootScope.monitor.objectId = objId;
        $rootScope.monitor.isNew = null;
        $rootScope.monitor.fromDate = $rootScope.reportStart;
        $rootScope.monitor.toDate = $rootScope.reportEnd;
    };
    
    $scope.setNoticeFilterByObjectAndRevision = function(objId){
        $scope.setNoticeFilterByObject(objId);
        $rootScope.monitor.isNew = true;

    };
    
    $scope.setNoticeFilterByObjectAndType = function(objId, typeId){
        $scope.setNoticeFilterByObject(objId);
        $rootScope.monitor.typeIds = [typeId];
    };
    
    $scope.getNoticesByObjectAndType = function(objId, typeId){
        $scope.setNoticeFilterByObjectAndType(objId, typeId);
        window.location.assign(noticesUrl);
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
    $scope.$watch('reportStart', function (newDates) {
console.log("reportStart watch");        
        $scope.getObjects(objectUrl);                              
    }, false);
    
    var interval;
    //watch for the change of the refresh period
    $scope.$watch('monitor.refreshPeriod', function (newPeriod) {
//console.log("monitor.refreshPeriod watch");
//console.log("new period = "+newPeriod);        
        //cancel previous interval
        if (angular.isDefined(interval)){
            $interval.cancel(interval);
            interval = undefined;
        };
        //set new interval
//        interval = $interval(function(){
//            var time = (new Date()).toLocaleString();
//console.log("new interval");            
//console.log(time);
//console.log(Number($scope.monitorSettings.refreshPeriod));        
//            $scope.getObjects(objectUrl);
//        },Number($scope.monitorSettings.refreshPeriod)*1000);
        
    }, false);
    
    //Вызвываем с заданным периодом обновление монитора
//    interval = $interval(function(){
//        var time = (new Date()).toLocaleString();
//console.log(time);
//console.log(Number($scope.monitorSettings.refreshPeriod));        
//        $scope.getObjects(objectUrl);
//    },Number($scope.monitorSettings.refreshPeriod)*1000);
    
        //chart
    $scope.runChart = function(objId){
        $scope.monitorSettings.createRoundDiagram = true;
        
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
        $scope.getEventTypesByObject($scope.objects[curObjIndex]);
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