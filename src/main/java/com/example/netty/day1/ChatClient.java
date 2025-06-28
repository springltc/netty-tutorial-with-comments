package com.example.netty.day1;

/**
 * @author liutc
 * @date 2025/6/28 17:39
 */

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.util.Scanner;

public class ChatClient {
    private final String host;
    private final int port;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChatClientHandler());
                        }
                    });

//            ChannelFuture f = b.connect(host, port).sync(); // 连接到服务器
            ChannelFuture f = b.connect(host, port).syncUninterruptibly();
            if (!f.isSuccess()) {
                System.out.println("Connection failed: " + f.cause());
                return;
            }
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine(); // 读取用户输入
                if ("exit".equalsIgnoreCase(message)) {
                    break;
                }

                System.out.println("Sending message: " + message); // 打印发送的消息
//                f.channel().writeAndFlush(message); // 发送消息
                f.channel().writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8)).addListener(future -> {
                    if (future.isSuccess()) {
                        System.out.println("Message sent: " + message);
                    } else {
                        System.out.println("Failed to send message: " + future.cause());
                    }
                });

            }
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new ChatClient("localhost", 8080).start(); // 连接到本地服务器，8080端口
    }
}

class ChatClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 输出服务端返回的消息
        System.out.println("Server responded: " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

