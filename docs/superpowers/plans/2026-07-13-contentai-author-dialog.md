# ContentAI Author Dialog & Multi-Source Search — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Enhance ContentAI Supported Search with an author dialog backed by `GET /content-sources`, multi-source parallel search with merged results, and visitor UX controls (`genSearchToggleVisible`, `genSearchErrorFallback`).

**Architecture:** Extend `ContentAIClient` with `listContentSources()` and typed search/gensearch calls. Add `ContentSourcesDataSourceServlet` (author-only) for Coral multi-select labels. Extend `ContentAISupportedSearch` model with multi-value `contentSources[]` + legacy `contentSource` fallback. Add `ContentSourceSearchMerger` for parallel fan-out in `ContentAISearchResultsServlet`. HTL/JS read new model properties for toggle visibility and error fallback.

**Tech Stack:** Java 11, Sling Models, OSGi DS, Apache HttpClient (`HttpClientBuilderFactory`), Jackson, Granite DataSource, JUnit 5 + Mockito + AEM Mocks, HTL, vanilla JS.

**Spec:** `docs/superpowers/specs/2026-07-13-contentai-author-dialog-design.md`

## Global constraints

- Resource type: `core/wcm/components/contentaisearch/v1/contentaisearch`
- List API: **`GET /content-sources` only** (no `/indexes`)
- Label: `indexName` + optional ` — ` + description (max 80 chars + `...`)
- `primaryContentSource` = first `contentSources[]` entry (auto, not in dialog)
- `genSearchErrorFallback` default: `RESULTS_ONLY`
- Bump `models/package-info.java` if public model API changes (`12.31.0` → `12.32.0`)
- No new Maven dependencies

## File map

| File | Responsibility |
|------|----------------|
| `services/contentai/ContentSourceListResult.java` | DTO for list API response |
| `services/contentai/ContentSourceListItem.java` | Single list item DTO |
| `services/contentai/ContentAIClient.java` | Add `listContentSources()`, overload search/genSearch with `type` |
| `internal/services/contentai/ContentAIClientImpl.java` | GET `/content-sources`; send `contentSource.type` in POST bodies |
| `internal/services/contentai/ContentSourceLabelFormatter.java` | Label formatting (80-char truncation) |
| `internal/services/contentai/ContentSourceSearchMerger.java` | Dedupe/merge/sort/limit results |
| `internal/servlets/contentaisearch/ContentSourcesDataSourceServlet.java` | Author dialog datasource |
| `models/ContentAISupportedSearch.java` | New property constants + getters |
| `internal/models/v1/ContentAISupportedSearchImpl.java` | Multi-value + legacy fallback |
| `internal/servlets/contentaisearch/ContentAISearchResultsServlet.java` | Multi-source orchestration |
| `internal/servlets/contentaisearch/ContentAIGenSearchServlet.java` | Use `getPrimaryContentSource()` |
| `content/.../_cq_dialog/.content.xml` | Three-tab dialog |
| `content/.../datasources/contentsources/.content.xml` | Datasource resource node |
| `content/.../contentaisearch.html` | New data attributes |
| `content/.../contentaisearch.js` | Toggle visibility + error fallback |
| Tests | Client, merger, formatter, datasource, model, servlet, JS |

---

### Task 1: List API DTOs + `ContentAIClient` API extension

**Files:**
- Create: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/services/contentai/ContentSourceListItem.java`
- Create: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/services/contentai/ContentSourceListResult.java`
- Modify: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/services/contentai/ContentAIClient.java`

- [ ] **Step 1: Create `ContentSourceListItem`**

Fields matching live API: `name`, `id` (optional), `description`, `type`, nested `config.access.public` via `@JsonIgnoreProperties(ignoreUnknown = true)` and inner `Config`/`Access` classes or `JsonNode` for `config` only in impl — prefer simple POJO:

```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentSourceListItem {
    private String name;
    private String id;
    private String description;
    private String type;
    private ContentSourceConfig config;
    // getters/setters
    public static class ContentSourceConfig {
        private ContentSourceAccess access;
    }
    public static class ContentSourceAccess {
        private boolean isPublic; // maps JSON "public" via @JsonProperty("public")
    }
}
```

- [ ] **Step 2: Create `ContentSourceListResult`**

```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentSourceListResult {
    private List<ContentSourceListItem> items = Collections.emptyList();
}
```

- [ ] **Step 3: Extend `ContentAIClient`**

```java
ContentSourceListResult listContentSources() throws ContentAIClientException;

ContentSourceSearchResult search(String contentSource, String contentSourceType, String query, int limit)
    throws ContentAIClientException;

ContentSourceQueryResult genSearch(String contentSource, String contentSourceType, String query)
    throws ContentAIClientException;
```

Keep existing `search(contentSource, query, limit)` / `genSearch(contentSource, query)` as default methods delegating to `ACQUISITION` for backward compatibility in tests.

- [ ] **Step 4: Run compile**

```bash
mvn -pl bundles/core -am compile -DskipTests
```

Expected: BUILD SUCCESS (impl will fail until Task 2 — add stub methods if needed).

---

### Task 2: `ContentSourceLabelFormatter` + tests

**Files:**
- Create: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/contentai/ContentSourceLabelFormatter.java`
- Create: `bundles/core/src/test/java/com/adobe/cq/wcm/core/components/internal/services/contentai/ContentSourceLabelFormatterTest.java`

- [ ] **Step 1: Write failing tests**

```java
@Test void nullDescription_returnsNameOnly() {
    assertEquals("aem-live", ContentSourceLabelFormatter.formatLabel("aem-live", null));
}
@Test void blankDescription_returnsNameOnly() {
    assertEquals("aem-live", ContentSourceLabelFormatter.formatLabel("aem-live", "   "));
}
@Test void shortDescription_appendsFull() {
    assertEquals("hotels-demo — Demo index",
        ContentSourceLabelFormatter.formatLabel("hotels-demo", "Demo index"));
}
@Test void longDescription_truncatesWithEllipsis() {
    String desc = "A".repeat(90);
    String label = ContentSourceLabelFormatter.formatLabel("x", desc);
    assertTrue(label.endsWith("..."));
    assertEquals("x — " + "A".repeat(80) + "...", label);
}
@Test void resolveIndexName_prefersNameOverId() {
    assertEquals("n", ContentSourceLabelFormatter.resolveIndexName("n", "id"));
    assertEquals("id", ContentSourceLabelFormatter.resolveIndexName("", "id"));
}
```

- [ ] **Step 2: Implement**

```java
public final class ContentSourceLabelFormatter {
    public static final int DESCRIPTION_LABEL_MAX_LENGTH = 80;
    public static String resolveIndexName(String name, String id) { ... }
    public static String formatLabel(String indexName, String description) { ... }
}
```

- [ ] **Step 3: Run tests**

```bash
mvn -pl bundles/core test -Dtest=ContentSourceLabelFormatterTest -DfailIfNoTests=false
```

Expected: PASS

---

### Task 3: `ContentSourceSearchMerger` + tests

**Files:**
- Create: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/contentai/ContentSourceSearchMerger.java`
- Create: `bundles/core/src/test/java/com/adobe/cq/wcm/core/components/internal/services/contentai/ContentSourceSearchMergerTest.java`

- [ ] **Step 1: Write failing tests** — merge two results with duplicate `id` (keep higher score), sort desc, limit 10, `totalResults` = merged list size, `cursor` null.

- [ ] **Step 2: Implement static `merge(List<ContentSourceSearchResult> partials, int limit)`**

- [ ] **Step 3: Run tests**

```bash
mvn -pl bundles/core test -Dtest=ContentSourceSearchMergerTest -DfailIfNoTests=false
```

---

### Task 4: `ContentAIClientImpl` — list + typed search

**Files:**
- Modify: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/contentai/ContentAIClientImpl.java`
- Modify: `bundles/core/src/test/java/com/adobe/cq/wcm/core/components/internal/services/contentai/ContentAIClientImplTest.java`

- [ ] **Step 1: Add failing test `listContentSourcesReturnsParsedItems`**

Mock `HttpGet` to `/content-sources`, response `{"items":[{"name":"aem-live","type":"ACQUISITION","config":{"access":{"public":true}}}]}`.

- [ ] **Step 2: Implement `listContentSources()`**

Use `HttpGet` with same headers as POST (`X-Api-Key`). Parse to `ContentSourceListResult`.

- [ ] **Step 3: Add failing test `searchIncludesContentSourceType`**

Assert POST body contains `"type":"ACQUISITION"`.

- [ ] **Step 4: Update `search` / `genSearch` to accept `contentSourceType`**

```java
body.putObject("contentSource").put("name", contentSource).put("type", contentSourceType);
```

- [ ] **Step 5: Run client tests**

```bash
mvn -pl bundles/core test -Dtest=ContentAIClientImplTest -DfailIfNoTests=false
```

---

### Task 5: `ContentSourcesDataSourceServlet` + tests

**Files:**
- Create: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/contentaisearch/ContentSourcesDataSourceServlet.java`
- Create: `content/.../datasources/contentsources/.content.xml`
- Create: `bundles/core/src/test/java/.../ContentSourcesDataSourceServletTest.java`

- [ ] **Step 1: Create datasource resource node**

```xml
<jcr:root jcr:primaryType="nt:unstructured"
    sling:resourceType="core/wcm/components/contentaisearch/v1/datasources/contentsources"/>
```

- [ ] **Step 2: Servlet** — register `RESOURCE_TYPE = "core/wcm/components/contentaisearch/v1/datasources/contentsources"`, methods GET, extension html.

Read request param `contentSourceType` (default `ACQUISITION`). Call `contentAIClient.listContentSources()`. Filter:
- `item.type` equals param (case-sensitive)
- `item.config.access.public == true` when `config.access` present

Build `SimpleDataSource` with `Resource` options (`value`/`text`). Use `ContentSourceLabelFormatter`.

On API failure: log error, return `EmptyDataSource`.

- [ ] **Step 3: Unit test** with mocked `ContentAIClient` + Sling mock request.

- [ ] **Step 4: Run test**

```bash
mvn -pl bundles/core test -Dtest=ContentSourcesDataSourceServletTest -DfailIfNoTests=false
```

---

### Task 6: Extend `ContentAISupportedSearch` model

**Files:**
- Modify: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/ContentAISupportedSearch.java`
- Modify: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/models/v1/ContentAISupportedSearchImpl.java`
- Modify: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/package-info.java` (bump to `12.32.0`)
- Modify: `bundles/core/src/test/resources/contentaisupportedsearch/test-content-dam.json`
- Modify: `bundles/core/src/test/java/.../ContentAISupportedSearchImplTest.java`

**New constants:**

```java
String PN_CONTENT_SOURCE_TYPE = "contentSourceType";
String PN_CONTENT_SOURCES = "contentSources";
String PN_PRIMARY_CONTENT_SOURCE = "primaryContentSource";
String PN_GENSEARCH_TOGGLE_VISIBLE = "genSearchToggleVisible";
String PN_GENSEARCH_ERROR_FALLBACK = "genSearchErrorFallback";
String GENSEARCH_ERROR_FALLBACK_RESULTS_ONLY = "RESULTS_ONLY";
// ...
```

**New getters:**

```java
@NotNull String getContentSourceType();          // default ACQUISITION
@NotNull List<String> getContentSources();       // legacy fallback from contentSource
@NotNull String getPrimaryContentSource();       // contentSources[0] or legacy contentSource
boolean isGenSearchToggleVisible();             // default true
@NotNull String getGenSearchErrorFallback();    // default RESULTS_ONLY
```

`getContentSource()` — keep, return `getPrimaryContentSource()` for backward compat.

- [ ] **Step 1: Update test JSON** with `contentSources`, `contentSourceType`, new props.

- [ ] **Step 2: Update tests** including legacy-only `contentSource` node.

- [ ] **Step 3: Implement model**

Use `@ValueMapValue(name = PN_CONTENT_SOURCES)` `String[] contentSources` or `List<String>`.

`@PostConstruct`: if `contentSources` empty && `contentSource` set → use singleton list.

- [ ] **Step 4: Run tests**

```bash
mvn -pl bundles/core test -Dtest=ContentAISupportedSearchImplTest -DfailIfNoTests=false
```

---

### Task 7: Multi-source search servlet + GenSearch primary source

**Files:**
- Modify: `ContentAISearchResultsServlet.java`
- Modify: `ContentAIGenSearchServlet.java`
- Modify: `ContentAISearchResultsServletTest.java`
- Modify: `ContentAIGenSearchServletTest.java`

- [ ] **Step 1: `ContentAISearchResultsServlet.executeQuery`**

```java
List<String> sources = model.getContentSources();
if (sources.isEmpty()) { throw or empty result }
List<ContentSourceSearchResult> partials = new ArrayList<>();
for (String source : sources) {
    partials.add(contentAIClient.search(source, model.getContentSourceType(), query, model.getResultsSize()));
}
return ContentSourceSearchMerger.merge(partials, model.getResultsSize());
```

Note: for true parallelism use `ExecutorService` or parallel stream — v1 can use sequential loop in servlet with merger tested independently; optional parallel stream if low risk.

- [ ] **Step 2: `ContentAIGenSearchServlet`**

```java
return contentAIClient.genSearch(model.getPrimaryContentSource(), model.getContentSourceType(), query);
```

- [ ] **Step 3: Update servlet tests** for multi-source + primary.

- [ ] **Step 4: Run servlet tests**

```bash
mvn -pl bundles/core test -Dtest=ContentAISearchResultsServletTest,ContentAIGenSearchServletTest -DfailIfNoTests=false
```

---

### Task 8: Author dialog (three tabs)

**Files:**
- Modify: `content/.../contentaisearch/v1/contentaisearch/_cq_dialog/.content.xml`

- [ ] **Tab: Content scope**
  - `contentSourceType` — static select items: ACQUISITION, AEM_AUTHOR, AEM_PUBLISH, CUSTOM (default ACQUISITION)
  - `contentSources` — `granite/ui/components/coral/foundation/form/select`, `multiple="{Boolean}true"`, `name="./contentSources"`, required
  - datasource child: `core/wcm/components/contentaisearch/v1/datasources/contentsources` with `contentSourceType="${param.contentSourceType}"` or granite:data linkage

- [ ] **Tab: Search behavior** — move `resultsSize`, `placeholder`

- [ ] **Tab: Generative search** — `genSearchToggleVisible`, `genSearchEnabledByDefault`, `genSearchErrorFallback` select (RESULTS_ONLY, SHOW_ERROR, SHOW_ERROR_MESSAGE), `disclaimerText`

- [ ] Remove legacy single `contentSource` textfield.

---

### Task 9: HTL + clientlib JS

**Files:**
- Modify: `contentaisearch.html`
- Modify: `contentaisearch.js`

- [ ] **HTL** — add attributes:

```html
data-cmp-gensearch-toggle-visible="${search.genSearchToggleVisible}"
data-cmp-gensearch-error-fallback="${search.genSearchErrorFallback}"
```

Wrap toggle label in `data-sly-test="${search.genSearchToggleVisible}"`.

When toggle hidden, set `data-cmp-gensearch-enabled-default` and JS initializes fixed behavior.

- [ ] **JS `_runGenSearch` catch** — read `genSearchErrorFallback`:
  - `RESULTS_ONLY`: hide summary + error (no-op)
  - `SHOW_ERROR`: current behavior
  - `SHOW_ERROR_MESSAGE`: show error text, hide retry button

- [ ] **JS init** — if toggle not in DOM, use `data-cmp-gensearch-enabled-default` only.

---

### Task 10: Examples QA page + full verification

**Files:**
- Modify: `examples/ui.content/.../qa-contentai-search/.content.xml`

- [ ] Update component props: `contentSources="[aem-live]"`, `contentSourceType="ACQUISITION"`, `genSearchErrorFallback="RESULTS_ONLY"`.

- [ ] **Run full core tests + content build**

```bash
mvn clean test -pl bundles/core -am -DfailIfNoTests=false
mvn clean package -pl content -am -DskipTests
```

Expected: BUILD SUCCESS, baseline passes (`package-info` bumped).

- [ ] **Deploy to local SDK** (if instances up)

```bash
mvn install -PautoInstallPackage,autoInstallPackagePublish -pl bundles/core,content,examples/ui.content -am -DskipTests
```

---

### Task 11: Update manual QA doc

**Files:**
- Modify: `docs/superpowers/plans/2026-07-09-contentai-supported-search-manual-qa.md`

- [ ] Replace `/indexes` references with `/content-sources` for list API discovery.

---

## Spec coverage self-review

| Spec section | Task |
|--------------|------|
| §5 `/content-sources` list API | Task 1, 4, 5 |
| §5.4 label formatting | Task 2, 5 |
| §6 dialog fields | Task 8 |
| §6 legacy migration | Task 6 |
| §7 multi-source merge | Task 3, 7 |
| §7 GenSearch primary | Task 6, 7 |
| §6 genSearchToggleVisible | Task 6, 9 |
| §6 genSearchErrorFallback | Task 6, 9 |
| §10 public filter | Task 5 |
| §11 tests | All task tests |

## Deferred (not in this plan)

- Search mode (hybrid/vector/fulltext) author field
- Parallel `ExecutorService` (optional enhancement; sequential fan-out acceptable v1.1)
- Dialog live-refresh when type changes without save
