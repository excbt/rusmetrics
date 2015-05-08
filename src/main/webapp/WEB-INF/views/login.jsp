<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html id="ng-app" ng-app="nmk-p" ng-strict-di>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet"
	href="bower_components/bootstrap/dist/css/bootstrap.css" />
<link rel="stylesheet" href="resources/styles/main.css" media="screen">
<title>НМК</title>

</head>
<body class="excbt_login_body">

	<div id="wrap">
		<div class="container">
<!-- 			<div class="row"> -->
<!-- 				<div class="jumbotron center-block bg-white text-center"> -->
<!-- 					<img src="resources/images/title_bg.jpg"> -->
<!-- 						<i class="btn btn-large glyphicon glyphicon-home"></i> -->
<!-- 				</div> -->
<!-- 			</div> -->
			<div class="row" ng-controller="LoginController">
				<div class="well col-md-offset-4 col-md-4">
					<h3>НМК Портал</h3>

					<form role="form" method="post" action="j_spring_security_check">
						<div>
							<input name="j_username" id="j_username" type="text"
								class="form-control" placeholder="имя пользователя"><br />
							<input name="j_password" id="j_password" type="password"
								class="form-control" placeholder="пароль"><br /> <input
								type="hidden" name="${_csrf.parameterName}"
								value="${_csrf.token}" />
							<div class="alert alert-error bg-danger"
								ng-show="displayLoginError">
								<p>Неправильное имя пользователя или пароль</p>
							</div>

							<button type="submit" name="submit"
								class="btn btn-primary pull-right">Войти</button>


						</div>
					</form>
				</div>
			</div>
		</div>
	</div>

	<div id="footer">
		<div class="container">
			<div class="text-left">
				<p>
<!-- 					<b>LOGO</b> -->
				</p>
			</div>
		</div>
	</div>


	<script
		src="bower_components/jquery/dist/jquery.js"></script>
	<script src="bower_components/angular/angular.js"></script>
	<script
		src="bower_components/bootstrap/dist/js/bootstrap.js"></script>
	<script
		src="bower_components/angular-animate/angular-animate.js"></script>
	<script
		src="bower_components/angular-cookies/angular-cookies.js"></script>
	<script
		src="bower_components/angular-resource/angular-resource.js"></script>
	<script
		src="bower_components/angular-route/angular-route.js"></script>
	<script
		src="bower_components/angular-sanitize/angular-sanitize.js"></script>
	<script
		src="bower_components/angular-touch/angular-touch.js"></script>

	<!-- Application -->
	<script>
		'use strict';

		var app = angular.module('nmk-p', []);

		app.controller("LoginController", [ "$scope", "$location",
				function($scope, $location) {
					var url = "" + $location.$$absUrl;
					$scope.displayLoginError = (url.indexOf("error") >= 0);

				} ]);
	</script>
</body>
</html>