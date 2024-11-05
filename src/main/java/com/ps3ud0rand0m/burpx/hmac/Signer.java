package com.ps3ud0rand0m.burpx.hmac;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static burp.api.montoya.http.handler.RequestToBeSentAction.continueWith;
import static burp.api.montoya.http.handler.ResponseReceivedAction.continueWith;
import static burp.api.montoya.http.message.params.HttpParameter.urlParameter;

public class Signer implements HttpHandler {

    private final Logging logging;
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String HMAC_KEY = "sup3r$3cret";

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
            //logging.logToOutput(requestToBeSent.bodyToString());

            // Add a URL param
            //modifiedRequest = requestToBeSent.withAddedParameters(urlParameter("Burpx-Url-Param", "UrlParam"));

            // Add key header
            modifiedRequest = modifiedRequest.withAddedHeader("X-Burpx-Key-Was", HMAC_KEY);

            // Add timestamp header
            String timeStamp = makeTimeStamp();
            modifiedRequest = modifiedRequest.withAddedHeader("X-Acme-Authorization-Timestamp", timeStamp);

            // Add HMAC header
            String requestData = modifiedRequest.toString();
            logging.logToOutput(requestData);
            String hmacSignature = generateHMACSignature(requestData, HMAC_KEY);
            modifiedRequest = modifiedRequest.withAddedHeader("X-Acme-Authorization", "HmacV1Auth 00000-0000-0000-0000-000000000000:" + hmacSignature);

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

    private static String makeTimeStamp() {
        return DateTimeFormatter.ISO_INSTANT
                .withZone(ZoneOffset.UTC)
                .format(Instant.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS)); // Intentionally, only to the second
    }

    private static String generateHMACSignature(String requestData, String key) {
         try {
             Mac mac = Mac.getInstance(HMAC_SHA256);
             SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
             mac.init(secretKeySpec);
             byte[] hmacBytes = mac.doFinal(requestData.getBytes(StandardCharsets.UTF_8));
             return Base64.getEncoder().encodeToString(hmacBytes);
         } catch (Exception e) {
             throw new RuntimeException("Failed to generate HMAC signature", e);
         }
    }
}