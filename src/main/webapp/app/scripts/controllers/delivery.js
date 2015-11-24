/*
 * Управление рассылками отчётов
 */
var app = angular.module('portalNMC');
app.controller(
		'DlvrCtrl',
		function($scope, $http, notificationFactory, mainSvc){
			/************************************
			 * Определяем глобальные переменные *
			 ************************************/
			// Массивы с данными
			// Список рассылок
			$scope.rep_shdls = [];
			// Текущий редактируемый список рассылок
			$scope.cur_rep_shdl = {};
			// Списки параметров
			$scope.rep_types = [];
			$scope.rep_act_types = [];
			$scope.rep_shdl_types = [];
			$scope.paramsets = [];
			$scope.dlvr_lists = [];
			// Переменные с url'ами запросов
			$scope.url_rep_shdls = "../api/reportShedule";
			$scope.url_rep_types = "../api/reportSettings/reportType";
			$scope.url_rep_act_types = "../api/reportSettings/reportActionType";
			$scope.url_rep_shdl_types = "../api/reportSettings/reportSheduleType";
			$scope.url_paramsets = "../api/reportParamset";
			$scope.url_dlvr_lists = '../api/subscr/subscrAction/groups';
			// Вспомогательные и временные переменные

			/****************************
			 * Функции для работы с API *
			 ****************************/
                //Обработка error-ответа от сервера
            errorCallback = function(e){
                console.log(e.data);
				notificationFactory.errorInfo(e.statusText,e.description || e.data);
            };
			// Получение списка рассылок
			$scope.getReportShedules = function(){
				$http.get($scope.url_rep_shdls)
					.success(function(data){
						$scope.rep_shdls = data;                    
					})
					.error(errorCallback);
			}
			
			// Добавление/изменение рассылки
			$scope.addReportShedule = function(){
				for(var zx=0; zx < $scope.paramsets.length; zx++){
					if($scope.paramsets[zx].id == $scope.cur_rep_shdl.reportParamset.id){
						var tmpl_id = $scope.paramsets[zx].reportTemplate.id;
					}
				};
//console.log($scope.cur_rep_shdl);
                
				// Проверяем, создаётся новая рассылка или модифицируется имеющаяся
				if($scope.cur_rep_shdl.id == 'new'){
					var url = $scope.url_rep_shdls + '?reportTemplateId=' + tmpl_id + '&reportParamsetId=' + $scope.cur_rep_shdl.reportParamset.id;
					$scope.cur_rep_shdl.id = null;
					$http.post(url, $scope.cur_rep_shdl)
						.success(function(){
                            notificationFactory.success();
							$scope.getReportShedules();
						})
						.error(errorCallback);
				}
				else {
					var url = $scope.url_rep_shdls + '/' + $scope.cur_rep_shdl.id + '?reportTemplateId=' + tmpl_id + '&reportParamsetId=' + $scope.cur_rep_shdl.reportParamset.id;                                     
                    
					$http.put(url, $scope.cur_rep_shdl)
						.success(function(){
                            notificationFactory.success();
							$scope.getReportShedules();
						})
						.error(errorCallback);
					}
			}
			
			// Удаление рассылки
			$scope.delReportShedule = function(rep_shdl){
				var url = $scope.url_rep_shdls + '/' + rep_shdl.id;
				$http.delete(url)
					.success(function(){
                        $('#div_delete_delivery').modal('hide');
                        notificationFactory.success();
						$scope.getReportShedules();
					})
					.error(errorCallback);
			}
			
			// Получение списков параметров рассылок
			$scope.getReportParameters = function (){
				// Получаем список типов отчётов
				$http.get($scope.url_rep_types)
					.success(function(data){
						$scope.rep_types = data;
					})
					.error(errorCallback);

				// Получаем список периодов
				$http.get($scope.url_rep_act_types)
					.success(function(data){
						$scope.rep_act_types = data;
					})
					.error(errorCallback);
				// Получаем список способов доставки
				$http.get($scope.url_rep_shdl_types)
					.success(function(data){
						$scope.rep_shdl_types = data;
					})
					.error(errorCallback);
			}
			
			// Получение списка вариантов шаблонов
			$scope.getParamsets = function(initialFlag){
				// Получаем суффикс выбранного типа отчёта и формируем с ним url
				for(var zx = 0; zx < $scope.rep_types.length; zx++){
					if ($scope.rep_types[zx].keyname == $scope.cur_rep_shdl.reportTemplate.reportTypeKeyname){
						var url = $scope.url_paramsets + $scope.rep_types[zx].suffix;
					}
				};
//console.log(url);
//console.log($scope.cur_rep_shdl);                
//console.log($scope.rep_types);                
				$http.get(url)
					.success(function(data){
						$scope.paramsets = data;
                        if (!initialFlag){
//console.log($scope.paramsets);                            
                            if (angular.isArray($scope.paramsets)&&($scope.paramsets.length>0)){
                                $scope.cur_rep_shdl.reportParamset = $scope.paramsets[0];
                            }else{
                                $scope.cur_rep_shdl.reportParamset = null;
                            };
                        };
					})
					.error(errorCallback);
			}
			
			// Получение списка рассылок
			$scope.getDeliveryLists = function(){
				$http.get($scope.url_dlvr_lists)
					.success(function(data){
						$scope.dlvr_lists = data;
						for(var zx = 0; zx < $scope.dlvr_lists.length; zx++){
							$scope.dlvr_lists[zx].id = $scope.dlvr_lists[zx].id.toString();
						}
					})
					.error(errorCallback);
			}
			
			/************************************
			 * Функции для работы с интерфейсом *
			 ************************************/
            $scope.addNewReportShedule = function(rep_shdl){
                $scope.editReportShedule(rep_shdl);
                $('#div_edit_delivery').modal();
            };
            
			// Подготовка данных для окна редактирования рассылки
			$scope.editReportShedule = function(rep_shdl){
				if(rep_shdl == 'new'){
					// Обнуляем массив
					$scope.cur_rep_shdl = {};
					$scope.cur_rep_shdl.id = 'new';
					// Обнуляем временные переменные
					$scope.time=[];
					$scope.start_date = new Date();
					$scope.end_date=null;
					// Делаем невидимыми ненужные поля
					document.getElementById("div_rcpt_slct").style.display = "none";
					document.getElementById("div_rcpt_email").style.display = "block";
					document.getElementById("div_day_of_month").style.display = "block";
					document.getElementById("div_day_of_week").style.display = "none";
					document.getElementById("div_day_date").style.display = "none";
					document.getElementById("div_daily").style.display = "none";
					// Отключаем зависимые поля
					document.getElementById("inp_day").disabled=true;
					document.getElementById("inp_hour").disabled = true;
					document.getElementById("inp_minute").disabled = true;
					document.getElementById("inp_rcpt_email").disabled = true;
					document.getElementById('slct_rep_tmpl_var').disabled = true;
					// Добавляем время по умолчанию:
					$scope.cur_rep_shdl.sheduleTimeTemplate = '15 0 1 * 1';
				}
				else {
					$scope.cur_rep_shdl = angular.copy(rep_shdl);
					// Получаем список вариантов шаблонов
					$scope.changeReportType(true);
					// Настраиваем способы доставки
					$scope.changeSheduleAction();
					// Настраиваем дату начала и конца действия рассылки
					$scope.activateStartEndDates();
					// Настраиваем время запуска
					$scope.activatePeriod();
				}
			}
			
			// Подготовка данных для списка вариантов шаблона
			$scope.changeReportType = function (initialFlag){
				document.getElementById('slct_rep_tmpl_var').disabled = false;
				$scope.getParamsets(initialFlag);
			}
			
			// Подготовка данных для редактирования способа доставки
			$scope.changeSheduleAction = function(){
				// Выясняем, какой способ доставки выбран
				switch ($scope.cur_rep_shdl.sheduleAction1Key) {
				case "EMAIL_LIST_DELIVERY":
					$scope.getDeliveryLists();
					document.getElementById("div_rcpt_slct").style.display = "block";
					document.getElementById("div_rcpt_email").style.display = "none";
					document.getElementById("slct_rcpt").disabled = false;
					break;
				case "EMAIL_RAW_DELIVERY":
					document.getElementById("div_rcpt_slct").style.display = "none";
					document.getElementById("div_rcpt_email").style.display = "block";
					document.getElementById("inp_rcpt_email").disabled = false;
				break;
				default:
					notificationFactory.errorInfo("Ошибка выбора способа доставки", "Пожалуйста, выберите способ доставки");
				break;
				}

			}
			
			// Подготовка данных для ввода даты начала и конца действия рассылки
			$scope.activateStartEndDates = function (){
				if ($scope.cur_rep_shdl.sheduleStartDate != null) $scope.start_date = new Date($scope.cur_rep_shdl.sheduleStartDate);
				if ($scope.cur_rep_shdl.sheduleEndDate != null) $scope.end_date = new Date($scope.cur_rep_shdl.sheduleEndDate);
			}
			
			// Подготовка данных для редактирования периодичности запуска
			$scope.activatePeriod = function (){
				// Активируем поля ввода времени
				document.getElementById("inp_hour").disabled = false;
				document.getElementById("inp_minute").disabled = false;
				// Разбиваем поле, содержащее расписание рассылки в формате cron на массив
				// И приводим элементы к валидному для форм виду
				$scope.time = $scope.cur_rep_shdl.sheduleTimeTemplate.split(' ');               
				if($scope.time[3] == '*'){
					$scope.time[3] = new Date();
				}
				else {
					var year = new Date();
                    $scope.time[3]-=1;//вычитаем 1 для корректного отображения месяца - js считает месяцы от 0, а нам приходит порядковый номер месяца считая от 1.
					$scope.time[3] = new Date(year.getFullYear(), $scope.time[3], $scope.time[2]);
				}
				// Определяем, какая периодичность выбрана
				switch ($scope.cur_rep_shdl.reportSheduleTypeKey) {
				case "SINGLE":
					document.getElementById("div_day_of_month").style.display = "none";
					document.getElementById("div_day_of_week").style.display = "none";
					document.getElementById("div_day_date").style.display = "block";
					document.getElementById("div_daily").style.display = "none";
					document.getElementById("inp_day_date").disabled = false;
					break;
				case "DAILY":
					document.getElementById("div_day_of_month").style.display = "none";
					document.getElementById("div_day_of_week").style.display = "none";
					document.getElementById("div_day_date").style.display = "none";
					document.getElementById("div_daily").style.display = "block";
					break;
				case "WEEKLY":
					document.getElementById("div_day_of_month").style.display = "none";
					document.getElementById("div_day_of_week").style.display = "block";
					document.getElementById("div_day_date").style.display = "none";
					document.getElementById("div_daily").style.display = "none";
					document.getElementById("slct_day_of_week").disabled=false;
					break;
				case "MONTHLY":
					document.getElementById("div_day_of_month").style.display = "block";
					document.getElementById("div_day_of_week").style.display = "none";
					document.getElementById("div_day_date").style.display = "none";
					document.getElementById("div_daily").style.display = "none";
					document.getElementById("inp_day").disabled=false;
					break;
				default:
					notificationFactory.errorInfo("Ошибка выбора периодичности рассылки", "Пожалуйста, выберите периодичность рассылки");
					break;
				}
			}
			
			// Функция, подготавливающая введённые данные к передаче на сервер
			 $scope.dlvrSave = function(){
				 var is_errors = false;
				 // Проверяем, заполнены ли необходимые поля
				 // Формируем дату старта и дату окончания действия рассылки
				 $scope.cur_rep_shdl.sheduleStartDate = $scope.start_date.getTime();
				 if($scope.end_date != null){
					 var local_end_date = $scope.end_date.getTime();
					 if(local_end_date < $scope.cur_rep_shdl.sheduleStartDate){
						 notificationFactory.errorInfo("Введено неправильное время", "Указанное время окончания действия рассылки меньше, чем время начала");
						 is_errors = true;
					 }
					 else {
						 $scope.cur_rep_shdl.sheduleEndDate = local_end_date;
					 }
				 }
				 else $scope.cur_rep_shdl.sheduleEndDate = null;
				// Проверяем, заполнено ли название рассылки
				 if ($scope.cur_rep_shdl.name == null) {
					 notificationFactory.errorInfo("Не указано название рассылки", "Укажите, пожалуйста, название рассылки");
					 is_errors = true;
				 }
//console.log($scope.cur_rep_shdl.reportTemplate);
//console.log($scope.cur_rep_shdl.reportParamset);                 
				 if (typeof $scope.cur_rep_shdl.reportTemplate == 'undefined'){
					 notificationFactory.errorInfo("Не указан тип отчёта", "Укажите, пожалуйста, тип отчёта");
					 is_errors = true;
				 }
				 else {
					 if((typeof $scope.cur_rep_shdl.reportParamset == 'undefined') || ($scope.cur_rep_shdl.reportParamset==null)){
						 notificationFactory.errorInfo("Не указан вариант шаблона", "Укажите, пожалуйста, вариант шаблона");
						 is_errors = true;
					 }
				 }
				 // Проверяем, указаны ли получатели
//console.log($scope.cur_rep_shdl.sheduleAction1Key);
				 if($scope.cur_rep_shdl.sheduleAction1Key == null){
					 notificationFactory.errorInfo("Не указан способ доставки", "Укажите, пожалуйста, способ доставки");
					 is_errors = true;
				 }
				 else {
					 if($scope.cur_rep_shdl.sheduleAction1Param == null){
						 notificationFactory.errorInfo("Не указаны получатели", "Укажите, пожалуйста, получателей");
						 is_errors = true;
					 }
				 }
				 // Формируем периодичность
				 // Проверяем корректность введённых минут и часов
				 if($scope.time[0] >= 0 && $scope.time[0] < 60){
					 $scope.cur_rep_shdl.sheduleTimeTemplate = $scope.time[0] + " ";
				 	}
				 else {
					 notificationFactory.errorInfo("Время указано неверно", "Укажите, пожалуйста, минуты от 0 до 60");
					 is_errors = true;
					}
					if($scope.time[1] >= 0 && $scope.time[1] < 24){
						$scope.cur_rep_shdl.sheduleTimeTemplate = $scope.cur_rep_shdl.sheduleTimeTemplate + $scope.time[1] + " ";
					}
					else {
						notificationFactory.errorInfo("Время указано неверно", "Укажите, пожалуйста, часы от 0 до 24");
						is_errors = true;
					}
					switch ($scope.cur_rep_shdl.reportSheduleTypeKey) {
					case "SINGLE":
						$scope.cur_rep_shdl.sheduleTimeTemplate = $scope.cur_rep_shdl.sheduleTimeTemplate + $scope.time[3].getDate() + " " + ($scope.time[3].getMonth()+1) + " *";
						break;
					case "DAILY":
						$scope.cur_rep_shdl.sheduleTimeTemplate = $scope.cur_rep_shdl.sheduleTimeTemplate + "* * *";
						break;
					case "WEEKLY":
						if($scope.time[4]<1 || $scope.time[4]>7){
							notificationFactory.errorInfo("Не выбран день недели", "Выберите, пожалуйста, день недели");
							is_errors = true;
						}
						else{
							$scope.cur_rep_shdl.sheduleTimeTemplate = $scope.cur_rep_shdl.sheduleTimeTemplate + "* * " + $scope.time[4];
						}
						break;
					case "MONTHLY":
						if($scope.time[2] > 0 && $scope.time[2] <= 31){
							$scope.cur_rep_shdl.sheduleTimeTemplate = $scope.cur_rep_shdl.sheduleTimeTemplate + $scope.time[2] + " * *";
						}
						break;
					default:
						notificationFactory.errorInfo("Не указана периодичность рассылки", "Выберите, пожалуйста, периодичность рассылки");
						is_errors = true;
						break;
					}
				 // Если всё без ошибок, то
				if (!is_errors){
					// Отправляем данные на сервер
					$scope.addReportShedule();
					// Закрываем окно
					$('#div_edit_delivery').modal('hide');
				}

			}
			/*********************
			 * Запускаем функции *
			 *********************/
			$scope.getReportShedules();
			$scope.getReportParameters();
            
                    //Функции проверки прав доступа пользователя к полям
            $scope.isAdmin = function(){
                return mainSvc.isAdmin();
            };

            $scope.isReadonly = function(){
                return mainSvc.isReadonly();
            };

            $scope.isROfield = function(){
                return ($scope.isReadonly());
            };
		}	
);