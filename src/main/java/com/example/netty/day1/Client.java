package com.example.netty.day1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

public class Client {
    public static void main(String[] args) {
        // 创建 EventLoopGroup，用于客户端的连接和 I/O 操作
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            // 创建 Bootstrap 实例
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)  // 使用 NIO SocketChannel
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            // 处理接收到的响应消息
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                    // 打印从服务器接收到的消息
                                    System.out.println("Client received: " + msg);
                                }
                            });
                        }
                    });

            // 连接到服务端
            ChannelFuture future = bootstrap.connect("localhost", 8080).sync();

            // 向服务端发送消息
            future.channel().writeAndFlush(Unpooled.copiedBuffer("send hello world", CharsetUtil.UTF_8));

            // 等待客户端通道关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅关闭
            group.shutdownGracefully();
        }
    }
}
