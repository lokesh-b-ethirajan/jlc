# jlc
Java Lock Coordinator

Locking is essential to avoid multiple concurrent updates to overwrite data from each other. Optimistic Locking is a technique that will check the object version before writing to the database. If there is a version conflict, the transaction is aborted and an error message is returned.

>> javax.persistence.OptimisticLockException

It becomes application's responsibility to handle such conditions and retry the update.

While the Pessimistic Locking could solve the problem, it could also create a lock contention in highly concurrent applications.

Java Lock Corodinator illustrates a method where you can queue the database updates and execute them atomically.

SimpleLockManager will allow to lock resources and update them atomically and sequentially. A LockPartitioner can be used to run multiple lock managers in parallel.

Example #1: To lock resources across mulitple threads in a single Java application

LockEvent lockEvent = new DeviceStateEvent(newDeviceState);
LockManager lockManager = new SimpleLockManager();
lockManager.lock(lockEvent);

Example #2: To lock resources across mulitple threads in a single Java application using partitions

LockEvent lockEvent = new DeviceStateEvent(newDeviceState);
LockPartitioner lockPartitioner = new DefaultLockPartitioner(2);
LockManager lockManager = lockPartitioner.getPartition(deviceStateEvent);
lockManager.lock(deviceStateEvent);

Example #3: To lock resources across mulitple processes/machines (Distributed Locking)

LockEvent lockEvent = new DeviceStateEvent(newDeviceState);
JSONPartitionConfig jsonPartitionConfig = new JSONPartitionConfig(new File(resource.getFile())); 
LockPartitioner lockPartitioner = new DistributedLockPartitioner(jsonPartitionConfig.getPartitionConfigs());
LockManager lockManager = lockPartitioner.getPartition(deviceStateEvent);
lockManager.lock(deviceStateEvent);


