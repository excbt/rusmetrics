/*jslint node: true, eqeq: true*/
/*global angular, $*/
'use strict';
var app = angular.module('portalNMC');
app.controller('documentsEnergoPassportsCtrl', ['$rootScope', '$scope', '$http', 'notificationFactory', 'mainSvc', '$timeout', '$interval', function ($rootScope, $scope, $http, notificationFactory, mainSvc, $timeout, $interval) {
    
    $scope.showContents_flag = true;
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.loading = true;
    $scope.ctrlSettings.cssMeasureUnit = "em";
    $scope.ctrlSettings.passportLoading = true;
    $scope.ctrlSettings.emptyString = " ";
    
    $timeout(function () {
        $scope.ctrlSettings.loading = false;
    }, 1500);
    
    $scope.addPassport = function () {
//        $scope.ctrlSettings.passportLoading = true;
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
    
//console.log(inputTableDef);
    
    $scope.passDocStructure = null;
    $scope.currentPassDocPart = null;
    function loadPassDoc(url) {
        $http.get(url)
            .then(function (resp) {
            if ($scope.passDocStructure === null) {
                $scope.passDocStructure = [];
            }
            $scope.passDocStructure.push(angular.copy(resp.data));
            if ($scope.passDocStructure.length === 1) {
                $scope.currentPassDocPart = $scope.passDocStructure[0];
                $scope.currentPassDocPart.isSelected = true;
                $timeout(function () {
                    $(':input').inputmask();
                }, 0);
            }
//console.log($scope.passDocStructure);
            $scope.ctrlSettings.passportLoading = false;
        }, function (res) {
            console.error(res);
        });
    }
    loadPassDoc('../app/resource/passDocP0.json');
    loadPassDoc('../app/resource/passDocP1.json');
    
    $scope.contentsPartSelect = function(part) {
//console.log(part);
        $scope.currentPassDocPart.isSelected = false;
        $scope.currentPassDocPart = part;
        $scope.currentPassDocPart.isSelected = true;
        $timeout(function () {
            $(':input').inputmask();
        }, 0);
        
    }
    
    $scope.tdBlur = function (cell) {
console.log(cell);        
    }
    
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
        }, 0);
    });
    
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
}])
.directive('contenteditable', ['$sce', function($sce) {
  return {
    restrict: 'A', // only activate on element attribute
    require: '?ngModel', // get a hold of NgModelController
    link: function(scope, element, attrs, ngModel) {
      if (!ngModel) return; // do nothing if no ng-model

      // Specify how UI should be updated
      ngModel.$render = function() {
        element.html($sce.getTrustedHtml(ngModel.$viewValue || ''));
      };

      // Listen for change events to enable binding
      element.on('blur keyup change', function() {
        scope.$evalAsync(read);
      });
      read(); // initialize

      // Write data to the model
      function read() {
        var html = element.html();
        // When we clear the content editable the browser leaves a <br> behind
        // If strip-br attribute is provided then we strip this out
        if (attrs.stripBr && html === '<br>') {
          html = '';
        }
        ngModel.$setViewValue(html);
      }
    }
  };
}]);;