'use strict';

/** @jsx React.DOM */

var app = angular.module('portalNMK');

var TableComponentReact = React.createClass( {
	    propTypes: {
		    tableDef: React.PropTypes.object.isRequired,
		    data: React.PropTypes.array.isRequired
		  },

		getTHeadRow : function() {

			if (typeof this.props.tableDef.columns === 'undefined') {
				return <tr><td>INVALID TABLE DEF</td></tr>;
			}

		 	var th = this.props.tableDef.columns.map(function (col, i) {
				var headerClass = col['headerClass'] || "";
				var thKey = col['fieldName'] || ('key' + i);
		 		return <th key={thKey} className={headerClass}>{col['header']}</th>;
		 	});

		 	var headerClassTR = this.props.tableDef.headerClassTR || '';
		 	return <tr className={headerClassTR}>{th}</tr>;
		 },

		getTDRows : function(dataRow) {

			if (typeof this.props.tableDef.columns === 'undefined') {
				return <tr><td>INVALID TABLE DEF</td></tr>;
			}

		 	var tdRows = this.props.tableDef.columns.map(function (col, i) {
				var tdClass = col['dataClass'] || '';
				if (typeof col['fieldName'] === 'undefined') {
					return <td key={'key' + i} className={tdClass}></td>	
				};
				var thKey = col['fieldName'] || ('key' + i);				
				return <td key={col['fieldName']} className={tdClass}>{dataRow[col['fieldName']]}</td>	
		 	});
		 	return <tr>{tdRows}</tr>;
		 },


	   render: function() {

	   		var tableClass = this.props.tableDef.tableClass || '';
	   		var hideHeader = this.props.tableDef.hideHeader || false;	   	

	   		var cbProcess = this.getTDRows;
	   		var dataRows = this.props.data.map(function (row, i) {
	   			var tdRows = cbProcess (row);
	   			return <tr key={i}>{tdRows}</tr>;
	   		});

	   		return <div> 
				<table className={tableClass}> 
				{hideHeader ? '' : <thead>{this.getTHeadRow()}</thead>} 
				<tbody>
					{dataRows}
				</tbody>
				</table>
	   		</div>;
	   }
});

app.value('TableComponentReact', TableComponentReact);
app.directive('tableComponentReact', function(reactDirective) {
    return reactDirective(TableComponentReact);
});

