let modulePromise: Promise<typeof import("./generated/stub-translator.teavm.js")> | null = null;

/**
 * Loads the TeaVM-compiled reference translator module, once.
 *
 * @remarks
 * Concurrent callers share one import, so the module is instantiated exactly once per process.
 *
 * @returns the module, whose exports are the translator's own string functions
 */
export function loadStubTranslator(): Promise<typeof import("./generated/stub-translator.teavm.js")> {
  if (!modulePromise) {
    modulePromise = import("./generated/stub-translator.teavm.js");
  }
  return modulePromise;
}

export * from "./translators.js";
export * from "@intisy-ai/basekit/ir";
