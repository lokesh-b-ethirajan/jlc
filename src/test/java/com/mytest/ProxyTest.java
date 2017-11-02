package com.mytest;

import com.jlc.proxy.ProxyLockClient;
import com.jlc.proxy.ProxyLockServer;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class ProxyTest {

    @Test
    public void testTimeIO() throws Exception {

        /*ProxyLockServer ts = new ProxyLockServer();
        new Thread(ts).start();

        while(!ts.isReady()) {
            TimeUnit.SECONDS.sleep(1);
        }

        ProxyLockClient.send();*/

    }
}
