package com.jlc.examples;

import com.jlc.partition.DistributedLockPartitioner;
import com.jlc.mgr.LockManager;
import com.jlc.partition.LockPartitioner;
import com.jlc.config.JSONPartitionConfig;
import com.jlc.examples.myevent.SampleLockEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.UUID;

/**
 * @author lokesh
 */

public class App {

    public static void main(String args[]) throws FileNotFoundException {

        JSONPartitionConfig jsonPartitionConfig = new JSONPartitionConfig(new File(args[0]));
        LockPartitioner lockPartitioner = new DistributedLockPartitioner(jsonPartitionConfig.getPartitionConfigs());

        for(int i=0; i<10; i++) {
            SampleLockEvent lockEvent = new SampleLockEvent();
            lockEvent.setId(UUID.randomUUID().toString());
            LockManager lockManager = lockPartitioner.getPartition(lockEvent);
            lockManager.lock(lockEvent);
        }

        System.out.println("press <enter> to quit..");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();

        lockPartitioner.shutdown();

    }

}
