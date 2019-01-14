/*global angular*/
angular.module('objectTreeModule').run(runFn);

runFn.$inject = ['contObjectService'];

/* @ngInject */
function runFn (contObjectService) {
    contObjectService.loadContObjectsShortInfoWrap();
}