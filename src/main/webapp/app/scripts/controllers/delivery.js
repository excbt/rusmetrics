var app = angular.module('portalNMK');
app.controller(
		'DlvrCtrl',
		function($scope, $http, notificationFactory){
			/************************************
			 * Определяем глобальные переменные *
			 ************************************/
			// Массивы с данными
			// Список рассылок
			$scope.rep_shdls = [];
			// Переменные с url'ами запросов
			$scope.url_rep_shdls = "../api/reportShedule";
			
			/****************************
			 * Функции для работы с API *
			 ****************************/
			// Получение списка рассылок
			$scope.getReportShedules = function(){
				$http.get($scope.url_rep_shdls)
					.success(function(data){
						$scope.rep_shdls = data;
					})
					.error(function(e){
						notificationFactory.errorInfo(e.statusText,e.description);
					});
			}
			
			 $scope.dlvrSave = function(){
				var slct_rep_type = document.getElementById('slct_rep_tmpl_var');
				var tmpl_id = slct_rep_type[slct_rep_type.selectedIndex].id;
				var paramset_id = slct_rep_type.value;
				// Подготовка данных для сохранения
				var dlvr_data = new Object ();
				dlvr_data['name'] = document.getElementById('inp_dlvr_name').value;
				dlvr_data['description'] = document.getElementById('txtarea_dlvr_descr').value;
				// Формируем расписание
				var slct_period = document.getElementById('slct_period');
				dlvr_data['reportSheduleTypeKey'] = slct_period.value;
				dlvr_data['sheduleTimeTemplate'] = document.getElementById('inp_minute').value + " " + document.getElementById('inp_hour').value;
				switch (slct_period.value) {
				case "SINGLE":
					var day_date = document.getElementById('inp_day_date').value.split(".");
					dlvr_data['sheduleTimeTemplate'] = dlvr_data['sheduleTimeTemplate'] + " " + day_date[0] + " " + day_date[1] + " *";
					break
				case "DAILY":
					dlvr_data['sheduleTimeTemplate'] = dlvr_data['sheduleTimeTemplate'] + " * * *";
					break
				case "WEEKLY":
					dlvr_data['sheduleTimeTemplate'] = dlvr_data['sheduleTimeTemplate'] + " * * " + document.getElementById('slct_day_of_week').value;
					break
				case "MONTHLY":
					dlvr_data['sheduleTimeTemplate'] = dlvr_data['sheduleTimeTemplate'] + " " + document.getElementById('inp_day').value + " * *";
					break
				default:
					alert("Пожалуйста, укажите периодичность отчёта!");
					break
				}
				// Получаем тип рассылки и список получателей
				dlvr_data['sheduleAction1Key'] = document.getElementById('slct_dlvr_type').value;
				dlvr_data['sheduleAction1Param'] = document.getElementById('inp_rcpt_email').value;
				// Получаем дату начала и конца
				dlvr_data['sheduleStartDate'] = document.getElementById('inp_dlvr_begin').value;
				dlvr_data['sheduleEndDate'] = document.getElementById('inp_dlvr_end').value;
				div_edit = document.getElementById('div_edit_delivery');
				if(div_edit.dlvr_id == 'new_dlvr'){
					$http.post(
							'../api/reportShedule/'+'?reportTemplateId=' + tmpl_id + '&reportParamsetId=' + paramset_id,
							dlvr_data)
							.success(function(data){
								//Добавляем новый элемент в массив ...
								addDelivery(data);
								// ... и в таблицу
                                document.getElementById('table_deliveries').innerHTML = '<tr id="tr_dlv_header" class="info">\
                                    <th class="col-md-1">#</th>\
                                    <th class="col-md-2">Название</th>\
                                    <th class="col-md-9">Описание</th>\
                                    </tr>';
                                dlvrTableInit();
                                $('#div_edit_delivery').modal('hide');
								}
							);
				}
				else {
					dlvr_data['id'] = div_edit.dlvr_id;
					dlvr_data['version'] = div_edit.version;
					$http.put(
							'../api/reportShedule/' + dlvr_data['id'] + '?reportTemplateId=' + tmpl_id + '&reportParamsetId=' + paramset_id,
							dlvr_data)
							.success(function(data){
                                document.getElementById('table_deliveries').innerHTML = '<tr id="tr_dlv_header" class="info">\
                                    <th class="col-md-1">#</th>\
                                    <th class="col-md-2">Название</th>\
                                    <th class="col-md-9">Описание</th>\
                                    </tr>';
                                dlvrTableInit();
								$('#div_edit_delivery').modal('hide');
							});
				}
			}
			/*********************
			 * Запускаем функции *
			 *********************/
			$scope.getReportShedules();
		}	
);