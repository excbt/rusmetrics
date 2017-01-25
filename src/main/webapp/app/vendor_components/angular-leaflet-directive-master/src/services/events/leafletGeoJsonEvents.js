angular.module("leaflet-directive")
.factory('leafletGeoJsonEvents', function ($rootScope, $q, $log, leafletHelpers,
  leafletEventsHelpersFactory, leafletLabelEvents, leafletData) {
    var safeApply = leafletHelpers.safeApply,
        isDefined = leafletHelpers.isDefined,
        Helpers = leafletHelpers,
        lblHelp = leafletLabelEvents,
        EventsHelper = leafletEventsHelpersFactory;


    var GeoJsonEvents = function(){
      EventsHelper.call(this,'leafletDirectiveGeoJson', 'geojson');
    };

    GeoJsonEvents.prototype =  new EventsHelper();


    GeoJsonEvents.prototype.genDispatchEvent = function(eventName, logic, leafletScope, lObject, name, model, layerName, extra) {
        var base = EventsHelper.prototype.genDispatchEvent.call(this, eventName, logic, leafletScope, lObject, name, model, layerName),
        _this = this;

        return function(e){
            if (eventName === 'mouseout') {
                if (extra.resetStyleOnMouseout) {
                    leafletData.getGeoJSON(extra.mapId)
                    .then(function(leafletGeoJSON){
                        //this is broken on nested needs to traverse or user layerName (nested)
                        var lobj = layerName? leafletGeoJSON[layerName]: leafletGeoJSON;
                        lobj.resetStyle(e.target);
                    });

                }
                safeApply(leafletScope, function() {
                    $rootScope.$broadcast(_this.rootBroadcastName + '.mouseout', e);
                });
            }
            base(e); //common
        };
    };

    GeoJsonEvents.prototype.getAvailableEvents = function(){ return [
        'click',
        'dblclick',
        'mouseover',
        'mouseout',
        ];
    };

    return new GeoJsonEvents();
});
