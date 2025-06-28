package com.example.netty.day3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class PipelineHandlerExample extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();  // 获取 ChannelPipeline
        pipeline.addLast(new StringDecoder());  // 解码器，接收字节流并转为字符串
        pipeline.addLast(new StringEncoder());  // 编码器，将字符串转为字节流
        pipeline.addLast(new SimpleChannelInboundHandler<String>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                System.out.println("Received: " + msg);  // 打印接收到的消息
                ctx.writeAndFlush("Hello from server with Pipeline");  // 向客户端发送响应
            }
        });
    }
}
