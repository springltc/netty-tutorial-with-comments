package com.example.netty.day2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChannelHandlerExample extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 打印接收到的消息，并向服务端发送回应
        System.out.println("Received from server: " + msg);
        ctx.writeAndFlush("Response from client"); // 向服务端发送响应
    }
}
