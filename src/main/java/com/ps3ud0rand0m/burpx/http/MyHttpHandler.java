package com.ps3ud0rand0m.burpx.http;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.utilities.ByteUtils;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static burp.api.montoya.http.handler.RequestToBeSentAction.continueWith;
import static burp.api.montoya.http.handler.ResponseReceivedAction.continueWith;

class MyHttpHandler implements HttpHandler {
    private final Logging logging;
    private final ByteUtils byteUtils;

    public MyHttpHandler(MontoyaApi api) {
        this.logging = api.logging();
        this.byteUtils = api.utilities().byteUtils();
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        // If post, log the body and add notes
        // Annotations annotations = requestToBeSent.annotations();
        // if (isPost(requestToBeSent)) {
        //     annotations = annotations.withNotes("Request was a post");
        //     logging.logToOutput(requestToBeSent.bodyToString());
        // }
        // return continueWith(modifiedRequest, annotations);

        // Modify the request if requestContains.
        // HttpRequest modifiedRequest = requestToBeSent;
        // String searchTerm = "foo";
        // if (requestContains(searchTerm, requestToBeSent)) {
        //     modifiedRequest = requestToBeSent.withAddedParameters(urlParameter("added", "matched"));
        // } else {
        //     modifiedRequest = requestToBeSent.withAddedParameters(urlParameter("added", "notmatched"));
        // }
        // return continueWith(modifiedRequest);

        // Match and replace string.
        String matchString = "foo";
        String replaceString = "fooReplaced";
        HttpRequest modifiedRequest = matchReplaceString(requestToBeSent, matchString, replaceString);
        return continueWith(modifiedRequest);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        //Annotations annotations = responseReceived.annotations();
        //// Highlight all responses with a Content-Length header.
        //if (responseHasContentLengthHeader(responseReceived)) {
        //    annotations = annotations.withHighlightColor(HighlightColor.RED);
        //}
        //return continueWith(responseReceived, annotations);
        return continueWith(responseReceived);
    }

    private HttpRequest matchReplaceString(HttpRequestToBeSent request, String matchString, String replaceString) {
        byte[] requestBytes = request.toByteArray().getBytes();
        String requestString = new String(requestBytes, StandardCharsets.UTF_8);
        String modifiedRequestString = requestString.replace(matchString, replaceString);
        HttpRequest modifiedRequest = HttpRequest.httpRequest(modifiedRequestString); // not working
        return modifiedRequest;
    }

    private static boolean requestContains(String searchTerm, HttpRequest request) {
        String requestString = new String(request.toByteArray().getBytes(), StandardCharsets.UTF_8);
        Pattern pattern = Pattern.compile(searchTerm, Pattern.DOTALL);
        return pattern.matcher(requestString).find();
    }

    private static boolean isPost(HttpRequestToBeSent httpRequestToBeSent) {
        return httpRequestToBeSent.method().equalsIgnoreCase("POST");
    }

    private static boolean responseHasContentLengthHeader(HttpResponseReceived httpResponseReceived) {
        return httpResponseReceived.initiatingRequest().headers().stream().anyMatch(header -> header.name().equalsIgnoreCase("Content-Length"));
    }
}