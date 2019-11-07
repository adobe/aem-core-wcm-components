/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
(function() {
    "use strict";

    var animationDuration = 200;
    var delay = 2000;

    var states = {
        'NOTICE': 'notice',
        'SUCCESS': 'success',
        'ERROR': 'error',
        'WARNING': 'warning'
    };

    // font awesome icon key to state map
    var stateIconMap = {
        'notice': 'info-circle',
        'success': 'check',
        'error': 'frown-open',
        'warning': 'exclamation-triangle'
    };

    window.CmpExamples.Notification = window.CmpExamples.Notification || {};

    window.CmpExamples.Notification.show = function(text, state) {
        state = (!state) ? states.NOTICE : state;
        state = state.toLowerCase();

        var notification = document.createElement('div');
        var stateCssClass = 'cmp-examples-notification--' + state;
        notification.classList.add('cmp-examples-notification');
        notification.classList.add(stateCssClass);
        notification.innerHTML = '<i class="cmp-examples-notification__icon fas fa-' + stateIconMap[state] + '"></i><span class="cmp-examples-notification__text">' + text + '</span>';
        document.body.appendChild(notification);

        window.setTimeout(function() {
            notification.classList.add('cmp-examples-notification--out');
            window.setTimeout(function() {
                notification.parentNode.removeChild(notification);
            }, animationDuration);
        }, delay);
    };

    window.CmpExamples.Notification.state = states;

})();
