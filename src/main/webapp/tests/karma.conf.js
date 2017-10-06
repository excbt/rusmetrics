// Karma configuration
// Generated on Fri May 26 2017 18:35:29 GMT+0400 (Московское время (лето))

module.exports = function(config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],      

    // list of files / patterns to load in the browser
    files: [
      '../app/bower_components/angular-1.4.1/angular.js',
      '../app/bower_components/angular-1.4.1/angular-mocks.js',
      '../app/vendor_components/RightsJS/right.js',
      '../app/vendor_components/RightsJS/right-resizable.js',      
      '../app/bower_components/jquery/dist/jquery.js',
      '../app/bower_components/bootstrap/dist/js/bootstrap.js',
      '../app/bower_components/angular-1.4.1/angular-animate.js',
      '../app/bower_components/angular-1.4.1/angular-cookies.js',
      '../app/bower_components/angular-1.4.1/angular-resource.js',
      '../app/bower_components/angular-1.4.1/angular-route.js',
      '../app/bower_components/angular-1.4.1/angular-sanitize.js',
      '../app/bower_components/angular-1.4.1/angular-touch.js',
      '../app/bower_components/jquery-ui/jquery-ui.js',
      '../app/bower_components/toastr/toastr.js',
      '../app/bower_components/moment/moment.js',
      '../app/bower_components/file-saver.js/FileSaver.js',
      '../app/bower_components/qtip2/jquery.qtip.js',
      '../app/bower_components/angular-file-upload/angular-file-upload.js',
      '../app/bower_components/angular-ui-select/dist/select.js',
      '../app/bower_components/bootstrap-toggle/js/bootstrap-toggle.js',
      '../app/bower_components/angular-ui-mask/dist/mask.js',
      '../app/bower_components/angular-widget/angular-widget.js',
      '../app/bower_components/chart.js/dist/Chart.js',
      '../app/bower_components/angular-chart.js/dist/angular-chart.js',
      '../app/bower_components/angular-daterangepicker/js/angular-daterangepicker.js',
      '../app/bower_components/bootstrap-daterangepicker/daterangepicker.js',
      '../app/bower_components/Blob/Blob.js',
      '../app/bower_components/jquery.inputmask/dist/jquery.inputmask.bundle.js',
      '../app/bower_components/angular-ui-tree/dist/angular-ui-tree.js',
      '../app/bower_components/angular-utils-pagination/dirPagination.js',
      '../app/bower_components/ng-idle/angular-idle.js',      
      '../app/vendor_components/flot/jquery.flot.js',
      '../app/vendor_components/flot/jquery.flot.pie.js',
      '../app/vendor_components/flot/jquery.flot.time.js',
      '../app/vendor_components/leaflet-1.0.0-b1/leaflet.js',
      '../app/vendor_components/angular-leaflet-directive-master/dist/angular-leaflet-directive.js',
      '../app/vendor_components/Leaflet.ExtraMarkers-master/src/leaflet.extra-markers.js',
      '../app/vendor_components/bootstrap-dropdowns-enhancement-master/dist/js/dropdowns-enhancement.js',      
      '../app/bower_components/jasmine-jquery/lib/jasmine-jquery.js',
      '../app/bower_components/angular-ui-layout/src/ui-layout.js',
      '../app/scripts/app.js',
      '../app/scripts/services/*.js',
      '../app/scripts/directives/*.js',
      '../app/scripts/controllers/*.js',
      '../tests/*Spec.js',
      '../tests/**/*Spec.js',

      {pattern: '../tests/mock/*.json', watched: true, serve: true, included: false}
    ],


    // list of files to exclude
    exclude: [
    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
    },


    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: ['Chrome'],// 'Firefox'],


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false,

    // Concurrency level
    // how many browser should be started simultaneous
    concurrency: Infinity,

    client: {
      captureConsole: true
    }
  })
}
