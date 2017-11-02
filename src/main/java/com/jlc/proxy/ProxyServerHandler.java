package com.jlc.proxy;

import com.jlc.LockEvent;
import com.jlc.SimpleLockManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author lokesh
 */

public class ProxyServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(ProxyServerHandler.class);

    private SimpleLockManager simpleLockManager = null;

    public ProxyServerHandler(SimpleLockManager simpleLockManager) {
        this.simpleLockManager = simpleLockManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        LockEvent lockEvent = (LockEvent) msg;
        logger.info("got it -> " + lockEvent);
        simpleLockManager.lock(lockEvent);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
