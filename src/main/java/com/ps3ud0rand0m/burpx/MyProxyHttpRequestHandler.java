package com.ps3ud0rand0m.burpx;

import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;

import static burp.api.montoya.core.HighlightColor.RED;
import static burp.api.montoya.http.message.ContentType.JSON;

class MyProxyHttpRequestHandler implements ProxyRequestHandler {
    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        //Drop all post requests
        if (interceptedRequest.method().equals("POST")) {
            return ProxyRequestReceivedAction.drop();
        }

        //Do not intercept any request with foo in the url
        if (interceptedRequest.url().contains("foo")) {
            return ProxyRequestReceivedAction.doNotIntercept(interceptedRequest);
        }

        //If the content type is json, highlight the request and follow burp rules for interception
        if (interceptedRequest.contentType() == JSON) {
            return ProxyRequestReceivedAction.continueWith(interceptedRequest, interceptedRequest.annotations().withHighlightColor(RED));
        }

        //Intercept all other requests
        return ProxyRequestReceivedAction.intercept(interceptedRequest);
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        //Do nothing with the user modified request, continue as normal.
        return ProxyRequestToBeSentAction.continueWith(interceptedRequest);
    }
}