/*jslint node: true, eqeq: true, white: true, nomen: true*/
/*global angular, $*/
'use strict';
var app = angular.module('portalNMC');
app.controller('documentsEnergoPassportsCtrl', ['$rootScope', '$scope', '$http', 'notificationFactory', 'mainSvc', '$timeout', '$interval', 'energoPassportSvc', '$location', function ($rootScope, $scope, $http, notificationFactory, mainSvc, $timeout, $interval, energoPassportSvc, $location) {
    
    $scope.showContents_flag = true;
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.loading = true;
    $scope.ctrlSettings.cssMeasureUnit = "em";
    $scope.ctrlSettings.passportLoading = true;
    $scope.ctrlSettings.emptyString = " ";
    
        //model columns
    $scope.ctrlSettings.passportColumns = [
        {
            "name": "id",
            "caption": "id",
            "class": "col-xs-1",
            "type": "id"
        },
        {
            "name": "passportName",
            "caption": "Название",
            "class": "col-xs-3",
            "type": "name"
        },
        {
            "name": "description",
            "caption": "Описание",
            "class": "col-xs-3"
        },
        {
            "name": "passportDate2",
            "caption": "Дата создания",
            "class": "col-xs-1",
            "type": "date"
        }
        

    ];
        
    
//    $timeout(function () {
//        $scope.ctrlSettings.loading = false;
//    }, 1500);
    
    $scope.addPassport = function () {
//        $scope.ctrlSettings.passportLoading = true;
        $location.path("/documents/energo-passport/new");
        //$('#editEnergoPassportModal').modal();
    };
    
    $scope.editPassport = function (passport) {
        $location.path("/documents/energo-passport/" + passport.id);
    };
    
    $scope.data = {};
    $scope.data.passportList = [
        
    ];
    
    $scope.data.contents = [
        {
            id: 1,
            name: "devicesinfo",
            caption: "Сведения об оснащенности приборами учета"
        }
    ];
    
      
    
    
    var superColDefs_var1 = [
        {
            name: "number",
            displayName: "№ п/п"
        }, {
            name: 'group1',
            displayName: 'Group 1'
        }, {
            name: 'group2',
            displayName: 'Group 2'
        }
    ];
    
    var inputTableDef = {
      "parts": [
        {
          "key": null,
          "partType": "HEADER",
          "elements": [
            {
              "@type": "Static",
              "width": 10.0,
              "partKey": null,
              "caption": "№ п/п",
              "_rowSpan": 3,
              "_colSpan": 1,
              "_totalWidth": 10.0
            },
            {
              "@type": "Static",
              "width": 40.0,
              "partKey": null,
              "caption": "Наименование показателя",
              "_rowSpan": 3,
              "_colSpan": 1,
              "_totalWidth": 40.0
            },
            {
              "@type": "Static",
              "partKey": null,
              "caption": "Количество, шт",
              "_rowSpan": 1,
              "_colSpan": 3,
              "_totalWidth": 60.0,
              "elements": [
                {
                  "@type": "Static",
                  "partKey": null,
                  "caption": "Электрической энергии",
                  "_rowSpan": 1,
                  "_colSpan": 2,
                  "_totalWidth": 20.0,
                  "elements": [
                    {
                      "@type": "Static",
                      "width": 10.0,
                      "keyValueIdx": 1,
                      "partKey": null,
                      "caption": "Всего",
                      "_rowSpan": 1,
                      "_colSpan": 1,
                      "_totalWidth": 10.0
                    },
                    {
                      "@type": "Static",
                      "width": 10.0,
                      "keyValueIdx": 2,
                      "partKey": null,
                      "caption": "В том числе в составе АИИС",
                      "_rowSpan": 1,
                      "_colSpan": 1,
                      "_totalWidth": 10.0
                    }
                  ]
                },
                {
                  "@type": "Static",
                  "partKey": null,
                  "caption": "Тепловой энергии",
                  "_rowSpan": 1,
                  "_colSpan": 2,
                  "_totalWidth": 20.0,
                  "elements": [
                    {
                      "@type": "Static",
                      "width": 10.0,
                      "keyValueIdx": 3,
                      "partKey": null,
                      "caption": "Всего",
                      "_rowSpan": 1,
                      "_colSpan": 1,
                      "_totalWidth": 10.0
                    },
                    {
                      "@type": "Static",
                      "width": 10.0,
                      "keyValueIdx": 4,
                      "partKey": null,
                      "caption": "В том числе в составе АИИС",
                      "_rowSpan": 1,
                      "_colSpan": 1,
                      "_totalWidth": 10.0
                    }
                  ]
                },
                {
                  "@type": "Static",
                  "partKey": null,
                  "caption": "Газа",
                  "_rowSpan": 1,
                  "_colSpan": 2,
                  "_totalWidth": 20.0,
                  "elements": [
                    {
                      "@type": "Static",
                      "width": 10.0,
                      "keyValueIdx": 5,
                      "partKey": null,
                      "caption": "Всего",
                      "_rowSpan": 1,
                      "_colSpan": 1,
                      "_totalWidth": 10.0
                    },
                    {
                      "@type": "Static",
                      "width": 10.0,
                      "keyValueIdx": 6,
                      "partKey": null,
                      "caption": "В том числе в составе АИИС",
                      "_rowSpan": 1,
                      "_colSpan": 1,
                      "_totalWidth": 10.0
                    }
                  ]
                }
              ]
            }
          ],
          "_totalWidth": 110.0,
          "_columnWidths": [
            10.0,
            40.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0
          ]
        },
        {
          "key": "P_1",
          "partType": "ROW",
          "elements": [
            {
              "@type": "Static",
              "partKey": "P_1",
              "caption": "1",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Static",
              "merged": true,
              "partKey": "P_1",
              "caption": "Сведения об оснащенности приборами коммерческого учета",
              "_rowSpan": 1,
              "_colSpan": 7
            }
          ],
          "_columnWidths": [
            10.0,
            100.0
          ]
        },
        {
          "key": "P_1.1",
          "partType": "ROW",
          "elements": [
            {
              "@type": "Static",
              "partKey": "P_1.1",
              "caption": "1.1",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Static",
              "partKey": "P_1.1",
              "caption": "Количество оборудованных узлами (приборами) учета точек приема (поставки), всего,\nв том числе:\n",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "DoubleAgg",
              "cellType": "VALUE",
              "keyValueIdx": 1,
              "partKey": "P_1.1",
              "value": null,
              "valueFunction": "sum()",
              "valueGroup": "P_1.1.*",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "DoubleAgg",
              "cellType": "VALUE",
              "keyValueIdx": 2,
              "partKey": "P_1.1",
              "value": null,
              "valueFunction": "sum()",
              "valueGroup": "P_1.1.*",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "DoubleAgg",
              "cellType": "VALUE",
              "keyValueIdx": 3,
              "partKey": "P_1.1",
              "value": null,
              "valueFunction": "sum()",
              "valueGroup": "P_1.1.*",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "DoubleAgg",
              "cellType": "VALUE",
              "keyValueIdx": 4,
              "partKey": "P_1.1",
              "value": null,
              "valueFunction": "sum()",
              "valueGroup": "P_1.1.*",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "DoubleAgg",
              "cellType": "VALUE",
              "keyValueIdx": 5,
              "partKey": "P_1.1",
              "value": null,
              "valueFunction": "sum()",
              "valueGroup": "P_1.1.*",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "DoubleAgg",
              "cellType": "VALUE",
              "keyValueIdx": 6,
              "partKey": "P_1.1",
              "value": null,
              "valueFunction": "sum()",
              "valueGroup": "P_1.1.*",
              "_rowSpan": 1,
              "_colSpan": 1
            }
          ],
          "_columnWidths": [
            10.0,
            40.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0
          ]
        },
        {
          "key": "P_1.1.1",
          "partType": "ROW",
          "elements": [
            {
              "@type": "Static",
              "partKey": "P_1.1.1",
              "caption": "1.1.1",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Static",
              "partKey": "P_1.1.1",
              "caption": "полученной от стороннего источника",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 1,
              "partKey": "P_1.1.1",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 2,
              "partKey": "P_1.1.1",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 3,
              "partKey": "P_1.1.1",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 4,
              "partKey": "P_1.1.1",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 5,
              "partKey": "P_1.1.1",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 6,
              "partKey": "P_1.1.1",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            }
          ],
          "_columnWidths": [
            10.0,
            40.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0
          ]
        },
        {
          "key": "P_1.1.2",
          "partType": "ROW",
          "elements": [
            {
              "@type": "Static",
              "partKey": "P_1.1.2",
              "caption": "1.1.2",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Static",
              "partKey": "P_1.1.2",
              "caption": "собственного производства",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 1,
              "partKey": "P_1.1.2",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 2,
              "partKey": "P_1.1.2",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 3,
              "partKey": "P_1.1.2",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 4,
              "partKey": "P_1.1.2",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 5,
              "partKey": "P_1.1.2",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 6,
              "partKey": "P_1.1.2",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            }
          ],
          "_columnWidths": [
            10.0,
            40.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0
          ]
        },
        {
          "key": "P_1.1.3",
          "partType": "ROW",
          "elements": [
            {
              "@type": "Static",
              "partKey": "P_1.1.3",
              "caption": "1.1.3",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Static",
              "partKey": "P_1.1.3",
              "caption": "потребленной на собственные нужды",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 1,
              "partKey": "P_1.1.3",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 2,
              "partKey": "P_1.1.3",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 3,
              "partKey": "P_1.1.3",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 4,
              "partKey": "P_1.1.3",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 5,
              "partKey": "P_1.1.3",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 6,
              "partKey": "P_1.1.3",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            }
          ],
          "_columnWidths": [
            10.0,
            40.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0
          ]
        },
        {
          "key": "P_1.1.4",
          "partType": "ROW",
          "elements": [
            {
              "@type": "Static",
              "partKey": "P_1.1.4",
              "caption": "1.1.4",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Static",
              "partKey": "P_1.1.4",
              "caption": "отданной субабонентам (сторонним потребителям)",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 1,
              "partKey": "P_1.1.4",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 2,
              "partKey": "P_1.1.4",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 3,
              "partKey": "P_1.1.4",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 4,
              "partKey": "P_1.1.4",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 5,
              "partKey": "P_1.1.4",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 6,
              "partKey": "P_1.1.4",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            }
          ],
          "_columnWidths": [
            10.0,
            40.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0
          ]
        },
        {
          "key": "P_1.2",
          "partType": "ROW",
          "elements": [
            {
              "@type": "Static",
              "partKey": "P_1.2",
              "caption": "1.2",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Static",
              "partKey": "P_1.2",
              "caption": "Количество необорудованных узлами (приборами) учета точек приема (поставки), всего,\nв том числе:\n",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "DoubleAgg",
              "cellType": "VALUE",
              "keyValueIdx": 1,
              "partKey": "P_1.2",
              "value": null,
              "valueFunction": "sum()",
              "valueGroup": "P_2.1.*",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "DoubleAgg",
              "cellType": "VALUE",
              "keyValueIdx": 2,
              "partKey": "P_1.2",
              "value": null,
              "valueFunction": "sum()",
              "valueGroup": "P_2.1.*",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "DoubleAgg",
              "cellType": "VALUE",
              "keyValueIdx": 3,
              "partKey": "P_1.2",
              "value": null,
              "valueFunction": "sum()",
              "valueGroup": "P_2.1.*",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "DoubleAgg",
              "cellType": "VALUE",
              "keyValueIdx": 4,
              "partKey": "P_1.2",
              "value": null,
              "valueFunction": "sum()",
              "valueGroup": "P_2.1.*",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "DoubleAgg",
              "cellType": "VALUE",
              "keyValueIdx": 5,
              "partKey": "P_1.2",
              "value": null,
              "valueFunction": "sum()",
              "valueGroup": "P_2.1.*",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "DoubleAgg",
              "cellType": "VALUE",
              "keyValueIdx": 6,
              "partKey": "P_1.2",
              "value": null,
              "valueFunction": "sum()",
              "valueGroup": "P_2.1.*",
              "_rowSpan": 1,
              "_colSpan": 1
            }
          ],
          "_columnWidths": [
            10.0,
            40.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0
          ]
        },
        {
          "key": "P_1.2.1",
          "partType": "ROW",
          "elements": [
            {
              "@type": "Static",
              "partKey": "P_1.2.1",
              "caption": "1.2.1",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Static",
              "partKey": "P_1.2.1",
              "caption": "полученной от стороннего источника",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 1,
              "partKey": "P_1.2.1",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 2,
              "partKey": "P_1.2.1",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 3,
              "partKey": "P_1.2.1",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 4,
              "partKey": "P_1.2.1",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 5,
              "partKey": "P_1.2.1",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 6,
              "partKey": "P_1.2.1",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            }
          ],
          "_columnWidths": [
            10.0,
            40.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0
          ]
        },
        {
          "key": "P_1.2.2",
          "partType": "ROW",
          "elements": [
            {
              "@type": "Static",
              "partKey": "P_1.2.2",
              "caption": "1.2.2",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Static",
              "partKey": "P_1.2.2",
              "caption": "собственного производства",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 1,
              "partKey": "P_1.2.2",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 2,
              "partKey": "P_1.2.2",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 3,
              "partKey": "P_1.2.2",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 4,
              "partKey": "P_1.2.2",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 5,
              "partKey": "P_1.2.2",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 6,
              "partKey": "P_1.2.2",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            }
          ],
          "_columnWidths": [
            10.0,
            40.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0
          ]
        },
        {
          "key": "P_1.2.3",
          "partType": "ROW",
          "elements": [
            {
              "@type": "Static",
              "partKey": "P_1.2.3",
              "caption": "1.2.3",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Static",
              "partKey": "P_1.2.3",
              "caption": "потребленной на собственные нужды",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 1,
              "partKey": "P_1.2.3",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 2,
              "partKey": "P_1.2.3",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 3,
              "partKey": "P_1.2.3",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 4,
              "partKey": "P_1.2.3",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 5,
              "partKey": "P_1.2.3",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 6,
              "partKey": "P_1.2.3",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            }
          ],
          "_columnWidths": [
            10.0,
            40.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0
          ]
        },
        {
          "key": "P_1.2.4",
          "partType": "ROW",
          "elements": [
            {
              "@type": "Static",
              "partKey": "P_1.2.4",
              "caption": "1.2.4",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Static",
              "partKey": "P_1.2.4",
              "caption": "отданной субабонентам (сторонним потребителям)",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 1,
              "partKey": "P_1.2.4",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 2,
              "partKey": "P_1.2.4",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 3,
              "partKey": "P_1.2.4",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 4,
              "partKey": "P_1.2.4",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 5,
              "partKey": "P_1.2.4",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 6,
              "partKey": "P_1.2.4",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            }
          ],
          "_columnWidths": [
            10.0,
            40.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0
          ]
        },
        {
          "key": "P_1.3",
          "partType": "ROW",
          "elements": [
            {
              "@type": "Static",
              "partKey": "P_1.3",
              "caption": "1.3",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Static",
              "partKey": "P_1.3",
              "caption": "Количество узлов (приборов) учета с нарушенными сроками поверки",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 1,
              "partKey": "P_1.3",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 2,
              "partKey": "P_1.3",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 3,
              "partKey": "P_1.3",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 4,
              "partKey": "P_1.3",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 5,
              "partKey": "P_1.3",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 6,
              "partKey": "P_1.3",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            }
          ],
          "_columnWidths": [
            10.0,
            40.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0
          ]
        },
        {
          "key": "P_1.4",
          "partType": "ROW",
          "elements": [
            {
              "@type": "Static",
              "partKey": "P_1.4",
              "caption": "1.4",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Static",
              "partKey": "P_1.4",
              "caption": "Количество узлов (приборов) учета с нарушением требований к классу точности (относительной погрешности) узла (прибора) учета",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 1,
              "partKey": "P_1.4",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 2,
              "partKey": "P_1.4",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 3,
              "partKey": "P_1.4",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 4,
              "partKey": "P_1.4",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 5,
              "partKey": "P_1.4",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 6,
              "partKey": "P_1.4",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            }
          ],
          "_columnWidths": [
            10.0,
            40.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0
          ]
        },
        {
          "key": "P_2",
          "partType": "ROW",
          "elements": [
            {
              "@type": "Static",
              "partKey": "P_2",
              "caption": "2",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Static",
              "merged": true,
              "partKey": "P_2",
              "caption": "Сведения об оснащенности узлами (приборами) технического учета",
              "_rowSpan": 1,
              "_colSpan": 7
            }
          ],
          "_columnWidths": [
            10.0,
            100.0
          ]
        },
        {
          "key": "P_2.1",
          "partType": "ROW",
          "elements": [
            {
              "@type": "Static",
              "partKey": "P_2.1",
              "caption": "2.1",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Static",
              "partKey": "P_2.1",
              "caption": "Суммарное количество узлов (приборов) учета",
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 1,
              "partKey": "P_2.1",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 2,
              "partKey": "P_2.1",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 3,
              "partKey": "P_2.1",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 4,
              "partKey": "P_2.1",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 5,
              "partKey": "P_2.1",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            },
            {
              "@type": "Double",
              "cellType": "VALUE",
              "keyValueIdx": 6,
              "partKey": "P_2.1",
              "value": null,
              "_rowSpan": 1,
              "_colSpan": 1
            }
          ],
          "_columnWidths": [
            10.0,
            40.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0
          ]
        }
      ]
    };
    
    function errorCallback(e) {
        $scope.ctrlSettings.loading = false;
        var errorObj = mainSvc.errorCallbackHandler(e);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    }
    
    function successLoadPassportsCallback(resp) {
//console.log(resp);        
        $scope.data.passports = angular.copy(resp.data);
        $scope.ctrlSettings.loading = false;
    }
    
//console.log(inputTableDef);
    function performElementsRecursion(elements, level, headerRows) {
        if (angular.isUndefined(elements) || elements === null) {
            return;
        }
        elements.forEach(function (elm) {
            if (headerRows.length <= level) {
                headerRows.push([]);
            }
            headerRows[level].push(angular.copy(elm));
            performElementsRecursion(elm.elements, level + 1, headerRows);
        });
    }
    
    function prepareTableHeaderRecursion(tablePart) {
        var headerRows = [];
        performElementsRecursion(tablePart.elements, 0, headerRows);        
        tablePart.headerRows = headerRows;
        return tablePart;
    }
    
//    function prepareTableHeader(tablePart) {
//        var headerRows = [];
//        var headerRow1 = [];
//        var headerRow2 = [];
//        var headerRow3 = [];
//        tablePart.elements.forEach(function (headerElem) {            
//            headerRow1.push(angular.copy(headerElem));
//            if (!headerElem.hasOwnProperty("elements") || !angular.isArray(headerElem.elements)) {
//                return;
//            }
//            headerElem.elements.forEach(function (cheaderElem) {
//                headerRow2.push(angular.copy(cheaderElem));
//                if (!cheaderElem.hasOwnProperty("elements") || !angular.isArray(cheaderElem.elements)) {
//                    return;
//                }
//                cheaderElem.elements.forEach(function (ccheaderElem) {
//                    headerRow3.push(angular.copy(ccheaderElem));
//                });
//            });
//        });
//        if (headerRow1.length > 0) {
//            headerRows.push(headerRow1);
//        }
//        if (headerRow2.length > 0) {
//            headerRows.push(headerRow2);
//        }
//        if (headerRow3.length > 0) {
//            headerRows.push(headerRow3);
//        }
//        tablePart.headerRows1 = headerRows;
//        return tablePart;
//    }
    
    function performTablePart(tablePart) {
        if (tablePart.partType === "HEADER") {
//console.log("Found header");
//console.log(tablePart);                    
//                    tablePart = prepareTableHeader(tablePart);
            tablePart = prepareTableHeaderRecursion(tablePart);
            return true;
        }

    }
    
    function preparePassDoc(passDoc) {
        //Prepare headers for inner tables
        switch (passDoc.viewType) {
        case "FORM":
            passDoc.parts.forEach(function (passDocPart) {
                if (passDocPart.partType !== "INNER_TABLE") {
                    return;
                }
    //console.log("Found Inner table");
    //console.log(passDocPart);            
                passDocPart.innerPdTable.parts.forEach(performTablePart);
            });
            break;
        case "TABLE":
            passDoc.parts.forEach(performTablePart);
            break;
        }
        return passDoc;
    }    
    
    $scope.passDocStructure = null;
    $scope.passDocStructureFromServer = null;
    $scope.currentPassDocPart = null;
    
//    function successCreatePassportCallback(response) {
//        console.log(response);
//        if (mainSvc.checkUndefinedNull(response) || mainSvc.checkUndefinedNull(response.data)) {
//            return false;
//        }
//        var result = [],
//            tmp = angular.copy(response.data);
//        tmp.sectionTemplates.forEach(function (secTempl) {
//            result.push(preparePassDoc(JSON.parse(secTempl.sectionJson)));
//        });
//console.log(result);        
//return;        
//        $scope.passDocStructure = result;
//        if ($scope.passDocStructure.length >= 1) {
//            $scope.currentPassDocPart = $scope.passDocStructure[0];
//            $scope.currentPassDocPart.isSelected = true;
//            $timeout(function () {
//                $(':input').inputmask();
//            }, 0);
//        }        
//        $scope.ctrlSettings.passportLoading = false;
//    }
//    
//    function createPassDocInit() {
//        energoPassportSvc.createPassport()
//            .then(successCreatePassportCallback, errorCallback);
//    }
    
//    createPassDocInit();
    
//    function loadPassDoc_v1(url) {
//        $http.get(url)
//            .then(function (resp) {
//            if ($scope.passDocStructure === null) {
//                $scope.passDocStructure = [];
//            }
//            var tmpPassDoc = angular.copy(resp.data);
//            
//            //prepare passDoc
//            // - transform table headers
//            tmpPassDoc = preparePassDoc(tmpPassDoc);
//            
//            $scope.passDocStructure.push(tmpPassDoc);
//            if ($scope.passDocStructure.length === 1) {
//                $scope.currentPassDocPart = $scope.passDocStructure[0];
//                $scope.currentPassDocPart.isSelected = true;
//                $timeout(function () {
//                    $(':input').inputmask();
//                }, 0);
//            }
//console.log($scope.passDocStructure);
//            $scope.ctrlSettings.passportLoading = false;
//        }, function (res) {
//            console.error(res);
//        });
//    }
//    loadPassDoc_v1('../app/resource/passDocP0.json');
//    loadPassDoc_v1('../app/resource/passDocP1.json');
    
//    $scope.contentsPartSelect = function(part) {
//        $scope.currentPassDocPart.isSelected = false;
//        $scope.currentPassDocPart = part;
//        $scope.currentPassDocPart.isSelected = true;
//        $timeout(function () {
//            $(':input').inputmask();
//        }, 0);
//        
//    };
//    
//    $scope.tdBlur = function (cell) {
//console.log(cell);        
//    };
    
    function getHeaderPart(inputData) {
        var headerPart = null;
        //TODO: check inputData
        inputData.parts.some(function (part) {
            if (part.partType === 'HEADER') {
                headerPart = part;
                return true;
            }            
        });        
//console.log(headerPart);        
        return headerPart;
    }
    
    function prepareHeader(headerPart) {
        var preparedHeader = angular.copy(headerPart._columnWidths);//[];
//        headerPart._columnWidths.forEach(function (col) {
//            
//        });
        return preparedHeader;
    }
    
    function getRows(inputData) {
        var dataRows = [];
        inputData.parts.some(function (part, ind) {
            if (part.partType === 'ROW') {
                var dataRow = {};
                dataRow = angular.copy(part);
                dataRows.push(dataRow);
//console.log(dataRow);                
            }
            
//            if (ind === 3) {
//                return true;
//            }
        });
        return dataRows;
    }
    var headerPart = getHeaderPart(inputTableDef);
    $scope.table = {};
    $scope.table.width = headerPart._totalWidth;
    $scope.table.maxWidth = headerPart._totalWidth;
    $scope.header = prepareHeader(getHeaderPart(inputTableDef));
    $scope.data.rows = getRows(inputTableDef);
    
    $('#editEnergoPassportModal').on('shown.bs.modal', function () {
        $timeout(function () {
            $(':input').inputmask();
//            $(':input').focus();
        }, 0);
    });
    
    
    function loadPassports() {
        energoPassportSvc.loadPassports()
            .then(successLoadPassportsCallback, errorCallback);
    }
    
    function initCtrl() {
        loadPassports();
    }
    
    initCtrl();
}]);