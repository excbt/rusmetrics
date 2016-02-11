var app = angular.module('portalNMC');
app.controller(
		'ContactsCtrl',
		function($rootScope, $scope, $http, mainSvc, notificationFactory){
            $rootScope.ctxId = "contacts_page";
			/*************************
			 * Определяем переменные *
			 *************************/
			// Массивы с полной инфой о контактах и списках
			$scope.contacts = [];
			$scope.lists = [];
			// Массив с текущим редактируемым элементом
			$scope.currentItem = {};
			// Массив с взаимосвязями группа-контакт
			$scope.cnt_lst = [];
			// Массивы для построения таблиц в списках с чекбоксами
			$scope.small_lists = [];
			$scope.small_contacts = [];
			// Переменные с url'ами запросов
			$scope.url_users = '../api/subscr/subscrAction/users';
			$scope.url_groups = '../api/subscr/subscrAction/groups';
				
			/****************************
			 * Функции для работы с API *
			 ****************************/
            var errorCallback = function (e) {
                console.log(e);
                var errorCode = "-1";
                if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || !mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode))){
                    errorCode = e.resultCode || e.data.resultCode;
                };
                var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
                notificationFactory.errorInfo(errorObj.caption, errorObj.description);
        //        notificationFactory.errorInfo(e.statusText,e.data.description);       
            };
			
			// Получение контактов с сервера
			$scope.getContacts = function (){
				$.ajax({
					type: 'GET',
					url: $scope.url_users,
					dataType: 'json',
					async: false,
					success: function(data){
						$scope.contacts = data;
					}
				});
			}
			
			// Получение списков контактов с сервера
			$scope.getLists = function (){
				$.ajax({
					type: 'GET',
					url: $scope.url_groups,
					dataType: 'json',
					async: false,
					success: function(data){
						$scope.lists = data;
					}
				});
			}
			
			// Добавление контакта на сервер
			$scope.addContact = function (){
                if (!$scope.currentItem.userName || $scope.currentItem.userName == ''){
                    notificationFactory.errorInfo("Ошибка", "Не задано имя контакта. Заполните поле 'Имя'.");
                    return "Add / edit contact: no name";
                };
				// Собираем отмеченные чекбоксы в списке групп
				var table_list = document.getElementById("table_lists_in_window");
				$scope.cnt_lst = [];
				for(var zxa = 0; zxa < table_list.rows.length; zxa++){
					//console.log(table_list.rows[zxa].cells[0].childNodes[1]);
					if(table_list.rows[zxa].cells[0].childNodes[1].checked){
						$scope.cnt_lst.push(table_list.rows[zxa].cells[0].childNodes[1].id);
					}
				}
				// Если объект уже существует - делаем put
				if(typeof $scope.currentItem.id !== 'undefined'){
					var url = $scope.url_users + '/' + $scope.currentItem.id + '?subscrGroupIds=' + $scope.cnt_lst;
					$http.put(url, $scope.currentItem)
					.success(function(){
						$scope.getContacts();
						$('#edit_contact').modal('hide');
					})
					.error(errorCallback);
				}
				// Если не существует - делаем post
				else {
					var url = $scope.url_users + '?subscrGroupIds=' + $scope.cnt_lst;
					$http.post(url, $scope.currentItem)
					.success(function(){
						$scope.getContacts();
						$('#edit_contact').modal('hide');
					})
					.error(errorCallback);
				}
			}
			
			// Добавление списка на сервер
			$scope.addList = function (){
				// Собираем отмеченные чекбоксы в списке групп
				var table_contacts = document.getElementById("table_contacts_in_window");
				$scope.cnt_lst = [];
				for(var zxa = 0; zxa < table_contacts.rows.length; zxa++){
					if(table_contacts.rows[zxa].cells[0].childNodes[1].checked){
						$scope.cnt_lst.push(table_contacts.rows[zxa].cells[0].childNodes[1].id);
					}
				}
				// Если объект уже существует - делаем put
				if(typeof $scope.currentItem.id !== 'undefined'){
					var url = $scope.url_groups + '/' + $scope.currentItem.id  + '?subscrUserIds=' + $scope.cnt_lst;
					$http.put(url, $scope.currentItem)
					.success(function(){
						$scope.getLists();
						$('#edit_list').modal('hide');
					})
					.error(errorCallback);
				}
				// Если не существует - делаем post
				else {
					var url = $scope.url_groups + '?subscrUserIds=' + $scope.cnt_lst
					$http.post(url, $scope.currentItem)
					.success(function(){
						$scope.getLists();
						$('#edit_list').modal('hide');
					})
					.error(errorCallback);
				}
			}

			// Удаление контакта
			$scope.delContact = function (contact){
				/*
				 * Добавить окно с подтверждалкой
				 */
				var url = $scope.url_users + '/' + contact.id;
				$http.delete(url).
					success(function(){
                        $('#div_delete_contact').modal('hide');
						$scope.getContacts();
					}).
					error(errorCallback);
			}
			
			// Удаление списка контактов
			$scope.delList = function (list){
				/*
				 * Добавить окно с подтверждалкой
				 */
				var url = $scope.url_groups + '/' + list.id;
				$http.delete(url)
					.success(function(){
                        $('#div_delete_contact_list').modal('hide');
						$scope.getLists();
					})
					.error(errorCallback);
			}
			
			// Получение списка связей контакт-группа
			// type - для кого получаем связи: contact или group
			$scope.getCntLst = function (type, item){
				// Сначала обнулим массив
				$scope.cnt_lst = [];
				// Проверяем, создаётся ли новый элемент или редактируется старый
				if(item!='new'){
					// Если надо получить список для контакта, то формируем один урл
					if(type == 'contact'){
						var url = $scope.url_users + '/' + item.id + '/groups';					
					}
					// Если для группы - то другой
					else {
						var url = $scope.url_groups + '/' + item.id + '/users';		
					}
					// Запрос на сервер
					$http.get(url)
					// В случае успеха запускаем функцию, которая, в числе прочего, формирует список с чекбоксами
						.success(function(data){
							for(var zx=0; zx < data.length; zx++){
								$scope.cnt_lst.push(data[zx].id);
							}
							// Для редактора списков делаем таблицу с контактами, для редактора контактов - наоборот
							if (type == 'list'){
								$scope.small_contacts = $scope.contacts;
								for (var zx = 0; zx < $scope.contacts.length; zx++){
									for (var zxa = 0; zxa < $scope.cnt_lst.length; zxa++){
										if($scope.small_contacts[zx].id == $scope.cnt_lst[zxa]) $scope.small_contacts[zx].checked = true;
									}
								}
							}
							else {
								$scope.small_lists = $scope.lists;
								for (var zx = 0; zx < $scope.lists.length; zx++){
									$scope.small_lists[zx].checked = false;
									for (var zxa = 0; zxa < $scope.cnt_lst.length; zxa++){
										if($scope.small_lists[zx].id == $scope.cnt_lst[zxa]) $scope.small_lists[zx].checked = true;
									}
								}
							}
						})
						.error(errorCallback);
				}
				else {
					if(type == 'contact'){
						$scope.small_lists = $scope.lists;
						for (var zxb = 0; zxb < $scope.small_lists.length; zxb++){
							$scope.small_lists[zxb].checked = false;
						}
					}
					else {
						$scope.small_contacts = $scope.contacts;
						for (var zxc = 0; zxc < $scope.small_contacts.length; zxc++){
							$scope.small_contacts[zxc].checked = false;
						}
					}
				}
			}
			
			/***************************
			 * Вспомогательные функции *
			 ***************************/
            // Добавление нового элемента
            $scope.addItem = function(item, type){
                $scope.editItem(item, type);
                if(type == 'contact'){
                    $('#edit_contact').modal();
                }else if(type == 'list'){
                    $('#edit_list').modal();
                };
			}
            
			// Готовим элемент для окна редактирования контакта/группы
			// type - тип редактируемого элемента (contact или list)
			$scope.editItem = function(item, type){
				// Если создаётся новый элемент - обнуляем объект и список связей контакт-группа
				if (item == 'new'){
					$scope.currentItem = {};
				}
				else {
					$scope.currentItem = item;
				}
				$scope.getCntLst(type, item);
			}
			
			/*********************
			 * Запускаем функции *
			 *********************/
			$scope.getContacts();
			$scope.getLists();
            
                        //check user rights
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
