# Core Components — LLM Wiki

This wiki documents subsystem-specific knowledge for the AEM WCM Core Components reactor.
Project-wide context (build, reactor layout, repo conventions) lives in the root
[`CLAUDE.md`](../../CLAUDE.md) — do not duplicate it here.

Each concept page focuses on one subsystem with invariants, resolution pipelines, and
source pointers. Read the relevant page before grepping the bundle.

## Concepts

- [Sling Model versioning and ImplementationPicker](concepts/sling-model-versioning.md) — how versioned `internal/models/vN/*Impl` classes are resolved at runtime.
- [Link resolution pipeline](concepts/link-resolution.md) — how `LinkManager` / `LinkBuilder` produce URLs and apply path processors and shadowing.
- [Adaptive Image servlet registration](concepts/adaptive-image-servlet.md) — how the image servlet is dynamically registered from OSGi factory configurations.
- [Data Layer integration](concepts/data-layer.md) — runtime contract between component models and the Adobe Client Data Layer.
- [Component policies via Sling CA-config](concepts/component-policies.md) — how content policies and CA-config reach component models.
- [Forms handling pipeline](concepts/forms-handling.md) — how `CoreFormHandlingServlet` intercepts POSTs to core form containers.
- [HTL components and AMP overrides](concepts/htl-components-and-amp.md) — how the `content/` package binds Sling models, and how AMP variants override scripts.
- [JSON model export (Sling Model Exporter)](concepts/json-model-export.md) — Jackson serialization rules for headless/SPA consumption.
