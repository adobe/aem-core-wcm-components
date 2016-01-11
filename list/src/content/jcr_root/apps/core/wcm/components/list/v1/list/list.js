/*******************************************************************************
 * Copyright 2015 Adobe Systems Incorporated
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
/*global use, resource, request, log, properties, Packages */
use([
        '/libs/wcm/foundation/components/utils/v2/AuthoringUtils.js',
        '/libs/wcm/foundation/components/utils/v2/Constants.js',
        '/libs/wcm/foundation/components/utils/v2/ResourceUtils.js'
    ],
    function (AuthoringUtils, Constants, ResourceUtils) {
        'use strict';
        var result               = {},
            resolver             = resource.getResourceResolver();
        const PROP_SOURCE = 'listFrom',
            PROP_QUERY           = 'query',
            PROP_SEARCH_IN       = 'searchIn',
            PROP_TYPE            = 'displayAs',
            PROP_ORDER_BY        = 'orderBy',
            PROP_LIMIT           = 'limit',
            PROP_PAGE_MAX        = 'pageMax',
            PROP_ORDERED         = 'ordered',
            PROP_PARENT_PAGE     = 'parentPage',
            PROP_FEED_ENABLED    = 'feedEnabled',
            PROP_SAVED_QUERY     = 'savedquery',
            PROP_TAG_SEARCH_ROOT = 'tagsSearchRoot',
            PROP_TAGS            = 'tags',
            PROP_TAGS_MATCH      = 'tagsMatch',
            PROP_PAGES           = 'pages',
            PROP_ITEM_TYPE       = 'itemType',
            PROP_LIST_CSS_CLASS  = 'listCSSClass',

            PARAM_PAGE_START     = 'start',
            PARAM_PAGE_MAX       = 'max',

            SOURCE_CHILDREN      = 'children',
            SOURCE_DESCENDANTS   = 'descendants',
            SOURCE_STATIC        = 'static',
            SOURCE_SEARCH        = 'search',
            SOURCE_QUERYBUILDER  = 'querybuilder',
            SOURCE_TAGS          = 'tags',

            TYPE_DEFAULT         = 'default',

            PAGE_MAX_DEFAULT     = -1,
            LIMIT_DEFAULT        = 100;

        /**
         *
         * @returns {{}}
         * @private
         */
        var _getListConfiguration = function () {
            var currentListId     = _getGeneratedId(),
                listConfiguration = {};

            listConfiguration.source       = properties.get(PROP_SOURCE) || SOURCE_CHILDREN;
            listConfiguration.query  = properties.get(PROP_QUERY);
            listConfiguration.startIn = properties.get(PROP_SEARCH_IN) || ResourceUtils.getAbsoluteParent(resource.path, 1);
            listConfiguration.type    = properties.get(PROP_TYPE) || TYPE_DEFAULT;
            listConfiguration.orderBy = properties.get(PROP_ORDER_BY) || '';
            listConfiguration.limit   = properties.get(PROP_LIMIT) || LIMIT_DEFAULT;
            listConfiguration.ordered = properties.get(PROP_ORDERED) || '';
            listConfiguration.feedEnabled = properties.get(PROP_FEED_ENABLED);
            listConfiguration.pageMax     = properties.get(PROP_PAGE_MAX) || PAGE_MAX_DEFAULT;
            listConfiguration.itemType    = properties.get(PROP_ITEM_TYPE) || '';
            listConfiguration.listCSSClass = properties.get(PROP_LIST_CSS_CLASS) || '';
            listConfiguration.pageStart    = 0;
            listConfiguration.id           = currentListId;

            listConfiguration.limit = parseInt(listConfiguration.limit);
            if (listConfiguration.limit <= 0) {
                listConfiguration.limit = LIMIT_DEFAULT;
            }

            listConfiguration.pageMax = parseInt(listConfiguration.pageMax);
            if (listConfiguration.pageMax <= 0) {
                listConfiguration.pageMax = PAGE_MAX_DEFAULT;
            }

            if (!listConfiguration.feedEnabled) {
                listConfiguration.feedEnabled = false;
            }

            if (request.getRequestParameter(_getListSpecificParamName(PARAM_PAGE_START, currentListId))) {
                listConfiguration.pageStart = parseInt(request.getRequestParameter(
                    _getListSpecificParamName(PARAM_PAGE_START, currentListId)).getString()
                );
                if (listConfiguration.pageStart <= 0) {
                    listConfiguration.pageStart = 0;
                }
            }

            if (request.getRequestParameter(_getListSpecificParamName(PARAM_PAGE_MAX, currentListId))) {
                listConfiguration.pageMax = parseInt(request.getRequestParameter(
                    _getListSpecificParamName(PARAM_PAGE_START, currentListId)).getString()
                );
                if (listConfiguration.pageMax <= 0) {
                    listConfiguration.pageMax = 0;
                }
            }

            if (log.isDebugEnabled()) {
                Object.getOwnPropertyNames(listConfiguration).forEach(function (property) {
                    log.debug('List config - ' + property + ' = ' + listConfiguration[property]);
                });
            }

            return listConfiguration;
        };

        var _getGeneratedId = function () {
            var resPath = resource.path,
                pgRoot  = "jcr:content/",
                root    = resPath.indexOf(pgRoot);
            if (root >= 0) {
                resPath = resPath.substring(root + pgRoot.length);
            }
            return resPath.replace('/', '_');
        };

        var _getListSpecificParamName = function (paramName, listId) {
            return listId + '_' + paramName;
        };

        var _getModifiedDate = function (resource) {
            var resourceProperties = ResourceUtils.getResourceProperties(resource),
                dateProperty       = resourceProperties.get('date'),
                date               = '';
            if (!dateProperty) {
                resourceProperties = ResourceUtils.getPageProperties(resource);
                dateProperty       =
                    resourceProperties.get(Constants.CQ_LAST_MODIFIED) || resourceProperties.get(Constants.JCR_LAST_MODIFIED);
            }
            try {
                var month     = dateProperty.get(Packages.java.util.Calendar.MONTH) + 1;
                var day   = dateProperty.get(Packages.java.util.Calendar.DAY_OF_MONTH);
                var year  = dateProperty.get(Packages.java.util.Calendar.YEAR);
                var hourOfDay = dateProperty.get(Packages.java.util.Calendar.HOUR_OF_DAY);
                var minutes   = dateProperty.get(Packages.java.util.Calendar.MINUTE);
                return month + "/" + day + "/" + year + " " + hourOfDay + ":" + minutes;
            } catch (e) {
                log.error('Cannot determine page ' + resource.path + ' modification date: ' + e);
                return date;
            }
        };

        var _hasImage = function (resource) {
            var jcrContent           = resolver.getResource(resource, Constants.JCR_CONTENT),
                jcrContentProperties = ResourceUtils.getResourceProperties(jcrContent),
                imageResource        = resolver.getResource(jcrContent, 'image'),
                imageResourceProperties,
                imageResourceFile;
            if (jcrContentProperties.get('fileReference')) {
                return true;
            } else {
                if (imageResource) {
                    imageResourceProperties = ResourceUtils.getResourceProperties(imageResource);
                    if (imageResourceProperties.get('fileReference')) {
                        return true;
                    } else {
                        imageResourceFile = resolver.getResource(imageResource, 'file');
                        if (imageResourceFile) {
                            return true;
                        }
                    }
                }
            }
            return false;
        };

        var _getRequestQueryString = function (replacedParameter, replacedParameterValue) {
            var queryString       = '',
                requestParameters = request.getRequestParameterList().toArray(),
                param,
                paramValue,
                i,
                found             = false;
            for (i = 0; i < requestParameters.length; i++) {
                param      = requestParameters[i];
                paramValue = param.getString();
                if (param === replacedParameter) {
                    found      = true;
                    paramValue = replacedParameterValue;
                }
                queryString += param + '=' + paramValue + '&';
            }
            if (!found) {
                queryString += replacedParameter + '=' + replacedParameterValue;
            } else if (queryString.length > 0) {
                queryString = queryString.substr(0, queryString.length - 1);
            }
            return queryString;
        };

        var _getNextPage = function (configuration, hasMore) {
            var nextPage = '';
            if (hasMore) {
                var startPoint  = configuration.pageStart + configuration.pageMax,
                    queryString = _getRequestQueryString(_getListSpecificParamName(PARAM_PAGE_START, configuration.id), startPoint);
                nextPage        = resource.path + '.html?' + queryString;
            }
            return nextPage;
        };

        var _getPreviousPage = function (configuration) {
            var previousPage = '';
            if (configuration.pageStart > 0) {
                var startPoint  = configuration.pageMax > 0 && configuration.pageStart > configuration.pageMax ?
                    configuration.pageStart - configuration.pageMax : 0,
                    queryString = _getRequestQueryString(_getListSpecificParamName(PARAM_PAGE_START, configuration.id), startPoint);
                previousPage    = resource.path + '.html?' + queryString;
            }
            return previousPage;
        };

        var _collectChildRenderItems = function (configuration, renderItems) {
            var parentPath     = properties.get(PROP_PARENT_PAGE) || resource.path,
                parentResource = resolver.getResource(parentPath);
            var containingPage = ResourceUtils.getContainingPage(parentResource);
            if (containingPage) {
                var childrenList = containingPage.getChildren(),
                    childIndex,
                    child,
                    list         = [];
                // remove the jcr:content child, if it exists
                for (childIndex = 0; childIndex < childrenList.length; childIndex++) {
                    child = childrenList[childIndex];
                    if (child.name !== Constants.JCR_CONTENT) {
                        list.push(child);
                    }
                }
                _addItems(list, configuration, renderItems);
            }
        };

        var _traverseIterator = function (iterator, configuration, renderItems) {
            var nextItem,
                resourcePath,
                iteratedResource,
                itemList = [];
            try {
                if (iterator && !iterator.hasNext()) {
                    log.debug('Empty iterator');
                }
                while (iterator && iterator.hasNext()) {
                    nextItem = iterator.next();
                    if (nextItem.getResource) {
                        resourcePath = nextItem.getResource().getPath();
                    } else if (nextItem.getPath) {
                        resourcePath = nextItem.getPath();
                    } else {
                        log.warn('Cannot determine search item path ' + nextItem);
                        resourcePath = nextItem;
                    }
                    iteratedResource = resolver.getResource(resourcePath);
                    itemList.push(iteratedResource);
                }
                _addItems(itemList, configuration, renderItems);
            } catch (e) {
                log.error(e);
            }
        };

        var _collectSimpleSearchItems = function (configuration, renderItems) {
            if (configuration.query) {
                var search        = resource.adaptTo(Packages.com.day.cq.search.SimpleSearch),
                    pagePredicate = Packages.com.day.cq.search.Predicate('type', 'type'),
                    searchResult,
                    hitsIterator;
                pagePredicate.set('type', 'cq:Page');
                search.addPredicate(pagePredicate);
                search.setHitsPerPage(configuration.limit);
                search.setQuery(configuration.query);
                search.setSearchIn(configuration.startIn);
                searchResult = search.getResult();
                if (searchResult && searchResult.getHits) {
                    hitsIterator = searchResult.getHits().iterator();
                    _traverseIterator(hitsIterator, configuration, renderItems);

                }
            }
        };

        var _collectQueryBuilderSearchItems = function (configuration, renderItems) {
            var session      = resolver.adaptTo(Packages.javax.jcr.Session),
                queryBuilder = resolver.adaptTo(Packages.com.day.cq.search.QueryBuilder),
                query        = queryBuilder.loadQuery(resource.path + '/' + PROP_SAVED_QUERY, session),
                searchResult,
                hitsIterator;
            if (query) {
                query.setHitsPerPage(configuration.limit);
                searchResult = query.getResult();
                if (searchResult && searchResult.getHits) {
                    hitsIterator = searchResult.getHits().iterator();
                    _traverseIterator(hitsIterator, configuration, renderItems);
                }
            }
        };

        var _collectTagSearchItems = function (configuration, renderItems) {
            var parentPath = properties.get(PROP_TAG_SEARCH_ROOT) || resource.path,
                tags       = properties.get(PROP_TAGS) || [],
                matchAny   = properties.get(PROP_TAGS_MATCH) == 'any',
                startPage  = ResourceUtils.getContainingPage(resolver.getResource(parentPath));
            if (tags.length > 0) {
                var tagManager         = resolver.adaptTo(Packages.com.day.cq.tagging.TagManager),
                    tagResultsIterator = tagManager.find(startPage.path, tags, matchAny);
                _traverseIterator(tagResultsIterator, configuration, renderItems);
            }
        };

        var _collectFixedPathItems = function (configuration, renderItems) {
            var pagePaths = properties.get(PROP_PAGES) || [],
                i,
                itemResource,
                itemList  = [];
            if (typeof pagePaths.length == 'function') {
                pagePaths = [pagePaths];
            }
            for (i = 0; i < pagePaths.length; i++) {
                itemResource = resolver.getResource(pagePaths[i]);
                if (itemResource) {
                    itemList.push(itemResource);
                }
            }
            _addItems(itemList, configuration, renderItems);
        };

        var _addItems = function (itemList, configuration, renderItems) {
            var i,
                item,
                collector = [],
                count     = 0,
                page,
                pageProperties;
            if (!itemList) {
                return;
            }
            for (i = 0; i < itemList.length; i++) {
                item = itemList[i];
                if (i >= configuration.limit) {
                    break;
                }
                if (configuration.pageStart >= 0 && i < configuration.pageStart) {
                    continue;
                }

                if (configuration.pageMax > 0 && count >= configuration.pageMax) {
                    renderItems.hasMore = true;
                    break;
                }
                page           = ResourceUtils.getContainingPage(item);
                pageProperties = ResourceUtils.getPageProperties(page);
                count++;
                collector.push({
                    item     : {
                        path       : page.path,
                        orderByData: pageProperties.get(configuration.orderBy),
                        description: pageProperties.get(Constants.JCR_DESCRIPTION, '')
                    },
                    itemName: pageProperties.get(Constants.JCR_TITLE),
                    modifdate: _getModifiedDate(page),
                    hasimage : _hasImage(page)
                });
            }
            if (collector.length > 0) {
                if (configuration.orderBy) {
                    collector.sort(function (elem1, elem2) {
                        var prop1 = elem1.item.orderByData || elem2.item.orderByData || '';
                        var prop2 = elem2.item.orderByData || elem1.item.orderByData || '';
                        log.debug('[Sorting list] Comparing ' + prop1 + ' with ' + prop2);
                        if (prop1.compareTo) {
                            log.debug('[Sorting list] Using "compareTo" method for comparison');
                            return prop1.compareTo(prop2);
                        } else if (prop1.localeCompare) {
                            log.debug('[Sorting list] Using "localeCompare" method for comparison');
                            return prop1.localeCompare(prop2);
                        }
                        return -1;
                    });
                }
            }
            renderItems.renderList = collector;
        };

        var _fetchRenderItems = function (configuration) {
            var renderItems        = {};
            renderItems.renderList = [];
            renderItems.hasMore    = false;
            log.debug('Source: ' + configuration.source);
            if (configuration.source == SOURCE_CHILDREN || configuration.source == SOURCE_DESCENDANTS) {
                log.debug('Building list from child items.')
                _collectChildRenderItems(configuration, renderItems);
            } else if (configuration.source == SOURCE_SEARCH) {
                log.debug('Building list from simple search.');
                _collectSimpleSearchItems(configuration, renderItems);
            } else if (configuration.source == SOURCE_QUERYBUILDER) {
                log.debug('Building list from advanced query builder search');
                _collectQueryBuilderSearchItems(configuration, renderItems);
            } else if (configuration.source == SOURCE_TAGS) {
                log.debug('Building list from tag items');
                _collectTagSearchItems(configuration, renderItems);
            } else {
                log.debug('Building list from a fixed set of items');
                _collectFixedPathItems(configuration, renderItems);
            }
            return renderItems;
        };

        var configuration = _getListConfiguration(),
            listElement   = configuration.ordered === true || configuration.ordered === 'true' ? 'ol' : 'ul',
            renderItems   = _fetchRenderItems(configuration);

        result.list                            = renderItems.renderList;
        result.element = listElement;
        result.type    = configuration.type;
        result.isTouch = AuthoringUtils.isTouch;
        result.isEmpty = renderItems.renderList.length === 0;
        result.isPaginating = true;
        result.nextLink     = _getNextPage(configuration, renderItems.hasMore);
        result.previousLink = _getPreviousPage(configuration);
        result.accessibleNextDescriptionId = properties.get('accessibleNext') ? 'cq_' + configuration.id + '_next' : '';
        result.accessiblePreviousDescriptionId = properties.get('accessiblePrevious') ? 'cq_' + configuration.id + '_previous' : '';
        result.listId                          = configuration.id;
        result.pageStart                       = configuration.pageStart;
        result.itemType                        = configuration.itemType;
        result.listCSSClass                    = configuration.listCSSClass;

        return result;

    }
);
