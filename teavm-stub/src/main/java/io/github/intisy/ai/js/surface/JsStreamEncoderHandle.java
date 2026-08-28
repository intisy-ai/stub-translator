package io.github.intisy.ai.js.surface;

import io.github.intisy.ai.tsemit.TsInterface;

/**
 * A stateful handle over one stream encode, as a TypeScript consumer sees it.
 *
 * @implNote Never implemented, only emitted, for the same reason as its decode counterpart: the
 * Java handle speaks {@code JSString}, which means nothing to a TypeScript caller.
 */
@TsInterface
public interface JsStreamEncoderHandle {

    /**
     * Encodes one IR stream event to this translator's wire text.
     *
     * @param irEventJson the IR stream event
     * @return the wire text to emit
     */
    String encode(String irEventJson);
}
