package io.github.intisy.ai.js.surface;

import io.github.intisy.ai.tsemit.TsModule;

/**
 * The JavaScript module surface {@link io.github.intisy.ai.js.StubTranslatorJs} exports, typed for a
 * TypeScript consumer.
 *
 * @implNote Never implemented, only emitted: {@link TsModule} renders its members as free functions,
 * which is the shape a TeaVM ES2015 module actually exports. The non-streaming members carry this
 * translator's wire JSON one way and core-ir's own IR JSON the other; the streaming pair hands back
 * a stateful handle instead, because a stream spans calls.
 */
@TsModule
public interface StubTranslatorSurface {

    /**
     * Stub wire JSON to an IR request.
     *
     * @param wireJson the request in this translator's own format
     * @return the canonical IR request
     */
    String stubDecodeRequest(String wireJson);

    /**
     * An IR request to stub wire JSON.
     *
     * @param irRequestJson the canonical IR request
     * @return the request in this translator's own format
     */
    String stubEncodeRequest(String irRequestJson);

    /**
     * Stub wire JSON to an IR response.
     *
     * @param wireJson the response in this translator's own format
     * @return the canonical IR response
     */
    String stubDecodeResponse(String wireJson);

    /**
     * An IR response to stub wire JSON.
     *
     * @param irResponseJson the canonical IR response
     * @return the response in this translator's own format
     */
    String stubEncodeResponse(String irResponseJson);

    /**
     * Opens a decode handle for one connection's stream.
     *
     * @return a handle carrying that connection's decode state
     */
    JsStreamDecoderHandle stubNewStreamDecoder();

    /**
     * Opens an encode handle for one connection's stream.
     *
     * @return a handle carrying that connection's encode state
     */
    JsStreamEncoderHandle stubNewStreamEncoder();
}
