/*******************************************************************************
 * Copyright 2022 Adobe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
// karma-browserify karma.conf.js

// Karma configuration
// Generated on Sat Dec 16 2017 16:32:55 GMT+0600 (Bangladesh Standard Time)
module.exports = function(config) {
    config.set({

        // base path that will be used to resolve all patterns (eg. files, exclude)
        basePath: '',


        // frameworks to use
        // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
        frameworks: ['jasmine','fixture'],

        // list of files / patterns to load in the browser
        files: [
            'test/mocks.js',
            'src/content/jcr_root/apps/core/wcm/components/commons/site/clientlibs/**/js/*.js',
            'src/content/jcr_root/apps/core/wcm/components/image/v3/image/clientlibs/site/**/js/*.js',
            'src/content/jcr_root/apps/core/wcm/components/contentfragment/v1/contentfragment/clientlibs/editor/authoring/js/editAction.js',
            'test/**/*Test.js',
            'test/**/*Test.html'
        ],


        // list of files to exclude
        exclude: [

        ],


        // preprocess matching files before serving them to the browser
        // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
        preprocessors: {
            '**/*.html'   : ['html2js'],
            'src/content/jcr_root/apps/core/wcm/components/commons/site/clientlibs/**/js/*.js': ['coverage']
        },
        // test results reporter to use
        // possible values: 'dots', 'progress'
        // available reporters: https://npmjs.org/browse/keyword/karma-reporter
        reporters: ['progress', 'coverage'],


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
        browsers: ['Chrome', 'ChromeHeadless', 'ChromeHeadlessCI'],
        customLaunchers: {
            ChromeHeadlessCI: {
                base: 'ChromeHeadless',
                flags: ['--no-sandbox']
            }
        },


        // Continuous Integration mode
        // if true, Karma captures browsers, runs the tests and exits
        singleRun: false,

        // Concurrency level
        // how many browser should be started simultaneous
        concurrency: Infinity,
        coverageReporter: {
            includeAllSources: true,
            type: 'lcov',
            dir: './coverage/'
        },
    })
}
