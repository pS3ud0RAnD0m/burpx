
package com.ps3ud0rand0m.burpx.hmac;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.handler.HttpHandler;
import burp.api.montoya.http.handler.RequestToBeSentAction;
import burp.api.montoya.http.handler.HttpRequestToBeSent;
import burp.api.montoya.http.handler.ResponseReceivedAction;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class Signer implements HttpHandler {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String KNOWN_GOOD_KEY = "known-good-key";  // Replace with actual key

    public Signer(MontoyaApi api) {
        api.logging().logToOutput("Signer initialized");
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent request) {
        String signature = generateHMACSignature(request.toString(), KNOWN_GOOD_KEY);
        HttpRequest modifiedRequest = request.withAddedHeader("X-Burpx-Signed", KNOWN_GOOD_KEY)
                .withAddedHeader("X-Burpx-HMACSignature", "HMACAuth: " + signature);
        return RequestToBeSentAction.continueWith(modifiedRequest);
    }

    //@Override
    //public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived response) {
    //    // No modification needed for now, just proceed with the response
    //    return ResponseReceivedAction.continueWith(response);
    //}

    private String generateHMACSignature(String data, String key) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC signature", e);
        }
    }
}
