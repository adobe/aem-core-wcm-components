#!/bin/bash

# Copyright 2020 Adobe Systems Incorporated
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#
# DO NOT MODIFY
#

# wait-for-grid.sh
set -e

cmd="$@"

# Remove trailing slash
SELENIUM_BASE_URL=${SELENIUM_BASE_URL%/}

while ! (curl -sSL "${SELENIUM_BASE_URL}/wd/hub/status" 2>&1 \
        | jq -r '.value.ready' 2>&1 | grep "true" >/dev/null) && [[ "$SECONDS" -lt ${SELENIUM_STARTUP_TIMEOUT} ]]; do
    echo 'Waiting for the Grid'
    sleep 1
done

>&2 echo "Selenium Grid is up - executing tests"

exec $cmd
