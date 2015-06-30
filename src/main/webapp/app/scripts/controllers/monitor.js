angular.module('portalNMC')
  .controller('MonitorCtrl', function($rootScope, $http, $scope, $compile){
    //object url
    var objectUrl = "resource/objects.json";    
    //objects array
    $scope.objects = [];
    //get objects function
    $scope.getObjects = function(url){        
        $http.get(url)
            .success(function(data){
                $scope.objects = data;           
                //sort objects by name
                $scope.objects.sort(function(a, b){
                    if (a.fullName>b.fullName){
                        return 1;
                    };
                    if (a.fullName<b.fullName){
                        return -1;
                    };
                    return 0;
                });         
                makeObjectTable();
            })
            .error(function(e){
                console.log(e);
            });
    };
    
    $scope.eventColumns = [
             {"name":"typeCategory", "header" : " ", "class":""},
            {"name":"typeEventCount", "header" : " ", "class":""},
            {"name":"typeName", "header" : "Типы уведомлений", "class":"col-md-10"}
            ]
    
    $scope.toggleShowGroupDetails = function(objId){//switch option: current goup details
        var curObject = null;
        $scope.objects.some(function(element){
            if (element.id === objId){
                curObject = element;
                return true;
            }
        });                  
        //if cur object = null => exit function
        if (curObject == null){
            return;
        };
        //else
        var eventTable = document.getElementById("eventTable"+curObject.id);
        if ((curObject.showGroupDetails==true) && (eventTable==null)){
            curObject.showGroupDetails =true;
        }else{
            curObject.showGroupDetails =!curObject.showGroupDetails;
        };                     
        //if curObject.showGroupDetails = true => get zpoints data and make zpoint table
        if (curObject.showGroupDetails === true){

            makeEventTypesByObjectTable(curObject);
            var btnDetail = document.getElementById("btnDetail"+curObject.id);
            btnDetail.classList.remove("glyphicon-chevron-right");
            btnDetail.classList.add("glyphicon-chevron-down");
        }//else if curObject.showGroupDetails = false => hide child zpoint table
        else{
            var trObjEvents = document.getElementById("trObjEvents"+curObject.id);
            trObjEvents.innerHTML = "";
            var btnDetail = document.getElementById("btnDetail"+curObject.id);
            btnDetail.classList.remove("glyphicon-chevron-down");
            btnDetail.classList.add("glyphicon-chevron-right");
        };
    };

    //Рисуем таблицу с объектами
    function makeObjectTable(){
        var objTable = document.getElementById('objectTable');
        var tableHTML = "";
        $scope.objects.forEach(function(element, index){
            var trClass= index%2>0?"":"success"; //Подкрашиваем разным цветом четные / нечетные строки
            var imgSize = 16; //размер иконки состояния объекта
            if(element.objectState=="green"){//если объет "зеленый", то размер уменьшаем до 1пх, чтобы ничего не выводилось 
                imgSize = 1;
            };
            tableHTML += "<tr class=\""+trClass+"\" id=\"obj"+element.id+"\">";
            tableHTML += "<td>";
            tableHTML += "<table>";
            tableHTML += "<tr>";
            tableHTML +="<td class=\"nmc-td-for-buttons\"> <i id=\"btnDetail"+element.id+"\" class=\"btn btn-xs noMargin glyphicon glyphicon-chevron-right nmc-button-in-table\" ng-click=\"toggleShowGroupDetails("+element.id+")\"></i>";
            tableHTML += "<img height=\""+imgSize+"\" width=\""+imgSize+"\" src=\""+"images/object-state-"+element.objectState+".png"+"\"/>";
            tableHTML+= "</td>";
            tableHTML += "<td class=\"col-md-1\"><a title=\"Всего уведомлений\" href=\"\">"+element.eventCount+" / "+element.eventTypeCount+"</a> (<a title=\"Новые уведомления\" href=\"\">"+element.eventCountNew+"</a>)";
            
            tableHTML += "</td>";
            tableHTML += "<td class=\"nmc-td-for-buttons\"><i class=\"btn btn-xs\" ng-click=\"runChart("+element.id+")\"><img height=\"16\" width=\"16\" src='images/roundDiagram4.png'/></i></td>";
            tableHTML += "<td class=\"col-md-3\">"+element.fullName+" <span ng-show=\"isSystemuser()\">(id = "+element.id+")</span></td>";
            tableHTML += "<td class=\"col-md-8\"></td></tr>";
//            tableHTML += "</tr>";
            tableHTML += "</table>";
            tableHTML += "</td>";
            tableHTML +="<tr id=\"trObjEvents"+element.id+"\">";
            tableHTML += "</tr>";                       
        });
//console.log(tableHTML); 
        objTable.innerHTML = tableHTML;
        $compile(objTable)($scope);
    };
    
    //Формируем таблицу с событиями объекта
    function makeEventTypesByObjectTable(object){
        var trObjEvents = document.getElementById("trObjEvents"+object.id);
        var trHTML = "<td><table id=\"eventTable"+object.id+"\" class=\"crud-grid table table-lighter table-bordered table-condensed table-hover nmc-child-table\">"+
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
        object.eventTypes.forEach(function(event){
            trHTML +="<tr id=\"trEvent"+event.id+"\" >";
            trHTML +="<td class=\"nmc-td-for-buttons\">"+
                    "<i class=\"btn btn-xs glyphicon glyphicon-list nmc-button-in-table\""+
                        "ng-click=\"getZpointSettings("+object.id+","+event.id+")\""+
                        "data-target=\"#showZpointOptionModal\""+
                        "data-toggle=\"modal\""+
                        "data-placement=\"bottom\""+
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
                            case "orange": title = "Не критическая ситуация"; break;
                                
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
    
    $scope.isSystemuser = function(){
        $scope.userInfo = $rootScope.userInfo;
        return $scope.userInfo._system;
    };
    //call get objects function
    $scope.getObjects(objectUrl);
    
        //chart
    $scope.runChart = function(objId){
        var curObjIndex = -1;
        $scope.objects.some(function(element, index){
            if(element.id == objId){
                curObjIndex = index;
                return true;
            };
        });
        if (curObjIndex==-1){
            return;
        };
        var data = [];//, series = Math.floor(Math.random() * 6) + 3;
        for (var i = 0; i < $scope.objects[curObjIndex].eventTypes.length; i++) {
			data[i] = {
				label: $scope.objects[curObjIndex].eventTypes[i].typeName,
				data: $scope.objects[curObjIndex].eventTypes[i].typeEventCount
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