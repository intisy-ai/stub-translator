package io.github.intisy.ai.ir.translators.stub;

import io.github.intisy.ai.ir.Block;
import io.github.intisy.ai.ir.IrRequest;
import io.github.intisy.ai.ir.IrResponse;
import io.github.intisy.ai.ir.IrStopReason;
import io.github.intisy.ai.ir.IrUsage;
import io.github.intisy.ai.ir.TextBlock;
import io.github.intisy.ai.ir.spi.StreamDecoder;
import io.github.intisy.ai.ir.spi.StreamEncoder;
import io.github.intisy.ai.ir.spi.Translator;
import io.github.intisy.ai.ir.stream.IrStreamEvent;

import java.util.Collections;
import java.util.List;

/**
 * The reference {@link Translator}: the smallest thing that satisfies the SPI, over a wire format
 * invented here rather than any real vendor's.
 *
 * @implNote Every method a vendor translator must answer is answered, so a reader sees the whole
 * shape of the contract in one file. The wire format carries the model and the response text and
 * nothing else, which is the least a caller can assert on and still prove the round trip happened.
 * A real vendor replaces the bodies; the surface stays exactly this.
 */
public final class StubTranslator implements Translator {

    /** The model this translator reports when a wire request names none. */
    public static final String MODEL = "stub-model";

    /** The id this translator reports for a response the wire did not name one for. */
    public static final String RESPONSE_ID = "stub-response";

    @Override
    public IrRequest decodeRequest(String wireJson) {
        IrRequest request = new IrRequest();
        String model = stringField(wireJson, "model");
        request.model = model != null ? model : MODEL;
        return request;
    }

    @Override
    public String encodeRequest(IrRequest request) {
        String model = request != null && request.model != null ? request.model : MODEL;
        return "{\"model\":" + quote(model) + "}";
    }

    @Override
    public IrResponse decodeResponse(String wireJson) {
        IrResponse response = new IrResponse();
        String model = stringField(wireJson, "model");
        String id = stringField(wireJson, "id");
        response.model = model != null ? model : MODEL;
        response.id = id != null ? id : RESPONSE_ID;
        response.stopReason = IrStopReason.END_TURN;
        response.usage = new IrUsage(0, 0, null, null);
        return response;
    }

    @Override
    public String encodeResponse(IrResponse response) {
        if (response == null) {
            return "{\"model\":" + quote(MODEL) + "}";
        }
        StringBuilder text = new StringBuilder();
        if (response.content != null) {
            for (Block block : response.content) {
                if (block instanceof TextBlock) {
                    text.append(((TextBlock) block).text);
                }
            }
        }
        return "{"
                + "\"id\":" + quote(response.id != null ? response.id : RESPONSE_ID) + ","
                + "\"model\":" + quote(response.model != null ? response.model : MODEL) + ","
                + "\"text\":" + quote(text.toString())
                + "}";
    }

    @Override
    public StreamDecoder newStreamDecoder() {
        return new StreamDecoder() {
            @Override
            public List<IrStreamEvent> decode(String chunk) {
                return Collections.emptyList();
            }
        };
    }

    @Override
    public StreamEncoder newStreamEncoder() {
        return new StreamEncoder() {
            @Override
            public String encode(IrStreamEvent event) {
                return "";
            }
        };
    }

    // Minimal string-field reader: this translator emits its own JSON, so a real parser would be
    // more machinery than the shape it has to read back. A vendor translator takes core-ir's
    // JsonCodec in its constructor instead.
    private static String stringField(String json, String key) {
        if (json == null) {
            return null;
        }
        int at = json.indexOf("\"" + key + "\"");
        if (at < 0) {
            return null;
        }
        int colon = json.indexOf(':', at + key.length() + 2);
        if (colon < 0) {
            return null;
        }
        int open = json.indexOf('"', colon + 1);
        if (open < 0) {
            return null;
        }
        StringBuilder out = new StringBuilder();
        boolean escaped = false;
        for (int i = open + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                out.append(c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                return out.toString();
            } else {
                out.append(c);
            }
        }
        return null;
    }

    private static String quote(String value) {
        StringBuilder out = new StringBuilder(value.length() + 2);
        out.append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '"' || c == '\\') {
                out.append('\\').append(c);
            } else if (c == '\n') {
                out.append("\\n");
            } else {
                out.append(c);
            }
        }
        out.append('"');
        return out.toString();
    }
}
