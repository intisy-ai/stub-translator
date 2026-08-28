import { guardDocumentation, guardNoSuppressions } from "@intisy-ai/api/testing";

// generated/ is emitted from the Java surface and compared byte for byte by its own drift test, so
// it is not hand-documented here: its comments come from the Java it was emitted out of.
guardDocumentation({ dir: new URL("..", import.meta.url), skipFiles: ["stub-translator.teavm.d.ts"] });
guardNoSuppressions({ dir: new URL("..", import.meta.url) });
