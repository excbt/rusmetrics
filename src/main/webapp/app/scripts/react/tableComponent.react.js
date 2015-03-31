'use strict';

/** @jsx React.DOM */

var app = angular.module('portalNMK');

var TableComponentReact = React.createClass( {displayName: "TableComponentReact",
	    propTypes: {
		    tableDef: React.PropTypes.object.isRequired,
		    data: React.PropTypes.array.isRequired
		  },

		getTHeadRow : function() {

			if (typeof this.props.tableDef.columns === 'undefined') {
				return React.createElement("tr", null, React.createElement("td", null, "INVALID TABLE DEF"));
			}

		 	var th = this.props.tableDef.columns.map(function (col, i) {
				var headerClass = col['headerClass'] || "";
				var thKey = col['fieldName'] || ('key' + i);
		 		return React.createElement("th", {key: thKey, className: headerClass}, col['header']);
		 	});

		 	var headerClassTR = this.props.tableDef.headerClassTR || '';
		 	return React.createElement("tr", {className: headerClassTR}, th);
		 },

		getTDRows : function(dataRow) {

			if (typeof this.props.tableDef.columns === 'undefined') {
				return React.createElement("tr", null, React.createElement("td", null, "INVALID TABLE DEF"));
			}

		 	var tdRows = this.props.tableDef.columns.map(function (col, i) {
				var tdClass = col['dataClass'] || '';
				if (typeof col['fieldName'] === 'undefined') {
					return React.createElement("td", {key: 'key' + i, className: tdClass})	
				};
				var thKey = col['fieldName'] || ('key' + i);				
				return React.createElement("td", {key: col['fieldName'], className: tdClass}, dataRow[col['fieldName']])	
		 	});
		 	return React.createElement("tr", null, tdRows);
		 },


	   render: function() {

	   		var tableClass = this.props.tableDef.tableClass || '';
	   		var hideHeader = this.props.tableDef.hideHeader || false;	   	

	   		var cbProcess = this.getTDRows;
	   		var dataRows = this.props.data.map(function (row, i) {
	   			var tdRows = cbProcess (row);
	   			return React.createElement("tr", {key: i}, tdRows);
	   		});

	   		return React.createElement("div", null, 
				React.createElement("table", {className: tableClass}, 
				hideHeader ? '' : React.createElement("thead", null, this.getTHeadRow()), 
				React.createElement("tbody", null, 
					dataRows
				)
				)
	   		);
	   }
});

app.value('TableComponentReact', TableComponentReact);
app.directive('tableComponentReact', function(reactDirective) {
    return reactDirective(TableComponentReact);
});

