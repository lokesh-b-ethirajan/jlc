package com.jlc.sample;

import com.jlc.DistributedLockPartitioner;
import com.jlc.LockManager;
import com.jlc.LockPartitioner;
import com.jlc.config.JSONPartitionConfig;

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
            lockEvent.setUuid(UUID.randomUUID().toString());
            LockManager lockManager = lockPartitioner.getPartition(lockEvent);
            lockManager.lock(lockEvent);
        }

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        lockPartitioner.shutdown();

    }

}
