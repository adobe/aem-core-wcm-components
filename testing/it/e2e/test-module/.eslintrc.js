/*
 *  Copyright 2020 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

module.exports = {
    extends: [
        'eslint:recommended',
        'plugin:wdio/recommended'
    ],
    env: {
        commonjs: true,
        es2017: true,
        node: true,
        mocha: true
    },
    parserOptions: {
        ecmaVersion: 9
    },
    rules: {
        'semi': ['error'],
        'semi-spacing': ['error', { before: false, after: true }],
        'semi-style': ['error', 'last'],
        'quotes': ['error', 'single'],
        'indent': ['error', 4],
        'no-trailing-spaces': ['error']
    },
    'plugins': [
        'wdio'
    ],
};
