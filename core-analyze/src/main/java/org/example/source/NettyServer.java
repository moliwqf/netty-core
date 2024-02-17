package org.example.source;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.net.InetSocketAddress;

public class NettyServer {

    private static final int PORT = 9000;

    public static final EventExecutorGroup group = new DefaultEventExecutorGroup(2);


    public static void main(String[] args) throws InterruptedException {
        new NettyServer().start();
    }

    public void start() throws InterruptedException {
        // 1. 初始化boss和work组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();
        // 2. 创建启动器
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 3. 设置组
        bootstrap.group(bossGroup, workGroup);
        // 4. 设置bossGroup的日志处理器
        bootstrap.handler(new LoggingHandler());
        // 5. 相关配置
        bootstrap.option(ChannelOption.SO_BACKLOG, 100);
        // 6. 设置通道类型
        bootstrap.channel(NioServerSocketChannel.class);
        // 7. 设置连接建立时的处理器 - workGroup下的线程
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                // 7.1 添加处理器
                ch.pipeline().addLast(new EchoServerHandler());
            }
        });
        // 8. 监听端口
        bootstrap.localAddress(new InetSocketAddress(PORT));
        // 9. 绑定server
        ChannelFuture future = bootstrap.bind().sync();
        // 10. 对通道关闭进行监听
        future.channel().closeFuture().sync();
        // 11. 关闭
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}