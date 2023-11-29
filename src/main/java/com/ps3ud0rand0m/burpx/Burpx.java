package com.ps3ud0rand0m.burpx;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class Burpx implements BurpExtension
{
    @Override
    public void initialize(MontoyaApi api)
    {
        api.extension().setName("Burpx");

        // Http handlers
        //api.http().registerHttpHandler(new MyHttpHandler(api));

        // Proxy handlers
        api.proxy().registerRequestHandler(new MyProxyRequestHandler());
        //api.proxy().registerResponseHandler(new MyProxyResponseHandler());
    }
}