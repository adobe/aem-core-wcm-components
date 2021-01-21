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
FROM node:lts-slim

ENV APP_PATH /usr/src/app
ENV SELENIUM_STARTUP_TIMEOUT 60

# Begin CloudManager Convention >>>
## Environment variables
ENV SELENIUM_BASE_URL http://localhost:4444
ENV SELENIUM_BROWSER chrome
ENV REPORTS_PATH reports
ENV AEM_AUTHOR_URL http://localhost:4502
ENV AEM_AUTHOR_USERNAME admin
ENV AEM_AUTHOR_PASSWORD admin
ENV AEM_PUBLISH_URL http://localhost:4503
ENV AEM_PUBLISH_USERNAME admin
ENV AEM_PUBLISH_PASSWORD admin
# Following convention variables are null by default
# ENV SHARED_FOLDER
# ENV UPLOAD_URL

## Install dependencies for wait_for_grid script
RUN apt -qqy update \
    && apt -qqy --no-install-recommends install \
    curl \
    jq
# <<< End CloudManager Convention

# Set Application Environment
WORKDIR ${APP_PATH}

# Copy + Install WDIO project
ENV TEST_RUN_COMMAND "npm run test-cloud"
COPY test-module/ ./
RUN npm install

# Begin CloudManager Convention >>>
## Copy helper to wait on Selenium readiness
COPY wait-for-grid.sh .
RUN chmod 555 wait-for-grid.sh

CMD ./wait-for-grid.sh ${TEST_RUN_COMMAND}
# <<< End CloudManager Convention
