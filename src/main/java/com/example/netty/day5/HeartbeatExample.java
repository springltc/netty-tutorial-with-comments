package com.example.netty.day5;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class HeartbeatExample extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 添加空闲状态检测（心跳机制）
        pipeline.addLast(new IdleStateHandler(0, 4, 0));  // 每 4 秒检测一次是否为空闲，若空闲则发送心跳
        pipeline.addLast(new StringDecoder());  // 解码器，接收字节流并转为字符串
        pipeline.addLast(new StringEncoder());  // 编码器，将字符串转为字节流
        pipeline.addLast(new SimpleChannelInboundHandler<String>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                System.out.println("Received: " + msg);  // 打印接收到的消息
                ctx.writeAndFlush("Hello from server with Heartbeat");  // 向客户端发送响应
            }
        });
    }
}
