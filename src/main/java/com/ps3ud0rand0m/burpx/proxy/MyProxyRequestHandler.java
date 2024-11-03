package com.ps3ud0rand0m.burpx.proxy;

import burp.api.montoya.logging.Logging;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;
import burp.api.montoya.MontoyaApi;

import java.util.Arrays;
import java.util.List;

class MyProxyRequestHandler implements ProxyRequestHandler {

    private final Logging logging;

    public MyProxyRequestHandler(MontoyaApi api) {
        this.logging = api.logging();
    }

    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        // Print the intercepted request URL to standard output for debugging
        //logging.logToOutput("Intercepted URL: " + interceptedRequest.url());

        // Drop all post requests
        //if (interceptedRequest.method().equals("POST")) {
        //    return ProxyRequestReceivedAction.drop();
        //}

        // Drop requests with any string in the dropUrls list in the url
        if (shouldDropRequest(interceptedRequest.url())) {
            return ProxyRequestReceivedAction.drop();
        }

        // Drop requests with <string> in the url
        //if (interceptedRequest.url().contains("mydroptest")) {
        //    logging.logToOutput("Dropping URL: " + interceptedRequest.url());
        //    return ProxyRequestReceivedAction.drop();
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

    private boolean shouldDropRequest(String url) {
        // Hard-coded list of strings to drop
        List<String> dropUrls = Arrays.asList(
                "mydroptest",
                "analytics.com/",
                "doubleclick.net/",
                "drift.com/",
                "fullstory.com/",
                "analytics.google.com/",
                "telemetry.elastic.co/"
        );

        for (String dropUrl : dropUrls) {
            if (url.contains(dropUrl)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        // Do nothing with the user modified request, continue as normal.
        return ProxyRequestToBeSentAction.continueWith(interceptedRequest);
    }
}
