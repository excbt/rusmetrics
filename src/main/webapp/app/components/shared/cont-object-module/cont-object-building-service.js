/*global angular, console*/
(function () {
    'use strict';

    angular
        .module('contObjectModule')
        .service('contObjectBuildingService', Service);

    Service.$inject = [];

    /* @ngInject */
    function Service() {
        var BUILDING_TYPE_CATEGORY_ICONS_PATH = "components/shared/cont-object-module/buildings/",
            DEFAULT_ICON = "building24.png",
            //icons for size 24x24            
            APT_BUILDING_ICON = "mkd26.png",
            COT_BUILDING_ICON = "cot16.png",
            LEARN_ICON = "school24.png",
            IND_HOUSES_ICON = "dom26.png",
            LAND_PLOT_ICON = "dom26.png",
            TEMP_STAY_ICON = "hotel26.png",
            HEALTH_ICON = "hospital24.png",
            GOV_BUILDINGS_ICON = "gos26.png",
            SPORT_ICON = "stadium24.png",
            BUILDING_TYPE_CATEGORY_ICONS_24 = {
                NR_LEARN: BUILDING_TYPE_CATEGORY_ICONS_PATH + LEARN_ICON,
                X_1000080: BUILDING_TYPE_CATEGORY_ICONS_PATH + LEARN_ICON,
                X_1000081: BUILDING_TYPE_CATEGORY_ICONS_PATH + LEARN_ICON,
                X_1000082: BUILDING_TYPE_CATEGORY_ICONS_PATH + LEARN_ICON,
                X_1000083: BUILDING_TYPE_CATEGORY_ICONS_PATH + LEARN_ICON,
                X_1000084: BUILDING_TYPE_CATEGORY_ICONS_PATH + LEARN_ICON,
                X_1000085: BUILDING_TYPE_CATEGORY_ICONS_PATH + LEARN_ICON,
                X_1000086: BUILDING_TYPE_CATEGORY_ICONS_PATH + LEARN_ICON,
            },
            // ICONS for  size 16x16
            APT_BUILDING_ICON_16 = "mkd16.png",
            COT_BUILDING_ICON_16 = "cot16.png",
            LEARN_ICON_16 = "school16.png",
            IND_HOUSES_ICON_16 = "dom16.png",
            LAND_PLOT_ICON_16 = "dom16.png",
            TEMP_STAY_ICON_16 = "hotel16.png",
            HEALTH_ICON_16 = "hospital24.png",
            GOV_BUILDINGS_ICON_16 = "gos16.png",
            SPORT_ICON_16 = "stadium16.png",
            BUILDING_TYPE_CATEGORY_ICONS_16 = {
                NR_LEARN: BUILDING_TYPE_CATEGORY_ICONS_PATH + LEARN_ICON_16,
                X_1000080: BUILDING_TYPE_CATEGORY_ICONS_PATH + LEARN_ICON_16,
                X_1000081: BUILDING_TYPE_CATEGORY_ICONS_PATH + LEARN_ICON_16,
                X_1000082: BUILDING_TYPE_CATEGORY_ICONS_PATH + LEARN_ICON_16,
                X_1000083: BUILDING_TYPE_CATEGORY_ICONS_PATH + LEARN_ICON_16,
                X_1000084: BUILDING_TYPE_CATEGORY_ICONS_PATH + LEARN_ICON_16,
                X_1000085: BUILDING_TYPE_CATEGORY_ICONS_PATH + LEARN_ICON_16,
                X_1000086: BUILDING_TYPE_CATEGORY_ICONS_PATH + LEARN_ICON_16,
                
                H_IND_HOUSES: BUILDING_TYPE_CATEGORY_ICONS_PATH + IND_HOUSES_ICON_16,
                X_1000000: BUILDING_TYPE_CATEGORY_ICONS_PATH + IND_HOUSES_ICON_16,
                X_1000001: BUILDING_TYPE_CATEGORY_ICONS_PATH + IND_HOUSES_ICON_16,
                
                H_APT_BUILDINGS: BUILDING_TYPE_CATEGORY_ICONS_PATH + APT_BUILDING_ICON_16,
                X_1000002: BUILDING_TYPE_CATEGORY_ICONS_PATH + APT_BUILDING_ICON_16,
                X_1000003: BUILDING_TYPE_CATEGORY_ICONS_PATH + APT_BUILDING_ICON_16,
                X_1000004: BUILDING_TYPE_CATEGORY_ICONS_PATH + APT_BUILDING_ICON_16,
                X_1000005: BUILDING_TYPE_CATEGORY_ICONS_PATH + APT_BUILDING_ICON_16,
                X_1000006: BUILDING_TYPE_CATEGORY_ICONS_PATH + APT_BUILDING_ICON_16,
                
                NR_HEALTH: BUILDING_TYPE_CATEGORY_ICONS_PATH + HEALTH_ICON_16,
                X_1000090: BUILDING_TYPE_CATEGORY_ICONS_PATH + HEALTH_ICON_16,
                X_1000091: BUILDING_TYPE_CATEGORY_ICONS_PATH + HEALTH_ICON_16,
                X_1000092: BUILDING_TYPE_CATEGORY_ICONS_PATH + HEALTH_ICON_16,
                X_1000093: BUILDING_TYPE_CATEGORY_ICONS_PATH + HEALTH_ICON_16,
                X_1000094: BUILDING_TYPE_CATEGORY_ICONS_PATH + HEALTH_ICON_16,
                X_1000095: BUILDING_TYPE_CATEGORY_ICONS_PATH + HEALTH_ICON_16,
                X_1000096: BUILDING_TYPE_CATEGORY_ICONS_PATH + HEALTH_ICON_16,
                X_1000097: BUILDING_TYPE_CATEGORY_ICONS_PATH + HEALTH_ICON_16,
                X_1000098: BUILDING_TYPE_CATEGORY_ICONS_PATH + HEALTH_ICON_16,
                
                H_COT_BUILDINGS: BUILDING_TYPE_CATEGORY_ICONS_PATH + COT_BUILDING_ICON_16,
                X_1000050: BUILDING_TYPE_CATEGORY_ICONS_PATH + COT_BUILDING_ICON_16,
                X_1000051: BUILDING_TYPE_CATEGORY_ICONS_PATH + COT_BUILDING_ICON_16,
                
                NR_SPORT: BUILDING_TYPE_CATEGORY_ICONS_PATH + SPORT_ICON_16,
                X_1000110: BUILDING_TYPE_CATEGORY_ICONS_PATH + SPORT_ICON_16,
                X_1000111: BUILDING_TYPE_CATEGORY_ICONS_PATH + SPORT_ICON_16,
                
                GB_GOV_BUILDINGS: BUILDING_TYPE_CATEGORY_ICONS_PATH + GOV_BUILDINGS_ICON_16,
                X_1000170: BUILDING_TYPE_CATEGORY_ICONS_PATH + GOV_BUILDINGS_ICON_16,
                X_1000171: BUILDING_TYPE_CATEGORY_ICONS_PATH + GOV_BUILDINGS_ICON_16,
                X_1000172: BUILDING_TYPE_CATEGORY_ICONS_PATH + GOV_BUILDINGS_ICON_16,
                X_1000173: BUILDING_TYPE_CATEGORY_ICONS_PATH + GOV_BUILDINGS_ICON_16,
            }
            ;
        
        this.getBuildingTypeCategoryIcon24 = getBuildingTypeCategoryIcon24;
        this.getBuildingTypeCategoryIcon16 = getBuildingTypeCategoryIcon16;

        ////////////////

        function getBuildingTypeCategoryIcon24(buildingTypeCategory) {
            return BUILDING_TYPE_CATEGORY_ICONS_24[buildingTypeCategory] || BUILDING_TYPE_CATEGORY_ICONS_PATH + DEFAULT_ICON;
        }
        
        function getBuildingTypeCategoryIcon16(buildingTypeCategory) {
//console.log(buildingTypeCategory);            
//console.log(BUILDING_TYPE_CATEGORY_ICONS_16[buildingTypeCategory] || BUILDING_TYPE_CATEGORY_ICONS_PATH + DEFAULT_ICON);            
            return BUILDING_TYPE_CATEGORY_ICONS_16[buildingTypeCategory] || BUILDING_TYPE_CATEGORY_ICONS_PATH + DEFAULT_ICON;
        }
    }
})();