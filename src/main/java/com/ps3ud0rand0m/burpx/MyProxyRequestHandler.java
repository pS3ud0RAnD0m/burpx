package com.ps3ud0rand0m.burpx;

import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;

import static burp.api.montoya.core.HighlightColor.RED;
import static burp.api.montoya.http.message.ContentType.JSON;

class MyProxyRequestHandler implements ProxyRequestHandler {
    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        // Drop all post requests
        //if (interceptedRequest.method().equals("POST")) {
        //    return ProxyRequestReceivedAction.drop();
        //}

        // Drop requests with <string> in the url
        if (interceptedRequest.url().contains("/api/stats/automatic/")) {
            return ProxyRequestReceivedAction.drop();
        }

        // Intercept requests with <string> in the url
        //if (interceptedRequest.url().contains("foo")) {
        //    return ProxyRequestReceivedAction.intercept(interceptedRequest);
        //}

        // If the content type is json, highlight the request and follow burp rules for interception
        //if (interceptedRequest.contentType() == JSON) {
        //    return ProxyRequestReceivedAction.continueWith(interceptedRequest, interceptedRequest.annotations().withHighlightColor(RED));
        //}

        // Intercept all other requests
        //return ProxyRequestReceivedAction.intercept(interceptedRequest);

        // Do nothing
        return ProxyRequestReceivedAction.continueWith(interceptedRequest);
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return ProxyRequestToBeSentAction.continueWith(interceptedRequest);
    }
}