<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet"
	href="../../../bower_components/bootstrap/dist/css/bootstrap.css" />
<link rel="stylesheet" href="../../../resources/styles/main.css"
	media="screen">
<title>НМК</title>

</head>
<body>

	<div id="wrap">
		<div class="container">

			<!-- 			<div class="row paddingTop10PC"> -->
			<!-- 				<div class="well col-md-offset-4 col-md-4"> -->

			<h1>Настройка контекста справки</h1>
			<form:form action="" commandName="helpContextForm">
				<div class="row row-eq-height">
					<div class="col-md-2">
						<p style="font-size: 20px">ID:</p>
					</div>

					<div class="col-md-4">
						<b style="font-size: 20px">${helpContext.id}</b> <input
							type="hidden" id="id" name="id" value="${helpContext.id}" />
					</div>
				</div>
				<div class="row row-height">
					<div class="col-md-2">
						<p style="font-size: 20px">Идентификатор:
						<p />
					</div>

					<div class="col-md-4">

						<b class=" text-danger" style="font-size: 20px">${helpContext.anchorId}</b>
						<input type="hidden" id="anchorId" name="anchorId"
							value="${helpContext.anchorId}" />

					</div>
				</div>
				<div class="row row-height">
					<div class="col-md-2">
						<br>
						<p style="font-size: 20px">URL:</p>
					</div>

					<div class="col-md-8">
						<br> <input type="text" class="form-control" id="helpUrl"
							name="helpUrl" value="${helpContext.helpUrl}" />
					</div>
				</div>

				<div class="row row-height">
					<div class="col-md-2"></div>

					<div class="col-md-8">

						<c:if test="${!empty helpContext.helpUrl}">
							<a href="../jmp/${helpContext.anchorId}" class="btn btn-primary">Перейти</a>
						</c:if>


						<input type="submit" value="Записать"
							class="btn btn-default pull-right" />

					</div>
				</div>

			</form:form>
			<!-- 				</div> -->
			<!-- 			</div> -->
		</div>
	</div>
	<script src="../../../bower_components/jquery/dist/jquery.js"></script>
	<script src="../../../bower_components/bootstrap/dist/js/bootstrap.js"></script>

</body>
</html>