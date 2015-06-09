
angular.module('portalNMC')
    .controller('IndicatorsCtrl', ['$scope','$rootScope', '$cookies', 'crudGridDataFactory',function($scope, $rootScope, $cookies, crudGridDataFactory){

        //Определяем оформление для таблицы показаний прибора
        
        //Определеяю названия колонок
    var ALERT_IMG_PATH = "images/divergenceIndicatorAlert.png";
    var CRIT_IMG_PATH = "images/divergenceIndicatorCrit.png";
    var EMPTY_IMG_PATH = "images/plug.png";    
        
    var listColumns = {
            "dataDate":{
                header : "Дата",
                headerClass : "col-md-2",
                dataClass : "col-md-2"
            }, 
            "workTime":{
                header : "Время наработки, час",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }, 
            "h_delta":{
                header : "ГКал отопления, ГКал",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }, 
          "m_in":{
                header : "Масса подачи, т",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }, 
            "m_out":{
                header : "Масса обратки, т",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }, 
            "t_in":{
                header : "Температура подачи, град C",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }, 
            "t_out":{
                header : "Температура обратки, град C",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            } , 
            "t_cold":{
                header : "Температура холодной воды, град C",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            } ,
            "t_outdoor":{
                header : "Температура окружающей среды, град C",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "m_delta":{
                header : "Разница масс воды, т",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "v_in":{
                header : "Объем подачи, м3",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "v_out":{
                header : "Объем обратки, м3",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "v_delta":{
                header : "Разница объемов (потребление), м3",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "h_in":{
                header : "Входящие ГКал, ГКал",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "h_out":{
                header : "ГКал на выходе, ГКал",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "p_in":{
                header : "Давление на подаче, Мпа",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "p_out":{
                header : "Давление на обратке, Мпа",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "p_delta":{
                header : "Разность давлений, Мпа",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }
        }
        ;
      
    $scope.intotalColumns = [
        {"name": "m_in",
         "header":"Масса подачи",
         "class":"col-md-1",
         "imgpath" : "",
         "imgclass": ""
         ,"title":""
        },
        {"name": "m_out",
         "header":"Масса обратки",
         "class":"col-md-1"
         ,"imgpath" : ""
         ,"imgclass": ""
         ,"title":""
        },
        {"name": "v_in",
         "header":"Объем подачи",
         "class":"col-md-1"
         ,"imgpath" : ""
         ,"imgclass": ""
         ,"title":""
        },
        {"name": "v_out",
         "header":"Объем обратки",
         "class":"col-md-1"
         ,"imgpath" : ""
         ,"imgclass": ""
         ,"title":""
        },
        {"name": "h_delta",
         "header":"ГКал отопления",
         "class":"col-md-1"
         ,"imgpath" : ""
         ,"imgclass": ""
         ,"title":""
        }
    ];    
    
    $scope.tableDef = {
        tableClass : "crud-grid table table-lighter table-bordered table-condensed table-hover",
        hideHeader : false,
        headerClassTR : "nmc-main-table-header",
        columns : []
    };
    
    $scope.summary = {};
//    $scope.summary.intotal ={};// [5,1,2,3,4];
//    $scope.summary.integrators = [[1,1],[2,1],[3,1],[4,1],[5,2]];    
//    $scope.summary.firstData = {};
//    $scope.summary.lastData = {};    
        
//    $rootScope.reportStart = moment().format('YYYY-MM-DD');
//    $rootScope.reportEnd = moment().format('YYYY-MM-DD');
        
    $scope.totalIndicators = 0;
    $scope.indicatorsPerPage = 25; // this should match however many results your API puts on one page    
    $scope.timeDetailType = "24h";    
    $scope.data = [];    
    $scope.pagination = {
        current: 1
    };         

      //Получаем показания
    $scope.columns = [];
    $scope.getData = function (pageNumber) {
        $scope.pagination.current = pageNumber;   
         var contZPoint = $cookies.contZPoint;
         $scope.contZPointName = $cookies.contZPointName;
         var contObject = $cookies.contObject;
         $scope.contObjectName = $cookies.contObjectName;
        
//console.log($scope.timeDetailType);
//console.log($cookies.timeDetailType);        
         var timeDetailType = $scope.timeDetailType || $cookies.timeDetailType;
         
         $scope.zpointTable = "../api/subscr/"+contObject+"/service/"+timeDetailType+"/"+contZPoint+"/paged?beginDate="+$rootScope.reportStart+"&endDate="+$rootScope.reportEnd+"&page="+(pageNumber-1)+"&size="+$scope.indicatorsPerPage;
        var table =  $scope.zpointTable;
//console.log(table);        
        crudGridDataFactory(table).get(function (data) {           
                $scope.totalIndicators = data.totalElements;
                var iCol = 0;
                var notUserColumns = new Set(["id","toJSON","$get", "$save", "$query", "$remove", "$delete", "$update", "version", "timeDetailType"]);
                for (var k in data.objects[0]){ 
                    if (notUserColumns.has(k)){continue;};      
                    var column = {};
                    column.header = listColumns[k].header || k; 
                    column.headerClass = listColumns[k].headerClass || "col-md-1";
                    column.dataClass = listColumns[k].dataClass || "col-md-1";
                    column.fieldName = k; 
                    $scope.columns[iCol] = column;
                    iCol=iCol+1;                          
                };
                $scope.tableDef.columns = $scope.columns;

                var tmp = data.objects.map(function(el){
                    var result  = {};
                    for(var i in $scope.columns){
                        if ($scope.columns[i].fieldName == "dataDate"){
//                          var datad = new Date(el.dataDate);
                          el.dataDate = moment(el.dataDate).format("DD.MM.YY HH:mm");
  
                            continue;
                        }
                        if (el[$scope.columns[i].fieldName]!=null){
                            el[$scope.columns[i].fieldName] = el[$scope.columns[i].fieldName].toFixed(2);
                        };
                        
                    };                    
                });
                $scope.data = data.objects;
        });
        
        // get summary
        var table_summary = table.replace("paged", "summary");
        crudGridDataFactory(table_summary).get(function(data){
                $scope.summary = data;

                if (!$scope.summary.hasOwnProperty('diffs') || !$scope.summary.hasOwnProperty('totals')){
                    return;
                }
                        //work with fractional part
                //search the shortest fractional part
                $scope.intotalColumns.forEach(function(element, index, array){
                    var columnName = element.name;                      
                    var lengthFractPart = 0;
                    var diff = $scope.summary.diffs[columnName];
                    var total = $scope.summary.totals[columnName];
                    var diffStr = diff.toString();
                    var tempStrArr = diffStr.split(".");
                    var diffFractPart = tempStrArr.length>1? tempStrArr[1].length : 0;
                    var totalStr = total.toString();
                    tempStrArr = totalStr.split(".");
                    var totalFractPart = tempStrArr.length>1? tempStrArr[1].length : 0;
                    lengthFractPart = totalFractPart>diffFractPart ? diffFractPart : totalFractPart;
                    $scope.summary.diffs[columnName] = diff.toFixed(lengthFractPart);
                    $scope.summary.totals[columnName] = total.toFixed(lengthFractPart);

                    var precision = Number("0.00000000000000000000".substring(0, lengthFractPart+1)+"1");
//            console.log("diff = "+$scope.summary.diffs[columnName]);           
//            console.log("total = "+$scope.summary.totals[columnName]);           
//            console.log("precision = "+precision);        

                    var difference = Math.abs(($scope.summary.diffs[columnName]-$scope.summary.totals[columnName]));
//            console.log("difference = "+difference);         
            //        var difference = Math.abs(total - diff);
                    if ((difference >precision)&&(difference <= 1))
                    {
//            console.log(ALERT_IMG_PATH);         
                       element.imgpath=  ALERT_IMG_PATH;
                        element.imgclass= "nmc-img-divergence-indicator";
                        element.title = "Итого и показания интеграторов расходятся НЕ более чем на 1";
                        return;

                    };
                    if ((difference >1))
                    {  
//            console.log(CRIT_IMG_PATH);            
                        element.imgpath = CRIT_IMG_PATH;
                        element.imgclass= "nmc-img-divergence-indicator";
                        element.title = "Итого и показания интеграторов расходятся БОЛЕЕ чем на 1";
                        return;
                    };
                    element.imgpath = EMPTY_IMG_PATH;
                    element.imgclass= "";
                    element.title = "";
                });
console.log(data);            
        });
    };

    $scope.pageChanged = function(newPage) {       
        $scope.getData(newPage);
    };  
        
    $scope.$watch('reportStart', function (newDates) {  
        if( (typeof $rootScope.reportStart == 'undefined') || ($rootScope.reportStart==null) ){
            return;
        }
        $scope.getData(1);                              
    }, false); 
    
//    $scope.setTitle = function(fieldName){ 
//        if ((typeof $scope.summary.firstData=='undefined')||($scope.summary.firstData==null)||($scope.summary.firstData == {})){
//            return;
//        };
//        return "Начальное значение = "+$scope.summary.firstData[fieldName] +"(Дата = "+
//            (new Date($scope.summary.firstData['dataDate'])).toLocaleString()+");" +"\n"+
//                "Конечное значение = "+$scope.summary.lastData[fieldName] +"(Дата = "+
//            (new Date($scope.summary.lastData['dataDate'])).toLocaleString()+")";
//    }; 
    

        
    $scope.getIndicatorImage = function(columnName){        
        if (($scope.summary.lastData == null)||($scope.summary.firstData == null)||($scope.summary.totals == null)){
            return;
        };
//console.log("$scope.summary.lastData["+columnName+"]="+$scope.summary.lastData[columnName]);
//console.log("$scope.summary.firstData["+columnName+"]="+$scope.summary.firstData[columnName]);
//console.log("$scope.summary.totals["+columnName+"]="+$scope.summary.totals[columnName]);  
//console.log("$scope.summary.lastData["+columnName+"]-"+"$scope.summary.firstData["+columnName+"]="+($scope.summary.lastData[columnName]-$scope.summary.firstData[columnName]));    
        
        //work with fractional part
        //search the shortest fractional part
        var lengthFractPart = 0;
        var diff = $scope.summary.diffs[columnName];
        var total = $scope.summary.totals[columnName];
        var diffStr = diff.toString();
        var tempStrArr = diffStr.split(".");
        var diffFractPart = tempStrArr[1];
        var totalStr = total.toString();
        tempStrArr = totalStr.split(".");
        var totalFractPart = tempStrArr[1];
        lengthFractPart = totalFractPart.length>diffFractPart.length ? diffFractPart.length : totalFractPart.length;
        $scope.summary.diffs[columnName] = diff.toFixed(lengthFractPart);
        $scope.summary.totals[columnName] = total.toFixed(lengthFractPart);
        
        var precision = Number("0.00000000000000000000".substring(0, lengthFractPart+1)+"1");
console.log("diff = "+$scope.summary.diffs[columnName]);           
console.log("total = "+$scope.summary.totals[columnName]);           
console.log("precision = "+precision);        
        
        var difference = Math.abs(($scope.summary.diffs[columnName]-$scope.summary.totals[columnName]));
console.log("difference = "+difference);         
//        var difference = Math.abs(total - diff);
        if ((difference >precision)&&(difference <= 1))
        {
console.log(ALERT_IMG_PATH);         
            return ALERT_IMG_PATH;

        };
        if ((difference >1))
        {  
console.log(CRIT_IMG_PATH);            
            return CRIT_IMG_PATH;
        };

    };    
        
    $scope.setBgColor = function(columnName){
        if (($scope.summary.lastData == null)||($scope.summary.firstData == null)||($scope.summary.totals == null)){
            return;
        };
//console.log("$scope.summary.lastData["+columnName+"]="+$scope.summary.lastData[columnName]);
//console.log("$scope.summary.firstData["+columnName+"]="+$scope.summary.firstData[columnName]);
//console.log("$scope.summary.totals["+columnName+"]="+$scope.summary.totals[columnName]);  
//console.log("$scope.summary.lastData["+columnName+"]-"+"$scope.summary.firstData["+columnName+"]="+($scope.summary.lastData[columnName]-$scope.summary.firstData[columnName]));        
        var diff = Math.abs(($scope.summary.diffs[columnName]-$scope.summary.totals[columnName]));
//console.log("Diff ="+diff); 
//        diff = diff.toFixed(2);
//        if ((diff >=0)&&(diff < 0.005))
//        {
//            return '#66CC00';
//        };
        if ((diff >=0.0000000000001)&&(diff <= 1))
        {
            return '3px solid yellow';
        };
        if ((diff >1))
        {
            return '3px solid red';
        };
    }; 
        
    $scope.setLiColor = function(columnName){
        if (($scope.summary.lastData == null)||($scope.summary.firstData == null)||($scope.summary.totals == null)){
            return;
        };
//console.log("$scope.summary.lastData["+columnName+"]="+$scope.summary.lastData[columnName]);
//console.log("$scope.summary.firstData["+columnName+"]="+$scope.summary.firstData[columnName]);
//console.log("$scope.summary.totals["+columnName+"]="+$scope.summary.totals[columnName]);  
//console.log("$scope.summary.lastData["+columnName+"]-"+"$scope.summary.firstData["+columnName+"]="+($scope.summary.lastData[columnName]-$scope.summary.firstData[columnName]));        
        var diff = Math.abs(($scope.summary.diffs[columnName]-$scope.summary.totals[columnName]));
//console.log("Diff ="+diff); 
//        diff = diff.toFixed(2);
//        if ((diff >=0)&&(diff < 0.005))
//        {
//            return '#66CC00';
//        };
        if ((diff >=0.0000000000001)&&(diff <= 1))
        {
            return {display: 'inherit', color: 'yellow'};
        };
        if ((diff >1))
        {
            return {display: 'inherit', color: 'red'};
        };
    };     
    
    $scope.toggleDetail = function(object){
        object.detail = !object.detail;
        return object.detail;
    };
        
    $scope.saveIndicatorsToFile = function(){
//        var csv = "hello text";
//console.log(csv);                
//        var csvData = 'data:application/csv;charset=utf-8,' + encodeURIComponent(csv);
//console.log(window.location.href);    
//        window.open("",'_blank');
//        window.location.href = csvData;
//        this.target = '_blank';
//        this.download = 'filename.csv';
//        alert("Нажата кнопка сохранить страницу с показаниями в файл.");
          
        var contZPoint = $cookies.contZPoint;
//        $scope.contZPointName = $cookies.contZPointName;
        var contObject = $cookies.contObject;
//        $scope.contObjectName = $cookies.contObjectName;

        var timeDetailType = $scope.timeDetailType || $cookies.timeDetailType;
         
        var url = "../api/subscr/"+contObject+"/service/"+timeDetailType+"/"+contZPoint+"/csv?beginDate="+$rootScope.reportStart+"&endDate="+$rootScope.reportEnd;
        window.open(url);
    }; 
        
     //chart
    $scope.runChart = function(){
        var data = [];
        for (var i=0; i<$scope.data.length; i++){
            data.push([$scope.data[i].dataDate, $scope.data[i].h_delta]);
//            data.push([i, $scope.data[i].h_delta]);
        };
//        for (var i = 0; i < 14; i += 0.5) {
//			data.push([i, Math.sin(i)]);
//		}
//console.log("data before====================");
//console.log(data);    
//console.log("----------------------------------");        
        for(var i = 0; i < data.length; i++){
//console.log(Date.parse(data[i][0]));            
//            data[i][0] = Date.parse(data[i][0]);
//            moment(el.dataDate).format("DD.MM.YY HH:mm");
console.log("====================");              
console.log(moment(data[i][0], "DD.MM.YY HH:mm"));   
console.log(moment.utc(data[i][0]));              
console.log("====================");              
            data[i][0] = moment(data[i][0], "DD.MM.YY HH:mm");
        };
//console.log("data after====================");        
//console.log(data);
//console.log("====================");                
        // свойства графика
        var plotConf = {
         series: {
           lines: {
             show: true,
             lineWidth: 2
           }
         },
         xaxis: {
           mode: "time",
           timeformat: "%d.%m.%y %h:%M",
         }
        };
        var plotData = [];
        plotData.push(data);
        // выводим график
        $("#indicatorChart-area").width(600);
        $("#indicatorChart-area").height(300);
       // $("#chartModal.modal-dialog").width(700);
        $('#chartModal').modal();
        
        $.plot('#indicatorChart-area', plotData, plotConf);
        
    };
      
        
}]);