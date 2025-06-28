package com.example.netty.day6;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ThreadModelExample {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);  // 用于接收连接的线程池
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 用于处理连接的线程池

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)  // 设置 bossGroup 和 workerGroup
                .channel(NioServerSocketChannel.class)  // 使用 NioServerSocketChannel 通道
                .childHandler(new ChannelInitializer<Channel>() {  // 配置子处理器
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                System.out.println("Received: " + msg);  // 打印接收到的消息
                            }
                        });
                    }
                });

            // 启动服务器
            ChannelFuture future = serverBootstrap.bind(8080).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();  // 关闭 bossGroup 线程池
            workerGroup.shutdownGracefully();  // 关闭 workerGroup 线程池
        }
    }
}
