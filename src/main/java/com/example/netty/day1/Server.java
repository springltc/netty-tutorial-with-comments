package com.example.netty.day1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Server {
    public static void main(String[] args) {
        // 创建两个 EventLoopGroup，bossGroup 用于接收连接，workerGroup 用于处理数据
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 创建 ServerBootstrap 实例
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            // 配置服务器，设置两个 EventLoopGroup 和通道类型
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new StringDecoder());  // 解码器
                            ch.pipeline().addLast(new StringEncoder());  // 编码器
                            // 在管道中添加处理器，处理接收到的消息
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                    // 打印客户端发送的消息
                                    System.out.println("Server received: " + msg);
                                    // 向客户端发送响应消息
                                    ctx.writeAndFlush("Hello from server");
                                }
                            });
                        }
                    });

            // 绑定端口并启动服务端
            ChannelFuture future = serverBootstrap.bind(8080).sync();

            // 阻塞，直到服务端关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
