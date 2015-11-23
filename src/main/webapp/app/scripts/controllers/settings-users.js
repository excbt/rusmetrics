angular.module('portalNMC')
.controller('SettingsUsersCtrl', ['$scope','$http', 'notificationFactory', 'crudGridDataFactory', 'mainSvc', function($scope, $http, notificationFactory, crudGridDataFactory, mainSvc){
console.log("Run user settings controller.");
    $scope.extraProps = {"idColumnName":"id", "defaultOrderBy" : "fullName", "nameColumnName":"fullName"};//angular.fromJson($attrs.exprops);
        //controller settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.orgUrl = "../api/rma/contObjects/cmOrganizations";
    $scope.ctrlSettings.clientsUrl = "../api/rma/subscribers";
    $scope.ctrlSettings.subscrUrl = "../api/subscr";
    $scope.ctrlSettings.userUrlSuffix = "/subscrUsers";
    
    //client columns
    $scope.ctrlSettings.userColumns =[
        {
            "name": "userName",
            "caption": "Логин",
            "class": "col-md-2"
        },
        {
            "name": "firstName",
            "caption": "Имя",
            "class": "col-md-3"
        },
        {
            "name": "lastName",
            "caption": "Фамилия",
            "class": "col-md-3"
        },
        {
            "name": "userComment",
            "caption": "Комментарий",
            "class": "col-md-4"
        }
    ];
    //data
    $scope.data={};
    $scope.data.organizations = [];
    $scope.data.clients = [];
    $scope.data.users = [];
    $scope.data.currentUser = {};
    $scope.data.currentClient = {};
    
    //get users
    var getUsers = function(){
        var targetUrl = $scope.ctrlSettings.subscrUrl+$scope.ctrlSettings.userUrlSuffix;
        $http.get(targetUrl)
        .then(function(response){
            response.data.forEach(function(elem){
                elem.subscrRoles.some(function(role){
                    if (role.roleName == "ROLE_SUBSCR_ADMIN"){
                        elem.isAdmin = true;
                        return true;
                    };
                });
            });
            $scope.data.users = response.data;
//console.log($scope.data.users);            
        },
             function(e){
            console.log(e);
        });
    };
    
    //    get subscribers
//    var getClients = function(){
//        var targetUrl = $scope.ctrlSettings.clientsUrl;
//        $http.get(targetUrl)
//        .then(function(response){
//            response.data.forEach(function(el){
//                el.organizationName = el.organization.organizationFullName;
//            });
//            $scope.data.clients = response.data;           
//            $scope.data.currentClient.id = $scope.data.clients[0].id;
//            getUsers();
//           
//        },
//             function(e){
//            console.log(e);
//        });
//    };
    
        //get organizations
    var getOrganizations = function(){
        var targetUrl = $scope.ctrlSettings.orgUrl
        $http.get(targetUrl)
        .then(function(response){
            $scope.data.organizations =  response.data;
//console.log($scope.data.organizations);
//            getClients();
            getUsers();
        },
             function(e){
            console.log(e);
        });
    };
    getOrganizations(); 
    
    $scope.selectedItem = function (item) {
        var curObject = angular.copy(item);
        $scope.data.currentUser = curObject;
    };
    
    $scope.addUser =  function(){
        $scope.data.currentUser = {};
        $scope.data.currentUser.id=null;
        $scope.data.currentUser.isAdmin = false;
        $('#showUserOptionModal').modal();
    };
    
    //data processing
     var successCallback = function (e, cb) {                    
        notificationFactory.success();
        $('#deleteUserModal').modal('hide');
        $('#showUserOptionModal').modal('hide');
         getUsers();
    };

    var errorCallback = function (e) {
        notificationFactory.errorInfo(e.statusText,e.data.description || e.data); 
        console.log(e);
    };
    
    $scope.sendUserToServer = function(obj){
//        obj.organizationId = 726;
//        obj.timezoneDef = null;"64166467"
        if ($scope.checkForm(obj) == false){
            return false;
        };
        var url = $scope.ctrlSettings.subscrUrl+$scope.ctrlSettings.userUrlSuffix;                    
        if (angular.isDefined(obj.id)&&(obj.id!=null)){
            $scope.updateObject(url, obj);
        }else{
            $scope.addObject(url,obj);
        };
    };
    
    $scope.addObject = function (url, obj) {
        url+="/?isAdmin="+obj.isAdmin;//+"&newPassword="+obj.password;
        if (angular.isDefined(obj.password)&&(obj.password!=null)&&(obj.password !="")){
            url+= "&newPassword="+obj.password;
        };
        crudGridDataFactory(url).save(obj, successCallback, errorCallback);
    };

    $scope.deleteObject = function (obj) {
        var url = $scope.ctrlSettings.subscrUrl+$scope.ctrlSettings.userUrlSuffix;                 
        crudGridDataFactory(url).delete({ id: obj[$scope.extraProps.idColumnName] }, successCallback, errorCallback);
    };

    $scope.updateObject = function (url, object) {
        var params = { id: object[$scope.extraProps.idColumnName],
                     isAdmin: object.isAdmin};
        if (angular.isDefined(object.password)&&(object.password!=null)&&(object.password !="")){
            params.oldPassword = object.curpassword;
            params.newPassword = object.password;
        };
        crudGridDataFactory(url).update(params, object, successCallback, errorCallback);
    };
    
    $scope.deleteObjectInit = function(object){
        $scope.selectedItem(object);
        //generation confirm code
        $scope.confirmCode = null;
        var tmpCode = mainSvc.getConfirmCode();
        $scope.confirmLabel = tmpCode.label;
        $scope.sumNums = tmpCode.result;
    };
    
    $scope.changeClient = function(){
        getUsers();
    };
    
    //checkers
        //user: admin?
    $scope.isAdmin = function(){
        return mainSvc.isAdmin();
    };
    //$scope.checkString  
    $scope.emptyString = function(str){
        return mainSvc.checkUndefinedEmptyNullValue(str);
    };
    
    $scope.checkPassword = function(){
        var result = false;
        result = !((($scope.data.currentUser.id==null)&&($scope.emptyString($scope.data.currentUser.password)))
            || ($scope.data.currentUser.password!=$scope.data.currentUser.passwordConfirm))
        ;
        if (($scope.data.currentUser.id!=null)
            &&((angular.isString($scope.data.currentUser.password)))
            &&((angular.isUndefined($scope.data.currentUser.curpassword))||($scope.data.currentUser.curpassword==null)||($scope.data.currentUser.curpassword==""))){
            result = false;
        };
        return result;
    };
    
    $scope.checkForm = function(obj){
        var result = true;
        if ($scope.emptyString(obj.lastName)){
            notificationFactory.errorInfo("Ошибка", "Не задана фамилия пользователя!");
            result = false;
        };
        if ($scope.emptyString(obj.firstName)){
            notificationFactory.errorInfo("Ошибка", "Не задано имя пользователя!");
            result = false;
        };
        if ($scope.emptyString(obj.userName)){
            notificationFactory.errorInfo("Ошибка", "Не задан логин пользователя!");
            result = false;
        };
        if ((!$scope.checkPassword())){
            notificationFactory.errorInfo("Ошибка", "Не корректно задан пароль!");
            result = false;
        };
        return result;
    };
    
    //set mask for login input
    $(document).ready(function(){
        $('#inputUserName').inputmask(
            {mask: "a[*{1,20}]", 
             greedy: false,
             definitions:{
                 '*':{
                     validator: "[0-9a-z!_-]",
                    cardinality: 1,
                    casing: "lower"
                 }
             }
            }
        );
    });
}]);