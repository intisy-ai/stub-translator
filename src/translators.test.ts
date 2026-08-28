import { describe, expect, it } from "vitest";
import { stubTranslator } from "./translators.js";

// Drives the TeaVM-compiled Java through the TypeScript wrapper, which is the only place the two
// halves of a translator meet. A unit test on either side alone would pass with the seam broken.
describe("the reference translator, across the TeaVM seam", () => {
  it("carries a model through a request round trip", async () => {
    const wire = await stubTranslator.encodeRequest({ model: "some-caller-model" } as never);
    const ir = await stubTranslator.decodeRequest(wire);
    expect(ir.model).toBe("some-caller-model");
  });

  it("falls back to its own model when the wire names none", async () => {
    expect((await stubTranslator.decodeRequest("{}")).model).toBe("stub-model");
  });

  it("reads an id and a model back off an encoded response", async () => {
    const ir = await stubTranslator.decodeResponse('{"id":"r-2","model":"m-2","text":"hi"}');
    expect(ir.id).toBe("r-2");
    expect(ir.model).toBe("m-2");
  });

  it("hands back both stream transforms, so the SPI is satisfied whole rather than in part", async () => {
    expect(await stubTranslator.decodeStream()).toBeInstanceOf(TransformStream);
    expect(await stubTranslator.encodeStream()).toBeInstanceOf(TransformStream);
  });

  it("carries the synchronous handles a Java routing engine reaches it through", () => {
    expect(stubTranslator.handles).toBeDefined();
  });
});
