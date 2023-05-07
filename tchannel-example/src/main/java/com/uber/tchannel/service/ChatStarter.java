package com.uber.tchannel.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ChatStarter {

    public static void main(String[] args) throws UnknownHostException, ExecutionException, InterruptedException {
        ChatPeer chatOne = new ChatPeer(18888);
        chatOne.init(new ChatRequestHandler());
        ChatPeer chatTwo = new ChatPeer(18889);
        chatTwo.init(new ChatRequestHandler());


        Scanner myObj = new Scanner(System.in);
        while (true) {
            // String input
            String name = myObj.nextLine();

            Future<String> result1 = chatOne.send(InetAddress.getByName("localhost"), 18889, "我是18888:" + name);
            Future<String> result2 = chatTwo.send(InetAddress.getByName("localhost"), 18888, "我是18889:" + name);

            System.out.println(result1.get());
            System.out.println(result2.get());
        }
    }
}
