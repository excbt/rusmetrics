'use strict'

var app = angular.module('portalNMK');
app.controller('DirectoryCtrl', function($scope){
//    $scope.$watch( 'abc.currentNode', function( newObj, oldObj ) {
//    if( $scope.abc && angular.isObject($scope.abc.currentNode) ) {
//        console.log( 'Node Selected!!' );
//        console.log( $scope.abc.currentNode );
//    }
//    }, false);
    
    $scope.clk = function(){
        alert("Hi");
    };
});