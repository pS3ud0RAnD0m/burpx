package com.ps3ud0rand0m.burpx;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import java.util.regex.Pattern;

import static burp.api.montoya.http.handler.RequestToBeSentAction.continueWith;
import static burp.api.montoya.http.handler.ResponseReceivedAction.continueWith;
import static burp.api.montoya.http.message.params.HttpParameter.urlParameter;

class MyHttpHandler implements HttpHandler {
    private final Logging logging;

    public MyHttpHandler(MontoyaApi api) {
        this.logging = api.logging();
    }


    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        Annotations annotations = requestToBeSent.annotations();

        // If the request is a post, log the body and add notes.
        //if (isPost(requestToBeSent)) {
        //    annotations = annotations.withNotes("Request was a post");
        //    logging.logToOutput(requestToBeSent.bodyToString());
        //}

        // Modify the request by adding url param.
        //HttpRequest modifiedRequest = requestToBeSent.withAddedParameters(urlParameter("added", "parameter"));

// Modify the request if regex matches.
        HttpRequest modifiedRequest = requestToBeSent;
        if (requestContainsRegex("foo", requestToBeSent)) {
            modifiedRequest = requestToBeSent.withAddedParameters(urlParameter("added", "matched"));
        } else {
            // This else block executes if the string does not match
            modifiedRequest = requestToBeSent.withAddedParameters(urlParameter("added", "notmatched"));
        }


        // Return the modified request to burp with updated annotations.
        return continueWith(modifiedRequest, annotations);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        Annotations annotations = responseReceived.annotations();
        // Highlight all responses with a Content-Length header.
        if (responseHasContentLengthHeader(responseReceived)) {
            annotations = annotations.withHighlightColor(HighlightColor.RED);
        }

        return continueWith(responseReceived, annotations);
    }

    private static boolean requestContainsRegex(String searchTerm, HttpRequestToBeSent httpRequestToBeSent) {
        Pattern pattern = Pattern.compile(searchTerm);
        return httpRequestToBeSent.contains(pattern);
    }

    private static boolean isPost(HttpRequestToBeSent httpRequestToBeSent) {
        return httpRequestToBeSent.method().equalsIgnoreCase("POST");
    }

    private static boolean responseHasContentLengthHeader(HttpResponseReceived httpResponseReceived) {
        return httpResponseReceived.initiatingRequest().headers().stream().anyMatch(header -> header.name().equalsIgnoreCase("Content-Length"));
    }
}