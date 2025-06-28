package com.example.netty.day7;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileTransferClient {
    public static void main(String[] args) throws IOException {
        EventLoopGroup group = new NioEventLoopGroup();  // 创建 EventLoopGroup 线程池

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                    byte[] response = new byte[msg.readableBytes()];
                                    msg.readBytes(response);  // 读取服务器返回的数据
                                    System.out.println("Server response: " + new String(response));  // 打印服务器响应
                                }
                            });
                        }
                    });

            ChannelFuture future = bootstrap.connect("localhost", 8080).sync();  // 连接服务器

            // 读取文件并发送
            File file = new File("testfile.txt");  // 指定文件
            FileInputStream fis = new FileInputStream(file);
            byte[] fileContent = new byte[(int) file.length()];
            fis.read(fileContent);  // 读取文件内容

            ByteBuf buffer = Unpooled.copiedBuffer(fileContent);  // 将文件内容转换为 ByteBuf
            future.channel().writeAndFlush(buffer);  // 发送文件数据

            fis.close();

            future.channel().closeFuture().sync();  // 等待文件传输完成
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            group.shutdownGracefully();  // 关闭线程池
        }
    }
}
