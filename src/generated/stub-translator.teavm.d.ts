// Generated from Java sources. Do not edit.

/**
 * A stateful handle over one stream decode, as a TypeScript consumer sees it.
 *
 * @remarks
 * Never implemented, only emitted. The Java handle it describes speaks
 * `JSString` and extends `JSObject`, neither of which means anything to a TypeScript
 * caller, which is why this shape is declared apart from it rather than annotated onto it.
 */
export interface JsStreamDecoderHandle {
  /**
   * Feeds one raw chunk and returns the IR stream events it completed, as a JSON array.
   *
   * @param chunk - the bytes as they arrived, at whatever boundary the transport gave them
   * @returns the IR stream events the chunk completed, as a JSON array
   */
  decode(chunk: string): string;
}

/**
 * A stateful handle over one stream encode, as a TypeScript consumer sees it.
 *
 * @remarks
 * Never implemented, only emitted, for the same reason as its decode counterpart: the
 * Java handle speaks `JSString`, which means nothing to a TypeScript caller.
 */
export interface JsStreamEncoderHandle {
  /**
   * Encodes one IR stream event to this translator's wire text.
   *
   * @param irEventJson - the IR stream event
   * @returns the wire text to emit
   */
  encode(irEventJson: string): string;
}

/**
 * Stub wire JSON to an IR request.
 *
 * @param wireJson - the request in this translator's own format
 * @returns the canonical IR request
 */
export declare function stubDecodeRequest(wireJson: string): string;
/**
 * Stub wire JSON to an IR response.
 *
 * @param wireJson - the response in this translator's own format
 * @returns the canonical IR response
 */
export declare function stubDecodeResponse(wireJson: string): string;
/**
 * An IR request to stub wire JSON.
 *
 * @param irRequestJson - the canonical IR request
 * @returns the request in this translator's own format
 */
export declare function stubEncodeRequest(irRequestJson: string): string;
/**
 * An IR response to stub wire JSON.
 *
 * @param irResponseJson - the canonical IR response
 * @returns the response in this translator's own format
 */
export declare function stubEncodeResponse(irResponseJson: string): string;
/**
 * Opens a decode handle for one connection's stream.
 *
 * @returns a handle carrying that connection's decode state
 */
export declare function stubNewStreamDecoder(): JsStreamDecoderHandle;
/**
 * Opens an encode handle for one connection's stream.
 *
 * @returns a handle carrying that connection's encode state
 */
export declare function stubNewStreamEncoder(): JsStreamEncoderHandle;

