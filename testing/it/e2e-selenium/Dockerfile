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


FROM docker-aem-sp.dr.corp.adobe.com/aem-qe/e2e-eaas/base

ENV IT_VERSION "0.0.1-SNAPSHOT"
ENV IT_PACKAGE "com.adobe.cq.wcm.launches.sites.it.seljup"
COPY --chown=seluser target/cq-ui-wcm-launches-it-e2e-seljup-${IT_VERSION}-it-jar-with-dependencies.jar ./e2e-it.jar
