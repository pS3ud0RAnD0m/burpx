package com.ps3ud0rand0m.burpx;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import com.ps3ud0rand0m.burpx.hmac.Signer;
import com.ps3ud0rand0m.burpx.http.MyHttpHandler;
import com.ps3ud0rand0m.burpx.http.MyHttpHandlerTinker;
import com.ps3ud0rand0m.burpx.proxy.MyProxyRequestHandler;

public class Burpx implements BurpExtension {
    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("Burpx");

        // Http handlers
        //api.http().registerHttpHandler(new MyHttpHandler(api));
        //api.http().registerHttpHandler(new MyHttpHandlerTinker(api));
        //api.http().registerHttpHandler(new Signer(api));

        // Proxy handlers
        api.proxy().registerRequestHandler(new MyProxyRequestHandler(api));
        //api.proxy().registerRequestHandler(new MyProxyRequestHandlerTinker());
        //api.proxy().registerResponseHandler(new MyProxyResponseHandler());
    }
}
