package com.example.netty.day4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ByteBufExample extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        // 打印 ByteBuf 内容
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);  // 将 ByteBuf 内容转为字节数组
        System.out.println("Received bytes: " + new String(bytes));  // 打印字节数组内容

        // 回复消息
        ctx.writeAndFlush(Unpooled.copiedBuffer("Response from ByteBufExample".getBytes()));  // 回复客户端
    }
}
