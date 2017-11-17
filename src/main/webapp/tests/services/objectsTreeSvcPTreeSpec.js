xdescribe("Portal NMC, objects tree servce tests", function () {
	var USER_INFO = {
			userName: "unitTestUser",
			subscriber: {
				subscriberName: "unitTestSubscriber"
			}
		},
		EMPTY_OBJECT = {},
		TEST_CERT_SUBSCR = "TEST_CERTIFICATE",

		USER_INFO_URL = "../api/systemInfo/fullUserInfo",
		PERMISSIONS_URL = "../api/subscr/manage/service/permissions",

		TEST_PERMISSIONS = [
			{
				keyname: "WEB_ALLOW_OBJECT_LIST_VIEW_PAGE",
				permissionTagId: "object_list_view_page",
				priority: 100
			},
			{
				keyname: "WEB_ALLOW_OBJECT_MAIN_MENU_ITEM",
				permissionTagId: "object_main_menu_item",
				priority: 100
			}
		],
		TEST_P_TREE1 = {
		  "_id": 128642059,
		  "nodeType": "ELEMENT",
		  "nodeName": "aaaassss",
		  "childNodes": [
		    {
		      "_id": 129672526,
		      "nodeType": "ELEMENT",
		      "nodeName": "Жилой дом 2",
		      "linkedNodeObjects": [
		        {
		          "nodeType": "CONT_OBJECT",
		          "nodeName": null,
		          "nodeObject": {
		            "id": 129281860,
		            "name": null,
		            "fullName": "Квартира с распределителем",
		            "fullAddress": "г Москва, Волоколамское шоссе, д 1 стр 1",
		            "number": null,
		            "owner": null,
		            "ownerContacts": null,
		            "currentSettingMode": "summer",
		            "settingModeMDate": null,
		            "description": "AK",
		            "cwTemp": 50.0,
		            "heatArea": 200.0,
		            "version": 0,
		            "timezoneDefKeyname": "MSK",
		            "exSystemKeyname": null,
		            "numOfStories": null
		          },
		          "childNodes": [
		            {
		              "nodeType": "CONT_ZPOINT",
		              "nodeName": null,
		              "nodeObject": {
		                "id": 129281872,
		                "contObjectId": 129281860,
		                "contServiceTypeKeyname": "heat",
		                "customServiceName": "Система отопления",
		                "startDate": 1496121666982,
		                "endDate": null,
		                "contZPointComment": null,
		                "version": 4
		              },
		              "childNodes": [
		                {
		                  "nodeType": "DEVICE_OBJECT",
		                  "nodeName": null,
		                  "nodeObject": {
		                    "id": 129281867,
		                    "deviceModelId": 129265814,
		                    "number": "AK-1705",
		                    "exCode": null,
		                    "exLabel": null,
		                    "exSystemKeyname": null,
		                    "version": 6,
		                    "isDeviceObjectMetadata": null,
		                    "isManual": true,
		                    "verificationInterval": null,
		                    "verificationDate": null,
		                    "metaVersion": 1,
		                    "deviceLoginInfo": null,
		                    "instType": "P",
		                    "deviceObjectName": "4444",
		                    "activeDeviceObject": true
		                  },
		                  "_id": null,
		                  "linkedNodeObjects": [],
		                  "links": {
		                    "empty": true
		                  }
		                }
		              ],
		              "_id": null,
		              "linkedNodeObjects": [],
		              "links": {
		                "empty": true
		              }
		            }
		          ],
		          "_id": null,
		          "linkedNodeObjects": [],
		          "links": {
		            "empty": true
		          }
		        },
		        {
		          "nodeType": "CONT_OBJECT",
		          "nodeName": null,
		          "nodeObject": {
		            "id": 128870763,
		            "name": null,
		            "fullName": "демо, Тест объект",
		            "fullAddress": null,
		            "number": null,
		            "owner": null,
		            "ownerContacts": null,
		            "currentSettingMode": "summer",
		            "settingModeMDate": null,
		            "description": null,
		            "cwTemp": null,
		            "heatArea": null,
		            "version": 1,
		            "timezoneDefKeyname": "MSK",            
		            "numOfStories": null
		          },
		          "childNodes": [
		            {
		              "nodeType": "CONT_ZPOINT",
		              "nodeName": null,
		              "nodeObject": {
		                "id": 128870810,
		                "contObjectId": 128870763,
		                "contServiceTypeKeyname": "heat",
		                "customServiceName": "Система отопления",
		                "startDate": 1487152251892,
		                "endDate": null,
		                "contZPointComment": null,
		                "version": 0
		              },
		              "childNodes": [
		                {
		                  "nodeType": "DEVICE_OBJECT",
		                  "nodeName": null,
		                  "nodeObject": {
		                    "id": 128870789,
		                    "deviceModelId": 29779958,
		                    "number": "2",
		                    "exCode": null,
		                    "exLabel": null,
		                    "exSystemKeyname": null,
		                    "version": 0,
		                    "isDeviceObjectMetadata": null,
		                    "isManual": true,
		                    "verificationInterval": null,
		                    "verificationDate": null,
		                    "metaVersion": 1,
		                    "instType": null,
		                    "deviceObjectName": null,
		                    "activeDeviceObject": true
		                  },
		                  "_id": null,
		                  "linkedNodeObjects": [],
		                  "links": {
		                    "empty": true
		                  }
		                }
		              ],
		              "_id": null,
		              "linkedNodeObjects": [],
		              "links": {
		                "empty": true
		              }
		            },
		            {
		              "nodeType": "CONT_ZPOINT",
		              "nodeName": null,
		              "nodeObject": {
		                "id": 128870814,
		                "contObjectId": 128870763,
		                "contServiceTypeKeyname": "hw",
		                "customServiceName": "Горячая вода",
		                "startDate": 1487152283627,
		                "endDate": null,
		                "contZPointComment": null,
		                "version": 0
		              },
		              "childNodes": [
		                {
		                  "nodeType": "DEVICE_OBJECT",
		                  "nodeName": null,
		                  "nodeObject": {
		                    "id": 128870769,
		                    "deviceModelId": 29779958,
		                    "number": null,
		                    "exCode": null,
		                    "exLabel": null,
		                    "exSystemKeyname": null,
		                    "version": 0,
		                    "isDeviceObjectMetadata": null,
		                    "isManual": true,
		                    "verificationInterval": null,
		                    "verificationDate": null,
		                    "metaVersion": 1,                    
		                    "deviceObjectName": null,
		                    "activeDeviceObject": true
		                  },
		                  "_id": null,
		                  "linkedNodeObjects": [],
		                  "links": {
		                    "empty": true
		                  }
		                }
		              ],
		              "_id": null,
		              "linkedNodeObjects": [],
		              "links": {
		                "empty": true
		              }
		            },
		            {
		              "nodeType": "CONT_ZPOINT",
		              "nodeName": null,
		              "nodeObject": {
		                "id": 128871318,
		                "contObjectId": 128870763,
		                "contServiceTypeKeyname": "cw",
		                "customServiceName": "ХВС",
		                "startDate": 1487170176575,
		                "endDate": null,
		                "contZPointComment": null,
		                "version": 0
		              },
		              "childNodes": [
		                {
		                  "nodeType": "DEVICE_OBJECT",
		                  "nodeName": null,
		                  "nodeObject": {
		                    "id": 128871298,
		                    "deviceModelId": 29779958,
		                    "number": "321",
		                    "exCode": null,
		                    "exLabel": null,
		                    "exSystemKeyname": null,
		                    "version": 0,
		                    "isDeviceObjectMetadata": null,
		                    "isManual": true,
		                    "verificationInterval": null,
		                    "verificationDate": null,
		                    "metaVersion": 1,
		                    "deviceObjectName": null,
		                    "activeDeviceObject": true
		                  },
		                  "_id": null,
		                  "linkedNodeObjects": [],
		                  "links": {
		                    "empty": true
		                  }
		                }
		              ],
		              "_id": null,
		              "linkedNodeObjects": [],
		              "links": {
		                "empty": true
		              }
		            },
		            {
		              "nodeType": "CONT_ZPOINT",
		              "nodeName": null,
		              "nodeObject": {
		                "id": 129321398,
		                "contObjectId": 128870763,
		                "contServiceTypeKeyname": "heat",
		                "customServiceName": "Система отопления",
		                "startDate": 1496912029039,
		                "endDate": null,
		                "contZPointComment": null,
		                "version": 0
		              },
		              "childNodes": [
		                {
		                  "nodeType": "DEVICE_OBJECT",
		                  "nodeName": null,
		                  "nodeObject": {
		                    "id": 129321395,
		                    "deviceModelId": 129265814,
		                    "number": null,
		                    "exCode": null,
		                    "exLabel": null,
		                    "exSystemKeyname": null,
		                    "version": 0,
		                    "isDeviceObjectMetadata": null,
		                    "isManual": true,
		                    "verificationInterval": null,
		                    "verificationDate": null,
		                    "metaVersion": 1,
		                    "instType": "P",
		                    "deviceObjectName": null,
		                    "activeDeviceObject": true
		                  },
		                  "_id": null,
		                  "linkedNodeObjects": [],
		                  "links": {
		                    "empty": true
		                  }
		                }
		              ],
		              "_id": null,
		              "linkedNodeObjects": [],
		              "links": {
		                "empty": true
		              }
		            }
		          ],
		          "_id": null,
		          "linkedNodeObjects": [],
		          "links": {
		            "empty": true
		          }
		        },
		        {
		          "nodeType": "CONT_OBJECT",
		          "nodeName": null,
		          "nodeObject": {
		            "id": 128875744,
		            "name": null,
		            "fullName": "АВТО-РОМАШКА-ТЕСТ",
		            "fullAddress": null,
		            "number": null,
		            "owner": null,
		            "ownerContacts": null,
		            "currentSettingMode": "winter",
		            "settingModeMDate": null,
		            "description": null,
		            "cwTemp": null,
		            "heatArea": null,
		            "version": 0,
		            "timezoneDefKeyname": "MSK",
		            "exSystemKeyname": null,
		            "isManual": true,            
		            "buildingType": "H",
		            "buildingTypeCategory": "X_1000002",
		            "numOfStories": null
		          },
		          "_id": null,
		          "linkedNodeObjects": [],
		          "links": {
		            "empty": true
		          }
		        },
		        {
		          "nodeType": "CONT_OBJECT",
		          "nodeName": null,
		          "nodeObject": {
		            "id": 128926256,
		            "name": null,
		            "fullName": "test_create",
		            "fullAddress": null,
		            "number": null,
		            "owner": null,
		            "ownerContacts": null,
		            "currentSettingMode": "summer",
		            "settingModeMDate": null,
		            "description": null,
		            "cwTemp": null,
		            "heatArea": null,
		            "version": 0,
		            "timezoneDefKeyname": "MSK",
		            "exSystemKeyname": null,
		            "isManual": true
		          },
		          "_id": null,
		          "linkedNodeObjects": [],
		          "links": {
		            "empty": true
		          }
		        },
		        {
		          "nodeType": "CONT_OBJECT",
		          "nodeName": null,
		          "nodeObject": {
		            "id": 129384626,
		            "name": null,
		            "fullName": "AK-06-2017",
		            "fullAddress": null,
		            "number": null,
		            "owner": null,
		            "ownerContacts": null,
		            "currentSettingMode": "summer",
		            "settingModeMDate": null,
		            "description": null,
		            "cwTemp": null,
		            "heatArea": null,
		            "version": 0,
		            "timezoneDefKeyname": "MSK",
		            "exSystemKeyname": null,
		            "isManual": true
		          },
		          "childNodes": [
		            {
		              "nodeType": "CONT_ZPOINT",
		              "nodeName": null,
		              "nodeObject": {
		                "id": 129497653,
		                "contObjectId": 129384626,
		                "contServiceTypeKeyname": "heat",
		                "customServiceName": "GVS",
		                "startDate": 1499434749344,
		                "endDate": null,
		                "contZPointComment": null,
		                "version": 0
		              },
		              "childNodes": [
		                {
		                  "nodeType": "DEVICE_OBJECT",
		                  "nodeName": null,
		                  "nodeObject": {
		                    "id": 129384631,
		                    "deviceModelId": 129265814,
		                    "number": "АК-44444",
		                    "exCode": null,
		                    "exLabel": null,
		                    "exSystemKeyname": null,
		                    "version": 0,
		                    "isDeviceObjectMetadata": null,
		                    "isManual": true,
		                    "verificationInterval": null,
		                    "verificationDate": null,
		                    "metaVersion": 1,
		                    "isHexPassword": null,
		                    "isTimeSyncEnabled": null,
		                    "isImpulse": true,
		                    "deviceObjectName": null,
		                    "activeDeviceObject": true
		                  },
		                  "_id": null,
		                  "linkedNodeObjects": [],
		                  "links": {
		                    "empty": true
		                  }
		                }
		              ],
		              "_id": null,
		              "linkedNodeObjects": [],
		              "links": {
		                "empty": true
		              }
		            },
		            {
		              "nodeType": "CONT_ZPOINT",
		              "nodeName": null,
		              "nodeObject": {
		                "id": 129497665,
		                "contObjectId": 129384626,
		                "contServiceTypeKeyname": "cw",
		                "customServiceName": "ХВС",
		                "startDate": 1499435941216,
		                "endDate": null,
		                "contZPointComment": null,
		                "version": 0
		              },
		              "childNodes": [
		                {
		                  "nodeType": "DEVICE_OBJECT",
		                  "nodeName": null,
		                  "nodeObject": {
		                    "id": 129384631,
		                    "deviceModelId": 129265814,
		                    "number": "АК-44444",
		                    "exCode": null,
		                    "exLabel": null,
		                    "exSystemKeyname": null,
		                    "version": 0,
		                    "isDeviceObjectMetadata": null,
		                    "isManual": true,
		                    "verificationInterval": null,
		                    "verificationDate": null,
		                    "metaVersion": 1,
		                    "deviceObjectName": null,
		                    "activeDeviceObject": true
		                  },
		                  "_id": null,
		                  "linkedNodeObjects": [],
		                  "links": {
		                    "empty": true
		                  }
		                }
		              ],
		              "_id": null,
		              "linkedNodeObjects": [],
		              "links": {
		                "empty": true
		              }
		            }
		          ],
		          "_id": null,
		          "linkedNodeObjects": [],
		          "links": {
		            "empty": true
		          }
		        }
		      ],
		      "links": {
		        "empty": true
		      }
		    }
		  ],
		  "linkedNodeObjects": [],
		  "links": {
		    "empty": true
		  }
		},
		TEST_P_TREE = {};

	var objectsTreeService, httpBackend;	

	beforeEach(function () {
		//load app 'portalNMC'
		module("portalNMC");
		//get mainSvc
		//get httpBackend for mock server request/response
		inject(function ($httpBackend, _objectsTreeSvc_) {
			objectsTreeService = _objectsTreeSvc_;
			httpBackend = $httpBackend;
		});
	});

	beforeEach(function () {
		jasmine.getJSONFixtures().fixturesPath = 'base/mock';
		// httpBackend.when("GET", /api/).respond(null);
		//httpBackend.when("GET", "../api/p-tree-node").respond(getJSONFixture('test_p_tree.json'))
		TEST_P_TREE = getJSONFixture('test_p_tree.json');
	});

	afterEach(function () {
		// httpBackend.verifyNoOutstandingExpectation();
  //       httpBackend.verifyNoOutstandingRequest();
	});

	//CHECKERS test

	xit("objectsTreeSvc.findNodeInPTree null-test:", function () {
		expect(objectsTreeService.findNodeInPTree).toBeDefined();
		// httpBackend.flush();
		var testNode = {};
		var foundedNode = objectsTreeService.findNodeInPTree(testNode, TEST_P_TREE);		
		expect(foundedNode).toBe(null);
	});

	it("objectsTreeSvc.findNodeInPTree search CONT_OBJECT test:", function () {
		expect(objectsTreeService.findNodeInPTree).toBeDefined();
		// httpBackend.flush();
		var testNode = TEST_P_TREE.childNodes[0].linkedNodeObjects[0];
		// console.log("test CONT_OBJECT node: ", testNode);
		// console.log("test PTree: ", TEST_P_TREE);
		var foundedNode = objectsTreeService.findNodeInPTree(testNode, TEST_P_TREE);
		// console.log("Founded CONT_OBJECT node: ", foundedNode);
		expect(foundedNode).toEqual(testNode);
	});

	it("objectsTreeSvc.findNodeInPTree search ELEMENT test:", function () {
		expect(objectsTreeService.findNodeInPTree).toBeDefined();
		// httpBackend.flush();
		var testNode = TEST_P_TREE.childNodes[1].childNodes[0];
		var foundedNode = objectsTreeService.findNodeInPTree(testNode, TEST_P_TREE);
//		console.log("Founded ELEMENT node: " + foundedNode.nodeType + "; " + foundedNode._id + "; " + foundedNode.nodeName);
		expect(foundedNode).toEqual(testNode);
	});

	it("objectsTreeSvc.findNodeInPTree search CONT_ZPOINT test:", function () {
		expect(objectsTreeService.findNodeInPTree).toBeDefined();
		// httpBackend.flush();
		var testNode = TEST_P_TREE.childNodes[0].linkedNodeObjects[0].childNodes[0];
		var foundedNode = objectsTreeService.findNodeInPTree(testNode, TEST_P_TREE);
		// console.log("Founded CONT_ZPOINT node: ", foundedNode);
		expect(foundedNode).toEqual(testNode);
	});

	it("objectsTreeSvc.findNodeInPTree search DEVICE_OBJECT test:", function () {
		expect(objectsTreeService.findNodeInPTree).toBeDefined();
		// httpBackend.flush();
		var testNode = TEST_P_TREE.childNodes[0].linkedNodeObjects[0].childNodes[0].childNodes[0];
		var foundedNode = objectsTreeService.findNodeInPTree(testNode, TEST_P_TREE);
		// console.log("Founded DEVICE_OBJECT node: ", foundedNode);
		expect(foundedNode).toEqual(testNode);
	});
});