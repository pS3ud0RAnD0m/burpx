package com.ps3ud0rand0m.burpx.http;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static burp.api.montoya.http.handler.RequestToBeSentAction.continueWith;
import static burp.api.montoya.http.handler.ResponseReceivedAction.continueWith;
import static burp.api.montoya.http.message.params.HttpParameter.urlParameter;

public class MyHttpHandlerTinker implements HttpHandler {
    private final Logging logging;

    public MyHttpHandlerTinker(MontoyaApi api) {
        this.logging = api.logging();
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        // Modify the request if match.
        HttpRequest modifiedRequest = requestToBeSent;
        String searchTerm = "foo";
        if (regexMatch(searchTerm, requestToBeSent)) {
            modifiedRequest = requestToBeSent.withAddedParameters(urlParameter("added", "matched"));
        } else {
            modifiedRequest = requestToBeSent.withAddedParameters(urlParameter("added", "notmatched"));
        }
        return continueWith(modifiedRequest);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        Annotations annotations = responseReceived.annotations();
        return continueWith(responseReceived, annotations);
    }

    private static boolean regexMatch(String searchTerm, HttpRequest request) {
        ByteArray requestBytes = request.toByteArray(); // Assuming toByteArray() gets the entire request as ByteArray
        byte[] bytes = requestBytes.getBytes(); // Correct method to get byte[] from ByteArray
        String requestString = new String(bytes, StandardCharsets.UTF_8); // Convert byte[] to String
        Pattern pattern = Pattern.compile(searchTerm, Pattern.DOTALL);
        return pattern.matcher(requestString).find();
    }
}