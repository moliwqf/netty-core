package org.example.source;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.*;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyServer {

    // websocket 协议名
    private static final String WEBSOCKET_PROTOCOL = "WebSocket";

    // 服务监听端口
    private static final int PORT = 9000;

    // 服务请求的路径
    private static final String PATH = "/chat";

    public static void main(String[] args) throws InterruptedException {
        new NettyServer().start();
    }

    public void start() throws InterruptedException {
        // 1. 初始化boss和work组
        // 父线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 子县城退组
        EventLoopGroup workGroup = new NioEventLoopGroup();
        // 2. 创建启动器
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 3. 设置组
        bootstrap.group(bossGroup, workGroup);
        // 设置日志处理器
        bootstrap.handler(new LoggingHandler());
        bootstrap.option(ChannelOption.SO_BACKLOG, 100);
        // 4. 设置通道类型
        bootstrap.channel(NioServerSocketChannel.class);
        // 5. 设置连接建立时的处理器
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                // 5.1 添加http编解码器
                ch.pipeline().addLast(new HttpServerCodec());
                ch.pipeline().addLast(new ObjectEncoder());
            }
        });
        // 6. 监听端口
        bootstrap.localAddress(new InetSocketAddress(PORT));
        // 7. 绑定server
        ChannelFuture future = bootstrap.bind().sync();
        // 8. 对通道关闭进行监听
        future.channel().closeFuture().sync();
    }

}