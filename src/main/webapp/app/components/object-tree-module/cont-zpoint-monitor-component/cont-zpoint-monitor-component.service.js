/*global angular*/
(function () {
    'use strict';

    angular
        .module('objectTreeModule')
        .service('contZpointMonitorComponentService', Service);

    Service.$inject = ['$http', '$q'];

    /* @ngInject */
    function Service($http, $q) {
        
        var EVENTS_URL = "../api/subscr/contEvent/notifications/contObject/{objectId}/monitorEventsV2",
            NOTIFICATIONS_URL = "../api/subscr/contEvent/notifications/paged?page=0&size=100";
//        /notifications/contObject/{contObjectId}/monitorEventsV2/byContZPoint/{contZPointId}
        
        var requestCanceler = null;
        var httpOptions = null;
        
        this.loadEvents = loadEvents;
        this.loadNotifications = loadNotifications;

        ////////////////
        
        var checkUndefinedNull = function (numvalue) {
            var result = false;
            if ((angular.isUndefined(numvalue)) || (numvalue === null)) {
                result = true;
            }
            return result;
        };

        function isCancelParamsIncorrect() {
            return checkUndefinedNull(requestCanceler) || checkUndefinedNull(httpOptions);
        }

        function getRequestCanceler() {
            return requestCanceler;
        }

        function loadEvents(objId) {
//            var notificationsUrl = "../api/subscr/contEvent/notifications";
//    var objectUrl = notificationsUrl + "/contObject";
//            var url = objectUrl + "/" + obj.contObject.id + "/monitorEvents";
//            var url = objectUrl + "/" + objId + "/monitorEventsV2";// + "?fromDate=" + $rootScope.monitorStart + "&toDate=" + $rootScope.monitorEnd;
            var url = EVENTS_URL.replace("{objectId}", objId);
//            if (isCancelParamsIncorrect() === true) {
//                return null;
//            }
            return $http.get(url, httpOptions);
        }
        
        function loadNotifications(startDate, endDate, objectArray, eventTypeArray, categoriesArray, deviationsArray, isNew) {
            var url = NOTIFICATIONS_URL;
            var params = {
                fromDate: startDate,
                toDate: endDate,
                contEventTypeIds: eventTypeArray,
                contEventCategories: categoriesArray,
                contEventDeviations: deviationsArray,
                contObjectIds: objectArray
            };
            if (angular.isDefined(isNew) && isNew !== null) {
                params.isNew = isNew;
            }
            //return $resource(table, {}, {'get': {method:'GET', params:params, cancellable: true}});
            return $http({
                url: url,
                method: "GET",
                params: params
            });
        }
        
        var initSvc = function () {

            requestCanceler = $q.defer();
            httpOptions = {
                timeout: requestCanceler.promise
            };
            //run getObjects
    //            getMonitorData();
    //            getCitiesAndObjects(cityWithObjectsUrl, monitorSvcSettings);
        };
        initSvc();
    }
})();