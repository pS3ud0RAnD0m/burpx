package com.ps3ud0rand0m.burpx;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class Main implements BurpExtension
{
    @Override
    public void initialize(MontoyaApi api)
    {
        api.extension().setName("Burpx");

        api.proxy().registerRequestHandler(new MyProxyHttpRequestHandler());
        api.proxy().registerResponseHandler(new MyProxyHttpResponseHandler());
    }
}