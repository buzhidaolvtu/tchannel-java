/*
 * Copyright (c) 2015 Uber Technologies, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.uber.tchannel.service;

import com.google.common.util.concurrent.SettableFuture;
import com.uber.tchannel.api.SubChannel;
import com.uber.tchannel.api.TChannel;
import com.uber.tchannel.api.TFuture;
import com.uber.tchannel.api.handlers.TFutureCallback;
import com.uber.tchannel.messages.JsonRequest;
import com.uber.tchannel.messages.JsonResponse;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Future;

/**
 * 既是服务器，又是客户端的】
 */
public class ChatPeer {

    private TChannel tchannel;
    private SubChannel subChannel;

    public void init(ChatRequestHandler requestHandler) throws InterruptedException {
        tchannel = new TChannel.Builder("DuxPeer-server")
            .setServerHost(InetAddress.getLoopbackAddress())
            .setServerPort(port)
            .build();
        subChannel = tchannel.makeSubChannel("dux-service");
        subChannel.register("DuxPeer", requestHandler);
        requestHandler.setSelf(this);
        tchannel.listen();
    }

    public Future<String> send(InetAddress inetAddress, int port, String content) throws UnknownHostException {
        JsonRequest<ChatRequest> request = new JsonRequest.Builder<ChatRequest>("dux-service", "DuxPeer")
            .setBody(new ChatRequest(content))
            .setHeader("some", "header")
            .setTimeout(10000000)
            .build();
        TFuture<JsonResponse<ChatResponse>> f = subChannel.send(
            request,
            inetAddress,
            port
        );

        final SettableFuture<String> future = SettableFuture.create();
        f.addCallback(new TFutureCallback<JsonResponse<ChatResponse>>() {
            @Override
            public void onResponse(JsonResponse<ChatResponse> pongResponse) {

                ChatResponse response = pongResponse.getBody(ChatResponse.class);
                future.set(response.getContent());
            }
        });

        return future;
    }

    private final int port;

    public ChatPeer(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public TChannel getTchannel() {
        return tchannel;
    }

    public SubChannel getSubChannel() {
        return subChannel;
    }
}
