<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html id="ng-app" ng-app="nmk-p" ng-strict-di>
<head>
<meta charset="UTF-8">
<meta name="referrer" content="origin">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet"
	href="bower_components/bootstrap/dist/css/bootstrap.css" />
<link rel="stylesheet" href="resources/styles/main.css" media="screen">
<title>Rusmetrics</title>

</head>
<body class="excbt_login_body">
<!--[if IE]>
  <p class="text-center">Вы используете браузер, который не поддерживает работу с порталом Rusmetrics. Установите <a href="https://www.google.com/chrome" target="_blank">Chrome</a> или <a href="https://www.firefox.com" target="_blank">Firefox</a>.
<![endif]-->
<!--[if !IE]>-->
	<div id="wrap">
		<div class="container">
<!-- 			<div class="row"> -->
<!-- 				<div class="jumbotron center-block bg-white text-center"> -->
<!-- 					<img src="resources/images/title_bg.jpg"> -->
<!-- 						<i class="btn btn-large glyphicon glyphicon-home"></i> -->
<!-- 				</div> -->
<!-- 			</div> -->
			<div class="row paddingTop10PC" ng-controller="LoginController">
				<div class="well col-md-offset-4 col-xs-4 col-md-4"
				>
					<h1 class = "nmc-login-logo">
                        <%--<b>Вход в Rusmetrics</b>--%>
                        <img src = "resources/images/logo-main-menu-h.png"/>
                    </h1>

					<!-- <form role="form" method="post" action="j_spring_security_check" class="form-horizontal"> -->
					<form role="form" class="form-horizontal">
						<div class="form-group">

							<label for="j_username" class="col-xs-3 control-label">Пользователь: </label>

							<div class="col-xs-9">
								<input name="j_username" id="j_username" type="text"
									class="form-control" placeholder="имя пользователя"
									ng-model = "cred.j_username"/>
							</div>
						</div>
						<div class="form-group">
							<label for="j_password" class="col-xs-3 control-label">Пароль: </label>
							<div class="col-xs-9">
								<input name="j_password" id="j_password" type="password"
									class="form-control" placeholder="пароль"
									ng-model = "cred.j_password"><br /> <input
									type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}" />
							</div>
						</div>
                        <div class="form-group">
                            <label for="remember-me" class="col-xs-3 control-label">Запомнить: </label>
                            <div class="col-xs-9">
                                <input name="remember-me" id="remember-me" type="checkbox" checked
                                ng-model = "cred.rememberMe"><br />
                                <input type="hidden" name="${_csrf.parameterName}"
                                value="${_csrf.token}" />
                            </div>
                        </div>

                        <!-- <div class="form-group">
							<div class="col-xs-12 alert alert-error bg-danger nmc-hide"
								ng-class="{'nmc-hide':!displayLoginError, 'nmc-show':displayLoginError}">
								<p>Неправильное имя пользователя или пароль</p>
							</div>
						</div> -->
						<div class="form-group">
							<div class="col-xs-9 text-success">
								Вход для демонстрационного доступа:<br/>
								Пользователь: demo<br/>
								Пароль: demodemo<br/>
							</div>
							<div class="col-xs-3">
								<button type="submit" name="submit"
									class="btn btn-primary pull-right nmc-bg-color"
									ng-click = "loginFn(cred)">Войти</button>
							</div>

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

				</p>
			</div>
		</div>
	</div>


      <div class="navbar-fixed-bottom row-fluid nmc-main-footer" >
			<p class="muted credit pull-right"><a class="btn btn-default btn-xs" href="./saml/login" role="button">SSO</a>
			<a class="btn btn-default btn-xs" href="./saml/logout" role="button">o</a>
			</p>
      </div>

	<script
		src="bower_components/jquery/dist/jquery.js"></script>
	<script src="bower_components/angular/angular.js"></script>
	<script
		src="bower_components/bootstrap/dist/js/bootstrap.js"></script>

	<!-- Application -->
	<script>
		'use strict';

		var app = angular.module('nmk-p', []);

		app.controller("LoginController", [ "$scope", "$http",
				function($scope, $http) {
					// var url = "" + $location.$$absUrl;
					// $scope.displayLoginError = (url.indexOf("error") >= 0);

                    console.log('Hello, World!!!');
					var url = window.location.pathname.replace("localLogin", "j_spring_security_check");
					$scope.cred = {};
					$scope.cred.rememberMe =  true;

					$scope.loginFn = function (body) {
						body["remember-me"] = body.rememberMe;
						delete body.rememberMe;
						$http({
								method: "POST",
								url: url,
								data: body,
								headers: {'Content-Type': 'application/x-www-form-urlencoded'},
								transformRequest: function(obj) {
							        var str = [];
							        for(var p in obj)
							        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
							        return str.join("&");
							    }
							})
							.then(function () {
								window.location.replace(window.location.pathname.replace("localLogin", ""));
							}, function (error) {
								window.location.replace(window.location.pathname.replace("localLogin", ""));
							});
					};

				} ]);

		function setSeasonBackground(){
			var curDate = new Date(),
				curMonth = curDate.getMonth(),
				curSeason = "title_bg";
			;

			switch(curMonth){
			case 0: curSeason = "January"; break;
			case 1: curSeason = "February"; break;
			case 2: curSeason = "March"; break;
			case 3: curSeason = "April"; break;
			case 4: curSeason = "May"; break;
			case 5: curSeason = "June"; break;
			case 6: curSeason = "July"; break;
			case 7: curSeason = "August"; break;
			case 8: curSeason = "September"; break;
			case 9: curSeason = "October"; break;
			case 10: curSeason = "November"; break;
			case 11: curSeason = "December"; break;
			}

// 			if (curMonth >= 2 && curMonth <= 4){
// 				curSeason = "spring";
// 			}else
// 			if (curMonth >= 5 && curMonth <= 7){
// 				curSeason = "summer";
// 			}else
// 			if (curMonth >= 8 && curMonth <= 10){
// 				curSeason = "autumn";
// 			}else{
// 				curSeason = "title_bg";
// 			}

			document.body.style = "background-image: url(/public/resources/images/" + curSeason + ".png)";
		}

//		setSeasonBackground();

	</script>
<!--<![endif]-->
</body>
</html>
