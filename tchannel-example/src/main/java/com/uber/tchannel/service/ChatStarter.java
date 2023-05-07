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

            /**
             * com.uber.tchannel.channels.Connection.setIdentified(java.util.Map<java.lang.String,java.lang.String>)
             * 上一步发送【chatOne.send】request（18888 - > 18889）时，使用的是随机端口号，但是18888也会作为header传递到服务端，然后服务端18889也会把18888记录下来，
             * 然后使得下一步发送【chatTwo.send】直接利用了上面的连接，并没有建立新的连接。
             *
             * 但是，我并不想使用这个特性，因为这使得分析变得不一致了。
             */
            Future<String> result2 = chatTwo.send(InetAddress.getByName("localhost"), 18888, "我是18889:" + name);


            System.out.println(result1.get());
            System.out.println(result2.get());
        }
    }
}
