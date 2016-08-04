angular.module('portalNMC')
    .directive('metaDataEditorModal', function(){
    return {
        restrict: "AE",
        replace: true,
        scope: {
            currentZpoint: "=",
            readOnly: "@",
            showOkButton: "@",
            btnClick: "&"
        },
        templateUrl: 'scripts/directives/templates/meta-data-editor-modal.html',
        controller: ['$scope', function($scope){
            
            $scope.zpointMetadataIntegratorsFlag = false;
            
            $scope.data = {};
            $scope.data.metadataSchema = [
                {
                    header: 'Поле источник',
                    headClass : 'col-xs-2 col-md-2',
                    name: 'srcProp',
                    type: 'select_src_field'
                },{
                    header: 'Делитель',
                    headClass : 'col-xs-1 col-md-1',
                    name: 'srcPropDivision',
                    type: 'input/text',
                    disabled: false
                },{
                    header: 'Единицы измерения источника',
                    headClass : 'col-xs-1 col-md-1',
                    name: 'srcMeasureUnit',
                    type: 'select_measure_units',
                    disabled: false
                },{
                    header: 'Единицы измерения приемника',
                    headClass : 'col-xs-1 col-md-1',
                    name: 'destMeasureUnit',
                    type: 'select_measure_units',
                    disabled: false
                }
                ,{
                    header: 'Поле приемник',
                    headClass : 'col-xs-2 col-md-2',
                    name: 'destProp',
                    type: 'input/text',
                    disabled: true
                },{
                    header: 'Функция',
                    headClass : 'col-xs-1 col-md-1',
                    name: 'propFunc',
                    type: 'input/text',
                    disabled: true
                }
            ];
            
            $scope.isDisabled = function () {
                return $scope.readOnly;
            }
        }]
    }
})