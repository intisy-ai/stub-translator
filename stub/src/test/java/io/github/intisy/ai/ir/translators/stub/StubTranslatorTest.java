package io.github.intisy.ai.ir.translators.stub;

import io.github.intisy.ai.ir.IrRequest;
import io.github.intisy.ai.ir.IrResponse;
import io.github.intisy.ai.ir.IrStopReason;
import io.github.intisy.ai.ir.TextBlock;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StubTranslatorTest {

    private final StubTranslator translator = new StubTranslator();

    @Test
    void carriesTheModelBothWaysThroughARequestRoundTrip() {
        IrRequest request = new IrRequest();
        request.model = "some-caller-model";

        IrRequest back = translator.decodeRequest(translator.encodeRequest(request));

        assertEquals("some-caller-model", back.model);
    }

    @Test
    void fallsBackToItsOwnModelWhenTheWireNamesNone() {
        assertEquals(StubTranslator.MODEL, translator.decodeRequest("{}").model);
        assertEquals(StubTranslator.MODEL, translator.decodeRequest(null).model);
    }

    @Test
    void carriesTheTextOfEveryTextBlockIntoTheEncodedResponse() {
        IrResponse response = new IrResponse();
        response.id = "r-1";
        response.model = "m-1";
        List<io.github.intisy.ai.ir.Block> content = new ArrayList<io.github.intisy.ai.ir.Block>();
        content.add(new TextBlock("first "));
        content.add(new TextBlock("second"));
        response.content = content;

        String wire = translator.encodeResponse(response);

        assertTrue(wire.contains("\"text\":\"first second\""), wire);
        assertTrue(wire.contains("\"id\":\"r-1\""), wire);
    }

    @Test
    void readsAnIdAndModelBackOffTheWire() {
        IrResponse back = translator.decodeResponse("{\"id\":\"r-2\",\"model\":\"m-2\",\"text\":\"hi\"}");

        assertEquals("r-2", back.id);
        assertEquals("m-2", back.model);
        assertEquals(IrStopReason.END_TURN, back.stopReason);
        assertNotNull(back.usage);
    }

    @Test
    void answersAStreamDecoderAndEncoder_soTheSpiIsSatisfiedWholeRatherThanInPart() {
        assertTrue(translator.newStreamDecoder().decode("anything").isEmpty());
        assertEquals("", translator.newStreamEncoder().encode(null));
    }

    @Test
    void escapesAQuoteRatherThanEmittingBrokenJson() {
        IrRequest request = new IrRequest();
        request.model = "a\"b";

        assertEquals("a\"b", translator.decodeRequest(translator.encodeRequest(request)).model);
    }
}
