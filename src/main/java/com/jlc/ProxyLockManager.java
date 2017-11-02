package com.jlc;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author lokesh
 */

public class ProxyLockManager implements LockManager {

    private static final Logger logger = LogManager.getLogger(ProxyLockManager.class);

    private String host = "localhost";
    private int port = 8040;
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Queue<LockEvent> queue = new ConcurrentLinkedQueue<>();

    private volatile boolean shutdown = false;
    private volatile boolean shutdownComplete = false;


    public ProxyLockManager(String host, int port) {
        this.host = host;
        this.port = port;
        new Thread(this).start();
    }

    @Override
    public void lock(LockEvent lockEvent) {
        queue.add(lockEvent);
        logger.info("added lock event to queue");
    }

    @Override
    public void shutdown() {
        this.shutdown = true;

        workerGroup.shutdownGracefully();

        while(!shutdownComplete) {
            try {
                logger.info("Waiting for shutdown..");
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void release(LockEvent lockEvent) {
        queue.remove(lockEvent);
    }

    @Override
    public void run() {

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProxyClientHandler());
                }
            });

            logger.info("Trying to connect..");

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            logger.info("connected..");

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    class ProxyClientHandler extends ChannelInboundHandlerAdapter {

        private final Logger logger = LogManager.getLogger(ProxyClientHandler.class);

        @Override
        public void channelActive(final ChannelHandlerContext ctx) {

            logger.info("Running proxy lock manager..");

            while (!shutdown || !queue.isEmpty()) {

                LockEvent lockEvent = queue.peek();
                if(lockEvent != null) {

                    logger.info("sending..." + lockEvent);
                    final ByteBuf time = ctx.alloc().buffer(4); // (2)
                    time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));

                    final ChannelFuture f = ctx.writeAndFlush(time); // (3)
                    f.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) {
                            assert f == future;
                            ctx.close();
                        }
                    });

                    release(lockEvent);
                }
                else
                {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            shutdownComplete = true;
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
