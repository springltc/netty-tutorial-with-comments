package com.example.netty.day1;

/**
 * @author liutc
 * @date 2025/6/28 17:39
 */

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ChatServer {
    private final int port;

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        // 创建两个事件循环组
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // 处理连接请求
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 处理数据读写

        try {
            ServerBootstrap b = new ServerBootstrap(); // 启动服务器
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new StringDecoder());  // 解码器
                            ch.pipeline().addLast(new StringEncoder());  // 编码器
                            ch.pipeline().addLast(new ChatServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置队列大小
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // 保持连接活跃

            ChannelFuture f = b.bind(port).sync(); // 绑定端口并等待成功
            System.out.println("Chat Server started on port " + port);
            f.channel().closeFuture().sync(); // 等待服务器关闭
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new ChatServer(8080).start(); // 启动服务器，监听8080端口
    }
}

class ChatServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 读取客户端发送的消息并广播给其他客户端
        System.out.println("Received: " + msg);
        ctx.writeAndFlush(msg); // 直接返回客户端消息，模拟聊天功能
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
