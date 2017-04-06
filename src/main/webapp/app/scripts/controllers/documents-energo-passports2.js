/*jslint node: true, eqeq: true*/
/*global angular, $*/
'use strict';
var app = angular.module('portalNMC');
app.controller('documentsEnergoPassportsCtrl2', ['$rootScope', '$scope', '$http', 'notificationFactory', 'mainSvc', '$timeout', '$interval', function ($rootScope, $scope, $http, notificationFactory, mainSvc, $timeout, $interval) {
    
    $scope.showContents_flag = true;
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.loading = true;
    $scope.ctrlSettings.cssMeasureUnit = "rem";
    
    $timeout(function () {
        $scope.ctrlSettings.loading = false;
    }, 1500);
    
    $scope.addPassport = function () {
        $('#editEnergoPassportModal').modal();
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
              "_colSpan": 1
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
              "_colSpan": 1
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
    
console.log(inputTableDef);    
    function getHeader(inputData) {
        var headerPart = null;
        //TODO: check inputData
        inputData.parts.some(function (part) {
            if (part.partType === 'HEADER') {
                headerPart = part;
                return true;
            }            
        });        
console.log(headerPart);        
        return headerPart;
    }
    
    function getRows(inputData) {
        var dataRows = [];
        inputData.parts.some(function (part, ind) {
            if (part.partType === 'ROW') {
                var dataRow = {};
                dataRow = angular.copy(part);
                dataRows.push(dataRow);
console.log(dataRow);                
            }
            
//            if (ind === 3) {
//                return true;
//            }
        });
        return dataRows;
    }
    
    $scope.header = getHeader(inputTableDef);
    $scope.data.rows = getRows(inputTableDef);
    
    $scope.data.data = [
        {
            name: 'Bob',
            title: 'CEO',
            age: '31'
        }, {
            name: 'Frank',
            title: 'Lowly Developer',
            age: '33'
        }
    ];
  
  $scope.gridOptions1 = {
      headerTemplate: 'header-template.html',
      headerDef: $scope.header,
      superColDefs: [
      {
          name: 'group1',
          displayName: 'Group 1'
      }, {
          name: 'group2',
          displayName: 'Group 2'
      }],
      columnDefs: [{
          name: 'name',
          displayName: 'Name',
          superCol: 'group1'
      }, {
          name: 'title',
          displayName: 'Title',
          superCol: 'group1'
      }, {
          name: 'age',
          displayName: 'Age',
          superCol: 'group2'
      }],
      data: $scope.data.rows,
        onRegisterApi: function (gridApi) {
          $scope.gridApi = gridApi;

          // call resize every 500 ms for 5 s after modal finishes opening - usually only necessary on a bootstrap modal
          $interval( function() {
            $scope.gridApi.core.handleWindowResize();
          }, 500, 10);
        }
  };
    
    $scope.gridOptions = {
      headerTemplate: 'header-template.html',
      headerDef: $scope.header,
      columnDefs: [{
          name: 'name',
          displayName: 'Name',
          superCol: 'group1'
      }, {
          name: 'title',
          displayName: 'Title',
          superCol: 'group1'
      }, {
          name: 'age',
          displayName: 'Age',
          superCol: 'group2'
      }],
      data: $scope.data.rows,
        onRegisterApi: function (gridApi) {
          $scope.gridApi = gridApi;

          // call resize every 500 ms for 5 s after modal finishes opening - usually only necessary on a bootstrap modal
          $interval( function() {
            $scope.gridApi.core.handleWindowResize();
          }, 500, 10);
        }
  };
}]);