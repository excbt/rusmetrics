/*
 * ToDo:
- Сделать удаление списка контактов
- Сделать добавление-удаление связей
- Сделать функцию добавления списка на сервер
- Удалить неиспользуемые стили
 */
	// Переключение вкладок
	function selectContacts () {
		document.getElementById('div_contacts').style.display = 'block';
		document.getElementById('div_contact_lists').style.display = 'none';
	}
	
	function selectLists () {
		document.getElementById('div_contacts').style.display = 'none';
		document.getElementById('div_contact_lists').style.display = 'block';
	}
	
	// Массив с контактами
	var contacts = [
	                [0, "Василий Пупкин", "+74951111111", "pupkin@mail.ru", 1],
	                [1, "Христофор Бонифатич", "+74952222222", "vrungel.h@rogaicopyta.ru", 0],
	                [2, "Фёдор Иванов", "+74953333333", "ivanov.i@peoplefromthestreet.com", 0],
	                [3, "nekto", "+74956666666", "nekto@nekto.ru", 0],
	                [4, "Череззаборногузакидайко", "+74957777777", "cherezzabornoguzakidayko@kontora.ru", 0],
	                [5, "Петров Иннокентий", "+74954444444", "petrov@outlook.com", 0],
	                [6, "Апполинарий Сидоров", "+74955555555", "sidoroff@gmail.com", 0]
	                ];
	var lists = [
	              [0, "Сантехники"],
	              [1, "Электрики"],
	              [2, "Лесники"],
	              [3, "Бухгалтеры"],
	              [4, "Люди с улицы"],
	              [5, "Ассенизаторы"],
	              [6, "Жители"],
	              [7, "Какие-то мутные персонажи"]
	              ];
	
	// 0 - id
	// 1 - контакт
	// 2 - группа
	var cnt_lst = [
	               [0, 1, 0],
	               [1, 1, 1],
	               [2, 2, 3],
	               [3, 3, 2],
	               [3, 3, 1],
	               ];
	
	/*****************
	/* Функции для работы с сервером
	******************/
	
	// Функция для получения контактов с сервера
	// Параметры: id - id первого контакта
	//				num - общее количество контактов
	//				если id=all, то возвращаются все контакты
	//				в любом другом случае возвращается контакт с id = id
	function getContacts (id) {
		var localCnt = [];
		switch(id) {
		case 'all':
			$.ajax({
				type: 'GET',
				url: '../api/subscr/subscrAction/users',
				dataType: 'json',
				async: false,
				success: function(data){
					localCnt = data;
				}
			});
			break
		default:
			$.ajax({
				type: 'GET',
				url: '../api/subscr/subscrAction/users' + "/" + id,
				dataType: 'json',
				async: false,
				success: function(data){
					localCnt = data;
				}
			});
		}
	return localCnt;
	}
	
	// Функция для получения списков контактов с сервера
	function getContactLists (id) {
		var local_lst = [];
		switch(id) {
		case 'all':
			$.ajax({
				type: 'GET',
				url: '../api/subscr/subscrAction/groups',
				dataType: 'json',
				async: false,
				success: function(data){
					local_lst = data;
				}
			});
			break
		default:
			$.ajax({
				type: 'GET',
				url: '../api/subscr/subscrAction/groups' + '/' + id,
				dataType: 'json',
				async: false,
				success: function(data){
					local_lst = data;
				}
			});
		}
		return local_lst;
	}

	// Функция удаления контакта с сервера
	function delContacts(type, id){
		var result;
		if (type == 'contact'){
			$.ajax({
				type: 'DELETE',
				url: '../api/subscr/subscrAction/users' + '/' + id,
				dataType: 'json',
				async: true,
				success: function(){
					result = 0;
				}
			});
		}
		else {
			$.ajax({
				type: 'DELETE',
				url: '../api/subscr/subscrAction/groups' + '/' + id,
				dataType: 'json',
				async: true,
				success: function(){
					result = 0;
				}
			});
		}
		return 0;
	}
	
	// Функция добавления контакта на сервер
	function addContact(cnt_data, cnt_lst){
		var result = new Object();
		// Проверяем, создаётся новый контакт или редактируется имеющийся
		if(cnt_data['id'] == 'new_cnt'){
			cnt_data['id'] = null;
			$.ajax({
				type: 'POST',
				contentType: "application/json",
				url: '../api/subscr/subscrAction/users' + '?' + 'subscrGroupIds=' + cnt_lst,
				dataType: 'json',
				async: false,
				success: function(data){
					result = data;
				},
				data: JSON.stringify(cnt_data)
			});
		}
		else {
			$.ajax({
				type: 'PUT',
				contentType: "application/json",
				url: '../api/subscr/subscrAction/users' + '/' + cnt_data['id'] + '?' + 'subscrGroupIds=' + cnt_lst,
				dataType: 'json',
				async: false,
				success: function(data){
					result = data;
				},
				data: JSON.stringify(cnt_data)
			});
		}
		return result;
	}

	// Функция добавления списка на сервер
	function addList(lst_data, lst_cnt){
		var result = new Object();
		// Проверяем, создаётся новый контакт или редактируется имеющийся
		if(lst_data['id'] == 'new_lst'){
			lst_data['id'] = null;
			$.ajax({
				type: 'POST',
				contentType: "application/json",
				url: '../api/subscr/subscrAction/groups' + '?' + 'subscrUserIds=' + lst_cnt,
				dataType: 'json',
				async: false,
				success: function(data){
					result = data;
				},
				data: JSON.stringify(lst_data)
			});
		}
		else {
			alert(lst_data['version']);
			$.ajax({
				type: 'PUT',
				contentType: "application/json",
				url: '../api/subscr/subscrAction/groups' + '/' + lst_data['id'] + '?' + 'subscrUserIds=' + lst_cnt,
				dataType: 'json',
				async: false,
				success: function(data){
					result = data;
				},
				data: JSON.stringify(lst_data)
			});
		}
		return result;
	}

	// Функция получения связей контакт-список
	// type: "group" или "contact".
	function getCntLst(type, id){
		var cnt_list_local = [];
		if(type == 'contact'){
			$.ajax({
				type: 'GET',
				url: '../api/subscr/subscrAction/users' + '/' + id + '/' + 'groups',
				dataType: 'json',
				async: false,
				success: function(data){
					cnt_list_local = data;
				}
			});
		}
		else {
			$.ajax({
				type: 'GET',
				url: '../api/subscr/subscrAction/groups' + '/' + id + '/' + 'users',
				dataType: 'json',
				async: false,
				success: function(data){
					cnt_list_local = data;
				}
			});
		}
		return cnt_list_local
	}
	
	/************************************
	 * Функции для работы с интерфейсом *
	 ************************************/
	
	// Функция обновления контакта
	// Функция добавления нового контакта
	function cntEdit() {
		var cnt_local = new Object();
		var cnt_list_local = [];
		// Получаем ID объекта из свойств DIV'а
		var wnd_edit_contact = document.getElementById("edit_contact");
		cnt_local['id'] = wnd_edit_contact.cnt_id;
		cnt_local['version'] = wnd_edit_contact.ver;
		// Присваиваем имя, телефон, емейл
		cnt_local['userName'] = document.getElementById('inp_cnt_name').value;
		cnt_local['userPhone'] = document.getElementById('inp_cnt_tel').value;
		cnt_local['userEmail'] = document.getElementById('inp_cnt_email').value;
		// Проверяем, заполнены ли обязательные поля
		if(cnt_local['userName']=='') {
			alert("Пожалуйста, заполните поле \"Имя\". Оно не должно быть пустым.");
			return 0;
		}
		// Проверяем, у каких списков стоят чекбоксы и добавляем связи
		var tmp_chkbx = [];
		var table_list = document.getElementById("table_lists_in_window");
		for(var zxa = 0; zxa < table_list.rows.length; zxa++){
			if(table_list.rows[zxa].cells[0].childNodes[0].checked){
				cnt_list_local.push(table_list.rows[zxa].cells[0].childNodes[0].id.split("_")[2]);
			}
		}
		// Отправляем данные контакта на сервер
		cnt_local = addContact(cnt_local, cnt_list_local);
		// Перестраиваем таблицу с контактами
		var newRow = document.getElementById("cnt_id_" + cnt_local['id']);
		// Если row с таким id уже есть, то меняем содержимое его cell'ов.
		// Если нет - то создаём новый
		if(!newRow){
			newRow =  document.getElementById('table_contacts').insertRow();
			newRow.id = "cnt_id_" + cnt_local['id'];
			newRow.insertCell().innerHTML = "<i title='Удалить' class='btn btn-default btn-xs glyphicon glyphicon-trash' onclick = delCntTd(" + cnt_local['id'] + ")></i> <i title='Изменить' class='btn btn-default btn-xs glyphicon glyphicon-edit' onclick=cntEditWindow(" + cnt_local['id'] + ") data-target='#edit_contact' data-toggle='modal'></i>";
			newRow.insertCell().innerHTML = cnt_local['userName'];
			newRow.insertCell().innerHTML = cnt_local['userPhone'];
			newRow.insertCell().innerHTML = cnt_local['userEmail'];
		}
		else {
			newRow.cells[1].innerHTML = cnt_local['userName'];
			newRow.cells[2].innerHTML = cnt_local['userPhone'];
			newRow.cells[3].innerHTML = cnt_local['userEmail'];
		}
		// Закрываем окно редактирования контакта
		$('#edit_contact').modal('hide');
	}
	
	// Функция обновления списка контактов
	// Функция добавления нового списка контактов
	function lstEdit() {
		var lst_local = new Object();
		var cnt_list_local = [];
		// Получаем ID и version объекта из свойств DIV'а
		var wnd_edit_list = document.getElementById("edit_list");
		lst_local['id'] = wnd_edit_list.lst_id;
		lst_local['version'] = wnd_edit_list.ver;
		// Присваиваем имя, телефон, емейл
		lst_local['groupName'] = document.getElementById('inp_lst_name').value;
		// Проверяем, заполнены ли обязательные поля
		if(lst_local['groupName']=='') {
			alert("Пожалуйста, заполните поле \"Имя\". Оно не должно быть пустым.");
			return 0;
		}
		// Проверяем, у каких списков стоят чекбоксы и добавляем связи
		var table_list = document.getElementById("table_contacts_in_window");
		for(var zxa = 0; zxa < table_list.rows.length; zxa++){
			if(table_list.rows[zxa].cells[0].childNodes[0].checked){
				cnt_list_local.push(table_list.rows[zxa].cells[0].childNodes[0].id.split("_")[2]);
			}
		}
		// Отправляем данные контакта на сервер
		lst_local = addList(lst_local, cnt_list_local);
		// Перестраиваем таблицу с контактами
		var newRow = document.getElementById("lst_id_" + lst_local['id']);
		// Если row с таким id уже есть, то меняем содержимое его cell'ов.
		// Если нет - то создаём новый
		if(!newRow){
			newRow =  document.getElementById('table_contact_lists').insertRow();
			newRow.id = "lst_id_" + lst_local['id'];
			newRow.insertCell().innerHTML = "<i title='Удалить' class='btn btn-default btn-xs glyphicon glyphicon-trash' onclick = delLstTd(" + lst_local['id'] + ")></i> <i title='Изменить' class='btn btn-default btn-xs glyphicon glyphicon-edit' onclick=lstEditWindow(" + lst_local['id'] + ") data-target='#edit_list' data-toggle='modal'></i>";
			newRow.insertCell().innerHTML = lst_local['groupName'];
		}
		else {
			newRow.cells[1].innerHTML = lst_local['groupName'];
		}
		// Закрываем окно редактирования контакта
		$('#edit_list').modal('hide');
	}

	// Функция инициализации таблицы контактов
	function cntTableInit () {
		// Получаем контакты с сервера
		var cntForTable = getContacts("all");
		var cntTable = document.getElementById('table_contacts');
		// Заполняем таблицу
		var newRow;
		for (var zx = 0; zx < cntForTable.length; zx++) {
			newRow = cntTable.insertRow();
			newRow.id = "cnt_id_" + cntForTable[zx]['id'];
			newRow.insertCell().innerHTML = "<i title='Удалить'\
												class='btn btn-default btn-xs glyphicon glyphicon-trash' \
												onclick = delCntTd(" + cntForTable[zx]['id'] + ")></i> \
											<i title='Изменить' \
												class='btn btn-default btn-xs glyphicon glyphicon-edit' \
												onclick=cntEditWindow(" + cntForTable[zx]['id'] + ") \
												data-target='#edit_contact' \
												data-toggle='modal'></i>";
			newRow.insertCell().innerHTML = cntForTable[zx]['userName'];
			newRow.insertCell().innerHTML = cntForTable[zx]['userPhone'];
			newRow.insertCell().innerHTML = cntForTable[zx]['userEmail'];
		}
	}
	
	// Инициализация таблицы списков контактов
	function cntListTableInit() {
		// Получаем списки контактов с сервера
		var cntlForTable = getContactLists('all');
		var cntlTable = document.getElementById('table_contact_lists');
		var newRow;
		for (var zx = 0; zx < cntlForTable.length; zx++){
			newRow = cntlTable.insertRow();
			newRow.id = "lst_id_" + cntlForTable[zx]['id'];
			newRow.insertCell().innerHTML = "<i title='Удалить' \
												class='btn btn-default btn-xs glyphicon glyphicon-trash' \
												onclick = delLstTd(" + cntlForTable[zx]['id'] + ")></i> \
											<i title='Изменить' \
												class='btn btn-default btn-xs glyphicon glyphicon-edit' \
												onclick=lstEditWindow(" + cntlForTable[zx]['id'] + ") \
												data-target='#edit_list' \
												data-toggle='modal'></i>";
			newRow.insertCell().innerHTML=cntlForTable[zx]['groupName'];
		}
	}
	
	// Удаление контакта
	function delCntTd(id){
		delContacts('contact', id);
		var cnt_td = document.getElementById("cnt_id_" + id);
		cnt_td.parentNode.removeChild(cnt_td);
	}
	
	// Удаление группы
	function delLstTd(id){
		delContacts('list', id);
		var lst_td = document.getElementById("lst_id_" + id);
		lst_td.parentNode.removeChild(lst_td);

	}
	// Функция подготовки таблицы в окне редактирования свойств контакта или группы
	// Параметры: type - "list" или "contact" - указать, кого редактируем, а не того, кого показываем.
	// id - id группы-контакта или "clear"
	function cntTablesInWindow(type, id){
		var ids_links = [];
		var cntlForTable = [];
		var newRow;
		// Получаем связи контакт-список
		if (id != 'new_lst' && id != 'new_cnt') ids_links = getCntLst(type, id);
		// Получаем списки контактов с сервера
		if (type == 'list'){
			var cntlForTable = getContacts("all");
			var cntlTable = document.getElementById('table_contacts_in_window');
			// Сначала чистим таблицу
			cntlTable.innerHTML = '';
			for (var zx = 0; zx < cntlForTable.length; zx++){
				newRow = cntlTable.insertRow();
				newRow.insertCell().innerHTML = "<input type='checkbox' id='chkbx_id_"+ cntlForTable[zx]['id'] +"' />";
				newRow.insertCell().innerHTML=cntlForTable[zx]['userName'];
			}
		}
		else {
			var cntlForTable = getContactLists('all');
			var cntlTable = document.getElementById('table_lists_in_window');
			cntlTable.innerHTML = '';
			for (var zx = 0; zx < cntlForTable.length; zx++){
				newRow = cntlTable.insertRow();
				newRow.insertCell().innerHTML = "<input type='checkbox' id='chkbx_id_"+ cntlForTable[zx]['id'] +"' />";
				newRow.insertCell().innerHTML=cntlForTable[zx]['groupName'];
			}
		}
		// Ставим галочки в чекбоксах
		for (var zxa = 0; zxa < ids_links.length; zxa++){
			document.getElementById('chkbx_id_' + ids_links[zxa]['id']).checked=true;
		}
	}
	
	// Функция подготовки окна редактирования контакта
	// для создания нового контакта надо передать в id значение "new_cnt"
	function cntEditWindow(id){
		// Передаём id редактируемого объекта в свойство div'а с редактором
		var wnd_edit_contact = document.getElementById("edit_contact")
		wnd_edit_contact.cnt_id = id;
		var local_cnt = new Array();
		// Если это не новый контакт, то получаем данные контакта по ид
		// если новый - то заполняем поля пустыми данными и изменяем заголовок окна
		if(id != 'new_cnt'){
			local_cnt = getContacts(id);
			document.getElementById("cnt_edit_w_header").innerHTML="Изменение контакта";
			wnd_edit_contact.ver = local_cnt['version'];
		}
		else {
			for (var zx=0; zx<=3; zx++){
				local_cnt['userName'] = '';
				local_cnt['userPhone'] = '';
				local_cnt['userEmail'] = '';
				}
			document.getElementById("cnt_edit_w_header").innerHTML="Создание контакта";
		}
		// Заполняем поля форм данными контакта
		document.getElementById('inp_cnt_name').value = local_cnt['userName'];
		document.getElementById('inp_cnt_tel').value = local_cnt['userPhone'];
		document.getElementById('inp_cnt_email').value = local_cnt['userEmail'];
		cntTablesInWindow('contact', id);
	}

	// Функция подготовки окна редактирования списка контактов
	function lstEditWindow(id){
		// Передаём id редактируемого объекта в свойство div'а с редактором
		var wnd_edit_list = document.getElementById("edit_list");
		wnd_edit_list.lst_id = id;
		var local_lst = new Array();
		// Если это не новый список, то получаем данные списка по ид
		// если новый - то заполняем поля пустыми данными и изменяем заголовок окна
		if(id != 'new_lst'){
			local_lst = getContactLists(id);
			document.getElementById('inp_lst_name').value = local_lst['groupName'];
			document.getElementById("lst_edit_w_header").innerHTML="Изменение списка контактов";
			wnd_edit_list.ver = local_lst['version'];
		}
		else {
			local_lst['groupName'] = '';
			document.getElementById("lst_edit_w_header").innerHTML="Создание списка контактов";
		}
		// Заполняем поля форм данными контакта
		document.getElementById('inp_lst_name').value = local_lst['groupName'];
		cntTablesInWindow('list', id);
	}
	
	// Запускаем функции
	cntTableInit();
	cntListTableInit();