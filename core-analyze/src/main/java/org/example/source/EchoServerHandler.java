package org.example.source;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

@ChannelHandler.Sharable
public class EchoServerHandler extends
        ChannelInboundHandlerAdapter {

    // group充当业务线程池中，可以将任务提交到该线程池中
    public static final EventExecutorGroup group = new DefaultEventExecutorGroup(16);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx,
                            Object msg) {
        /*ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5 * 1000);
                    System.out.println(Thread.currentThread().getName());
                    ctx.writeAndFlush("你好");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/
        // 将任务添加到线程池
        group.submit(() -> {
            System.out.println(msg);
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("线程池线程名：" + Thread.currentThread().getName());
        });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}