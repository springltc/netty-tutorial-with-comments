package com.example.netty.day7;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class FileTransferServer {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);  // 接收客户端连接
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 处理 I/O

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                byte[] data = new byte[msg.readableBytes()];  // 将 ByteBuf 转为字节数组
                                msg.readBytes(data);  // 读取数据
                                System.out.println("Received file data");

                                // 回复客户端，文件接收成功
                                ctx.writeAndFlush(Unpooled.copiedBuffer("File received successfully!".getBytes()));  // 向客户端发送响应
                            }
                        });
                    }
                });

            ChannelFuture future = bootstrap.bind(8080).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
