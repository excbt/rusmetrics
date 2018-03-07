/*global $*/
(function () {
    'use strict';
    
    $('.modal-content').resizable({
        minHeight: 300,
        minWidth: 300
    });
    
    $('.modal-dialog').draggable();
    
})();