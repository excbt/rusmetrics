'use strict'
angular.module('portalNMK').directive( 'treeModel', ['$compile', function( $compile ) {
		return {
			restrict: 'A',
            
			link: function ( scope, element, attrs ) {
				//tree id
				var treeId = attrs.treeId;
			
				//tree model
				var treeModel = attrs.treeModel;
                    
				//node id
				var nodeId = attrs.nodeId || 'id';

				//node label
				var nodeLabel = attrs.nodeLabel || 'label';

				//children
				var nodeChildren = attrs.nodeChildren || 'children';

				//tree template
				var template =
					'<ul>' +
						'<li data-ng-repeat="node in ' + treeModel + '">' +
							'<i class="collapsed" data-ng-show="node.' + nodeChildren + '.length && node.collapsed" data-ng-click="' + treeId + '.selectNodeHead(node)"></i>' +
							'<i class="expanded" data-ng-show="node.' + nodeChildren + '.length && !node.collapsed" data-ng-click="' + treeId + '.selectNodeHead(node)"></i>' +
							'<i class="normal" data-ng-hide="node.' + nodeChildren + '.length"></i> ' +
							'<span data-ng-class="node.selected" data-ng-click="' + treeId + '.selectNodeLabel(node)">{{node.' + nodeLabel + '}}</span>' +
							'<div data-ng-hide="node.collapsed" data-tree-id="' + treeId + '" data-tree-model="node.' + nodeChildren + '" data-node-id=' + nodeId + ' data-node-label=' + nodeLabel + ' data-node-children=' + nodeChildren + '></div>' +
						'</li>' +
					'</ul>';


				//check tree id, tree model
				if( treeId && treeModel ) {

					//root node
					if( attrs.angularTreeview ) {
					
						//create tree object if not exists
						scope[treeId] = scope[treeId] || {};

						//if node head clicks,
						scope[treeId].selectNodeHead = scope[treeId].selectNodeHead || function( selectedNode ){

							//Collapse or Expand
							selectedNode.collapsed = !selectedNode.collapsed;
						};

						//if node label clicks,
						scope[treeId].selectNodeLabel = scope[treeId].selectNodeLabel || function( selectedNode ){

							//remove highlight from previous node
							if( scope[treeId].currentNode && scope[treeId].currentNode.selected ) {
								scope[treeId].currentNode.selected = undefined;
							}

							//set highlight to selected node
							selectedNode.selected = 'selected';

							//set currentNode
							scope[treeId].currentNode = selectedNode;
						};
					}

					//Rendering template.
					element.html('').append( $compile( template )( scope ) );
				}   
			}
          // ,templateUrl: 'scripts/directives/templates/tree-model-template.html'
            ,controller: ['$scope', '$element', '$attrs',
            function ($scope, $element, $attrs) {
                $scope.openNode = function(node){
                   //  $('#showDirectoryStructModal').modal();
                    
                    $scope.oldColumns = [ 
                                      {"name":"paramName", "header" : "Наименование", "class":"col-md-2"}
                                    ,{"name":"paramType"
                                      ,"lookup":
                                        {
                                            "table": "rest/types",
                                            "key": "typeId",
                                            "value":"typeKeyname",
                                            "orderBy": {"field": "typeKeyname", "asc": "true"}
                                        }                                      
                                      ,"header" : "Тип", "class":"col-md-2"}
                                    ,{"name":"paramDescription", "header" : "Описание", "class":"col-md-4"}
                        ,{"name":"paramValue", "header" : "Значение", "class":"col-md-1"}
                                    
                    ];
                    
                    var templ = '<div id="edit" class="modal fade" role="dialog">Hello</div>';
                                                
//                            '<div id="editParamsValueModal" class="modal fade" role="dialog"'
//                            +    'aria-labelledby="editParamsValueLabel" tabindex="-1">'
//
//                                +'<div class="modal-dialog  modal-content">'
//
//                                    +'<div class="modal-header">'
//                                        +'<h3 id="editParamsValueLabel">'
//                                            +''+node.Label+''
//                                       +' </h3>'
//                                    +'</div>'
//                                    +'<div class="modal-body">'
//                                        +'<form name="editParamsValueForm" novalidate>'
//
//
//                            
//                                            +'<table class="crud-grid table table-lighter table-bordered table-condensed table-hover">'
//                                                +'<tr>'
//                                                    
//                                                    +'<th ng-repeat="'+'oldColumn in '+$scope.oldColumns+'" ng-class="'+'oldColumn.class'+'" ng-click="">'
//                                                        +'<div>'
//                                                            +'{{oldColumn.header || oldColumn.name}}'+
//                                                            '<i class="glyphicon" ng-class="{\'glyphicon-sort-by-alphabet\': orderBy.asc, \'glyphicon-sort-by-alphabet-alt\': !orderBy.asc}" ng-show="orderBy.field == '+'{{oldColumn.name}}'+'"></i>'
//                                                        +'</div>'
//                                                    +'</th>'
//                                                    +'<th class="col-md-1">' 
//                            
//                                                       +'<i class="btn btn-default glyphicon glyphicon-plus" ng-class="{\'glyphicon-minus\': addParamMode, \'glyphicon-plus\': !addParamMode}" ng-click="'+'toggleAddParamMode()'+'"></i>'
//
//
//                                                    +'</th>'
//                                                +'</tr>'
//                            
//                                                +'<tr ng-show="addParamMode">'
//
//                                                    +'<td ng-repeat="column in oldColumns" ng-switch="!column.lookup" ng-class="column.class">'
//                                                        +'<input ng-switch-default ng-model="object[column.name]" ng-disabled="column.autoincrement" class="form-control"/>'
//                                                        +'<select ng-switch-when="false" ng-options="l[column.lookup.key] as l[column.lookup.value] for l in getLookupData(column.lookup.table) | orderBy: column.lookup.orderBy.field:!column.lookup.orderBy.asc" ng-show="column.lookup" ng-model="object[column.name]" class="form-control"></select>'
//                               +'                     </td>'
//                                                    +'<td class="col-md-2">'
//                                                        +'<div class="btn-toolbar">'
//                                                            +'<div class="btn-group">'
//                                                                +'<i class="btn btn-default btn-sm glyphicon glyphicon-save" ng-click="addObject()"></i>'
//                                                                +'<i class="btn btn-default btn-sm glyphicon glyphicon-remove" ng-click="toggleAddParamMode()"></i>'
//                                                            +'</div>'
//                                                        +'</div>'
//                                                    +'</td>'
//                                                 +'</tr>'
//
//                            
//                            +'                     <tr ng-repeat="oldObject in oldObjects | orderBy:orderBy.field:!orderBy.asc | filter: currentObject.name">'
//                                                     +'<td ng-repeat="oldColumn in oldColumns" ng-switch on="oldObject.editMode">'
//                                                    +'<div ng-switch-default >'
//                                                                +'<div ng-switch on="oldColumn.name">'
//
//                                            
//                                            +'                        <span ng-switch-default>'
//                                                                            +'{{oldObject[oldColumn.name]}}'
//
//                                                                    +'</span>'
//                                                                +'</div>'
//                                                    +'</div>         '
//                                                    +'<div ng-switch-when="true" ng-switch on="!oldColumn.lookup">'
//                                                        +'<input ng-switch-default ng-model="oldObject[oldColumn.name]" model-change-blur ng-disabled="oldColumn.autoincrement" class="form-control" />'
//                                                        +'<select ng-switch-when="false" ng-options="l[oldColumn.lookup.key] as l[oldColumn.lookup.value] for l in getLookupData(oldColumn.lookup.table) | orderBy: oldColumn.lookup.orderBy.field:!oldColumn.lookup.orderBy.asc" ng-model="oldObject[oldColumn.name]" class="form-control"></select>'
//                   +'                                 </div>'
//                                                     +'</td>'
//                                                     +'<td ng-class="col-md-1">'
//                                                         +'<div class="btn-toolbar">'
//                                                                +'<div class="btn-group">'
//                                                                    +'<table>'
//                                                                        +'<tr>'
//                                                                            +'<td ng-show="!oldObject.editMode"><i class="btn btn-default btn-sm glyphicon glyphicon-edit" ng-click="toggleEditMode(oldObject)" ></i> </td>'
//                                                       +'                     <td ng-show="oldObject.editMode"><i class="btn btn-default btn-sm glyphicon glyphicon-save" ng-click="toggleEditMode(oldObject)" ></i> </td>'
//                                                       +'                     <td><i class="btn btn-default btn-sm glyphicon glyphicon-remove" ng-click="setCurObjToDel(oldObject, 1, oldColumns[0].name)" data-target="#deleteObjectModal" data-toggle="modal"></i></td>'
//                                                                        +'</tr>'
//                                                                    +'</table>    '
//                                                                +'</div>    '
//                                                            +'</div>'
//                                                     +'</td>'
//                                                 +'</tr>'
//                                                +'</table>'
//
//                                        +'</form>'
//                                    +'</div>'
//                                    +'<div class="modal-footer">'
//                                            +'<div class="container-fluid dialogMarginButtons">'
//
//                                                +'<div class="row">'
//
//                                                    +'<div class="col-xs-offset-2 col-md-3">'
//                                                        +'<button class="btn btn-default btn-block" data-dismiss="modal" ng-click="exit(\'#currentObject\')">'
//                                                            +'Отменить'
//                                                        +'</button>'
//                                                    +'</div>'
//                                                +'</div>'
//                                            +'</div>		'
//                                    +'</div>'
//                                    
//                                +'</div>'
//                            +'</div>'
//                    ;
                    //Rendering template.
					//$element.html('').append( $compile( templ )( $scope ) );
                   // alert("cur = "+node);
                    $('#editDirValueModal').modal();
                };
                
            }]
            
            
		};
	}]);