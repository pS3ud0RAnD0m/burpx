
package com.ps3ud0rand0m.burpx.hmac;

import burp.api.montoya.MontoyaApi;
;
import burp.api.montoya.proxy.http.HttpRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestHandler.Result;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class KeyTester implements ProxyRequestHandler {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final List<String> TEST_KEYS = List.of(
            "test-key-1",  // Replace with actual keys
            "test-key-2",
            "test-key-3"
    );

    private final MontoyaApi api;

    public KeyTester(MontoyaApi api) {
        this.api = api;
        api.logging().logToOutput("KeyTester initialized");
    }

    @Override
    public Result handleRequest(HttpRequest request) {
        if (api.scope().isInScope(request.url())) {
            for (String key : TEST_KEYS) {
                HttpRequest modifiedRequest = request.withAddedHeader("X-Burpx-Signed", key)
                        .withAddedHeader("X-Burpx-HMACSignature",
                                "HMACAuth: " + generateHMACSignature(request.toString(), key));
                api.proxy().sendRequest(modifiedRequest);
            }
        }
        return Result.CONTINUE;
    }

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
