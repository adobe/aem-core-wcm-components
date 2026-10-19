# contentaisearch.js Jest harness

Standalone unit tests for `contentaisearch.js`'s request-handling logic (the
per-endpoint request-ID/in-flight-guard mechanism, Load More error handling,
and the toggle-off/gensearch invalidation race) — the concurrency-sensitive
code most likely to regress silently.

This is deliberately **separate** from the project's main JS test suite
(`content/karma.conf.js`, Karma + Jasmine, used for editor-side scripts) and
is **not** wired into `content/package.json`'s scripts, the Maven build, or
CI. It's a local, on-demand tool.

## Running

```sh
npm install
npm test
```

## How it works

`contentaisearch.js` is a self-executing IIFE with no exports — it scans the
DOM for `[data-cmp-is="contentaisearch"]` and wires up an instance per match
as soon as it loads. Each test builds a minimal DOM matching the component's
real markup (see `renderComponentHtml` in `contentaisearch.test.js`), resets
Jest's module registry, then `require()`s the real source file directly from
`content/src/content/jcr_root/.../clientlibs/site/js/contentaisearch.js` so
it discovers the fresh DOM and binds to it.

`global.fetch` is replaced with a controllable mock that queues pending
requests instead of resolving them immediately, so tests can resolve/reject
specific in-flight requests in any order to exercise the race conditions
directly (e.g. resolving a "Load More" append after a fresh search has
already superseded it).
