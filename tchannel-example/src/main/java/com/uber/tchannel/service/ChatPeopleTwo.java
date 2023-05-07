package com.uber.tchannel.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ChatPeopleTwo {

    public static void main(String[] args) throws UnknownHostException, ExecutionException, InterruptedException {
        ChatPeer chatPeople = new ChatPeer(18889);
        chatPeople.init(new ChatRequestHandler());


        Scanner myObj = new Scanner(System.in);
        while (true) {
            // String input
            String name = myObj.nextLine();
            Future<String> result1 = chatPeople.send(InetAddress.getByName("localhost"), 18888, "我是18889:" + name);

            System.out.println(result1.get());
        }
    }
}