package io.github.intisy.ai.js;

import io.github.intisy.ai.ir.IrRequest;
import io.github.intisy.ai.ir.IrResponse;
import io.github.intisy.ai.ir.json.IrJson;
import io.github.intisy.ai.ir.json.SimpleJsonCodec;
import io.github.intisy.ai.ir.spi.JsonCodec;
import io.github.intisy.ai.ir.spi.StreamDecoder;
import io.github.intisy.ai.ir.spi.StreamEncoder;
import io.github.intisy.ai.ir.spi.Translator;
import io.github.intisy.ai.ir.stream.IrStreamEvent;
import io.github.intisy.ai.ir.translators.stub.StubTranslator;

import org.teavm.jso.JSExport;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSString;

import java.util.ArrayList;
import java.util.List;

/**
 * TeaVM JS export surface over the reference translator.
 *
 * @implNote Every export is a STRING function, because the routing engine reaches a translator
 * through a synchronous seam and JSON is the only thing that crosses it. A vendor translator's
 * surface is this file with the vendor's own translator substituted; nothing else about it changes,
 * which is what makes the six names below the whole contract a TS driver binds to.
 */
public final class StubTranslatorJs {
    private StubTranslatorJs() {
    }

    /**
     * Stub wire JSON to an IR request.
     *
     * @param wireJson the request in this translator's own format
     * @return the canonical IR request
     */
    @JSExport
    public static String stubDecodeRequest(String wireJson) {
        JsonCodec json = new SimpleJsonCodec();
        IrRequest request = new StubTranslator().decodeRequest(wireJson);
        return IrJson.serializeRequest(json, request);
    }

    /**
     * An IR request to stub wire JSON.
     *
     * @param irRequestJson the canonical IR request
     * @return the request in this translator's own format
     */
    @JSExport
    public static String stubEncodeRequest(String irRequestJson) {
        JsonCodec json = new SimpleJsonCodec();
        IrRequest request = IrJson.parseRequest(json, irRequestJson);
        return new StubTranslator().encodeRequest(request);
    }

    /**
     * Stub wire JSON to an IR response.
     *
     * @param wireJson the response in this translator's own format
     * @return the canonical IR response
     */
    @JSExport
    public static String stubDecodeResponse(String wireJson) {
        JsonCodec json = new SimpleJsonCodec();
        IrResponse response = new StubTranslator().decodeResponse(wireJson);
        return IrJson.serializeResponse(json, response);
    }

    /**
     * An IR response to stub wire JSON.
     *
     * @param irResponseJson the canonical IR response
     * @return the response in this translator's own format
     */
    @JSExport
    public static String stubEncodeResponse(String irResponseJson) {
        JsonCodec json = new SimpleJsonCodec();
        IrResponse response = IrJson.parseResponse(json, irResponseJson);
        return new StubTranslator().encodeResponse(response);
    }

    /** Stateful JS handle over one {@link StreamDecoder}: feed a raw chunk, get back IR events. */
    public interface JsStreamDecoderHandle extends JSObject {
        /**
         * Feeds one raw chunk.
         *
         * @param chunk the bytes as they arrived, at whatever boundary the transport gave them
         * @return the IR stream events the chunk completed, as a JSON array
         */
        JSString decode(JSString chunk);
    }

    /** Stateful JS handle over one {@link StreamEncoder}: feed one IR event, get back wire text. */
    public interface JsStreamEncoderHandle extends JSObject {
        /**
         * Encodes one IR stream event to this translator's wire text.
         *
         * @param irEventJson the IR stream event
         * @return the wire text to emit
         */
        JSString encode(JSString irEventJson);
    }

    /**
     * Opens a decode handle for one connection's stream.
     *
     * @return a handle carrying that connection's decode state
     */
    @JSExport
    public static JsStreamDecoderHandle stubNewStreamDecoder() {
        return newStreamDecoderHandle(new StubTranslator());
    }

    /**
     * Opens an encode handle for one connection's stream.
     *
     * @return a handle carrying that connection's encode state
     */
    @JSExport
    public static JsStreamEncoderHandle stubNewStreamEncoder() {
        return newStreamEncoderHandle(new StubTranslator());
    }

    private static JsStreamDecoderHandle newStreamDecoderHandle(Translator translator) {
        JsonCodec json = new SimpleJsonCodec();
        StreamDecoder decoder = translator.newStreamDecoder();
        return new JsStreamDecoderHandle() {
            @Override
            public JSString decode(JSString chunk) {
                String text = chunk == null ? "" : chunk.stringValue();
                List<IrStreamEvent> events = decoder.decode(text);
                List<Object> eventMaps = new ArrayList<>();
                for (IrStreamEvent event : events) eventMaps.add(IrJson.toMap(event));
                return JSString.valueOf(json.stringify(eventMaps));
            }
        };
    }

    private static JsStreamEncoderHandle newStreamEncoderHandle(Translator translator) {
        JsonCodec json = new SimpleJsonCodec();
        StreamEncoder encoder = translator.newStreamEncoder();
        return new JsStreamEncoderHandle() {
            @Override
            public JSString encode(JSString irEventJson) {
                String text = irEventJson == null ? "" : irEventJson.stringValue();
                IrStreamEvent event = IrJson.parseStreamEvent(json, text);
                return JSString.valueOf(encoder.encode(event));
            }
        };
    }
}
