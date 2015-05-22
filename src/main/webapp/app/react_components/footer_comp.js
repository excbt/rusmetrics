'use strict';

var FooterComponent = React.createClass({
	propTypes : {
		fname : React.PropTypes.string.isRequired,
		lname : React.PropTypes.string.isRequired
	},

	render : function() {
		return React.DOM.span(null, 'Hello ' + this.props.fname + ' '
				+ this.props.lname);
	}
});

console.log("Self executing");
//app.value('FooterComponent', FooterComponent);
