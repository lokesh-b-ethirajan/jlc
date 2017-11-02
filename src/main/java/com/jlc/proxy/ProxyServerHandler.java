package com.jlc.proxy;

import com.jlc.LockEvent;
import com.jlc.SimpleLockManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

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
        ByteBuf m = (ByteBuf) msg; // (1)
        try {
            long currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L;
            logger.info("got it -> " + new Date(currentTimeMillis));
            ctx.close();
        } finally {
            m.release();
        }
        /*LockEvent lockEvent = (LockEvent) msg;
        logger.info("got it -> " + lockEvent);
        simpleLockManager.lock(lockEvent);
        ctx.close();*/
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
