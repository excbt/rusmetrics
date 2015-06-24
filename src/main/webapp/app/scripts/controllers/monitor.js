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
             {"name":"typeCategory", "header" : "Категория", "class":"col-md-2"},
            {"name":"typeEventCount", "header" : "Количество", "class":"col-md-2"},
            {"name":"typeName", "header" : "Наименование", "class":"col-md-2"}
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
        $scope.objects.forEach(function(element){
            tableHTML += "<tr id=\"obj"+element.id+"\">";
            tableHTML += "<td>";
            tableHTML += "<table>";
            tableHTML += "<tr>";
            tableHTML +="<td class=\"nmc-td-for-buttons\"> <i id=\"btnDetail"+element.id+"\" class=\"btn btn-xs noMargin glyphicon glyphicon-chevron-right nmc-button-in-table\" ng-click=\"toggleShowGroupDetails("+element.id+")\"></i>";
            tableHTML += "<img height=\"16\" width=\"16\" src=\""+"images/object-state-"+element.objectState+".png"+"\"/>";
            tableHTML+= "</td>";
            tableHTML += "<td class=\"col-md-1\"><a href=\"\">"+element.eventCount+"</a> / <a href=\"\">"+element.eventTypeCount+"</a> (<a href=\"\">"+element.eventCountNew+"</a>)";
            
            tableHTML += "</td>";
            tableHTML += "<td class=\"nmc-td-for-buttons\"><img height=\"16\" width=\"16\" src='images/roundDiagram_1.png' /></td>";
            tableHTML += "<td class=\"col-md-2\">"+element.fullName+" <span ng-show=\"isSystemuser()\">(id = "+element.id+")</span></td>";
            tableHTML += "<td class=\"col-md-7\"></td></tr>";
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
        var trHTML = "<td><table id=\"eventTable"+object.id+"\" class=\"crud-grid table table-lighter table-bordered table-condensed table-hover nmc-child-object-table\">"+
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
            trHTML +="<tr id=\"trEvent"+event.id+"\" ng-dblclick=\"getIndicators("+object.id+","+event.id+")\">";
            trHTML +="<td class=\"nmc-td-for-buttons\">"+
                    "<i class=\"btn btn-xs glyphicon glyphicon-edit nmc-button-in-table\""+
                        "ng-click=\"getZpointSettings("+object.id+","+event.id+")\""+
                        "data-target=\"#showZpointOptionModal\""+
                        "data-toggle=\"modal\""+
                        "data-placement=\"bottom\""+
                        "title=\"Свойства точки учёта\">"+
                    "</i>"+
                    "<i class=\"btn btn-xs nmc-button-in-table\""+
                        "ng-click=\"getZpointSettings("+object.id+","+event.id+")\""+
                        "data-target=\"#showZpointExplParameters\""+
                        "data-toggle=\"modal\""+
                        "data-placement=\"bottom\""+
                        "title=\"Эксплуатационные параметры точки учёта\">"+
                            "<img height=12 width=12 src=\"vendor_components/glyphicons_free/glyphicons/png/glyphicons-140-adjust-alt.png\" />"+
                    "</i>"+
                "</td>";
            $scope.eventColumns.forEach(function(column){
                switch (column.name){
                    case "typeName": trHTML += "<td>"+event[column.name]+"<span ng-show=\"isSystemuser()\">(id = "+event.id+")</span></td>"; break;
                    case "typeCategory" : trHTML +="<td><img height=\"16\" width=\"16\" src=\""+"images/object-state-"+event[column.name]+".png"+"\"/></td>"; break;   
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
});