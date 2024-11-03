package com.ps3ud0rand0m.burpx.hmac;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.http.handler.*;
        import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;

import static burp.api.montoya.http.handler.RequestToBeSentAction.continueWith;
import static burp.api.montoya.http.handler.ResponseReceivedAction.continueWith;
import static burp.api.montoya.http.message.params.HttpParameter.urlParameter;

public class Signer implements HttpHandler {
    private final Logging logging;

    public Signer(MontoyaApi api) {
        this.logging = api.logging();
    }


    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        Annotations annotations = requestToBeSent.annotations();
        HttpRequest modifiedRequest = requestToBeSent;

        if (isInScope(requestToBeSent)) {
            // Log stuff
            annotations = annotations.withNotes("Burpx-Status: Request was in scope.");
            logging.logToOutput(requestToBeSent.bodyToString());

            // Add a URL param
            //modifiedRequest = requestToBeSent.withAddedParameters(urlParameter("Burpx-Url-Param", "UrlParam"));

            // Add headers
            modifiedRequest = modifiedRequest.withAddedHeader("Burpx-Key-Was", "KeyValue");
        }

        // Return the modified request to burp with updated annotations.
        return continueWith(modifiedRequest, annotations);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        Annotations annotations = responseReceived.annotations();

        return continueWith(responseReceived, annotations);
    }

    private static boolean isInScope(HttpRequestToBeSent httpRequestToBeSent) {
        return httpRequestToBeSent.isInScope();
    }

    private static boolean isPost(HttpRequestToBeSent httpRequestToBeSent) {
        return httpRequestToBeSent.method().equalsIgnoreCase("POST");
    }

    private static boolean responseHasContentLengthHeader(HttpResponseReceived httpResponseReceived) {
        return httpResponseReceived.initiatingRequest().headers().stream().anyMatch(header -> header.name().equalsIgnoreCase("Content-Length"));
    }
}