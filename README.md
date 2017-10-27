# jlc
Java Lock Coordinator

Locking is essential to avoid multiple concurrent updates to overwrite data from each other. Optimistic Locking is a technique that will check the object version before writing into the database. If there is a version conflict, the transaction is aborted and an error message is returned.

>> javax.persistence.OptimisticLockException

It becomes application's responsibility to handle such conditions and retry the update.

While the Pessimistic Locking could solve the problem, it could also create a lock contention in highly concurrent applications.

Java Lock Corodinator illustrates a method where you can queue the database updates and execute them atomically.

