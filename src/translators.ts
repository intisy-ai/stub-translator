import { loadStubTranslator } from "./index.js";
import { makeVendorTranslator } from "@intisy-ai/basekit/ir";

/**
 * The reference translator, as every consumer takes it.
 *
 * @remarks
 * Built by basekit/ir's `makeVendorTranslator`, so it loads the TeaVM module lazily on first use and
 * carries the synchronous handles the Java routing engine reaches it through. The six names below
 * are the whole contract between a translator's Java surface and its TypeScript one: a vendor
 * translator differs from this file only in the prefix they carry.
 */
export const stubTranslator = makeVendorTranslator(loadStubTranslator, {
  decodeRequest: (m) => m.stubDecodeRequest,
  encodeRequest: (m) => m.stubEncodeRequest,
  decodeResponse: (m) => m.stubDecodeResponse,
  encodeResponse: (m) => m.stubEncodeResponse,
  newStreamDecoder: (m) => m.stubNewStreamDecoder,
  newStreamEncoder: (m) => m.stubNewStreamEncoder,
});
