# jlc
Java Lock Coordinator

Locking is essential to avoid multiple concurrent updates to overwrite data from each other. Optimistic Locking is a technique that will check the object version before writing into the database. If there is a version conflict, the transaction is aborted and an error message is returned. Example below:

lokesh@Lokesh-PC:~/IdeaProjects/jlc$ more build/test.log | grep MyFailingTest | grep ERROR
2017-10-27 15:12:39.954 [TestNG] ERROR com.mytest.MyFailingTest - org.springframework.orm.jpa.JpaOptimisticLockingFailureException: Batch update returned unexpected row count from update [0]; actual row count: 0; expected: 1; nested exception is javax.persistence.OptimisticLockException: Batch update returned unexpected row count from update [0]; actual row count: 0; expected: 1
2017-10-27 15:12:39.959 [TestNG] ERROR com.mytest.MyFailingTest - org.springframework.orm.jpa.JpaOptimisticLockingFailureException: Batch update returned unexpected row count from update [0]; actual row count: 0; expected: 1; nested exception is javax.persistence.OptimisticLockException: Batch update returned unexpected row count from update [0]; actual row count: 0; expected: 1
2017-10-27 15:12:39.959 [TestNG] ERROR com.mytest.MyFailingTest - org.springframework.orm.jpa.JpaOptimisticLockingFailureException: Batch update returned unexpected row count from update [0]; actual row count: 0; expected: 1; nested exception is javax.persistence.OptimisticLockException: Batch update returned unexpected row count from update [0]; actual row count: 0; expected: 1
2017-10-27 15:12:39.962 [TestNG] ERROR com.mytest.MyFailingTest - javax.persistence.OptimisticLockException: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect) : [com.mymodel.DeviceState#Apple-ipad]

It becomes an application concern to handle the error and retry the update. Below test result shows the

lokesh@Lokesh-PC:~/IdeaProjects/jlc$ more build/test.log | grep MyFailingTest | grep INFO
2017-10-27 15:12:39.801 [Test worker] INFO  com.mytest.MyFailingTest - 35876d19-505c-4525-9ce7-56cf7e024b68
2017-10-27 15:12:39.925 [TestNG] INFO  com.mytest.MyFailingTest - 35876d19-505c-4525-9ce7-56cf7e024b68 --> f311b16e-102d-4690-a97c-5d21071bb6c4
2017-10-27 15:12:39.925 [TestNG] INFO  com.mytest.MyFailingTest - 35876d19-505c-4525-9ce7-56cf7e024b68 --> c70c2595-cb64-4d45-95e4-0522f2a84ccd
2017-10-27 15:12:39.925 [TestNG] INFO  com.mytest.MyFailingTest - 35876d19-505c-4525-9ce7-56cf7e024b68 --> 311e54a1-6c6c-42d8-bd76-814698b23920
2017-10-27 15:12:39.927 [TestNG] INFO  com.mytest.MyFailingTest - 35876d19-505c-4525-9ce7-56cf7e024b68 --> fc8c17cb-092c-4256-a1ab-b11c715cc5a8
2017-10-27 15:12:39.937 [TestNG] INFO  com.mytest.MyFailingTest - c70c2595-cb64-4d45-95e4-0522f2a84ccd --> f3c7d1b1-d599-4af5-b686-4366bb9f1958
2017-10-27 15:12:39.951 [TestNG] INFO  com.mytest.MyFailingTest - f3c7d1b1-d599-4af5-b686-4366bb9f1958 --> 348603d3-d953-4dc2-9c82-d55266d2168d
2017-10-27 15:12:39.957 [TestNG] INFO  com.mytest.MyFailingTest - f3c7d1b1-d599-4af5-b686-4366bb9f1958 --> 522b84a4-ce01-446d-aa64-a1fbebe56182
2017-10-27 15:12:39.968 [TestNG] INFO  com.mytest.MyFailingTest - 348603d3-d953-4dc2-9c82-d55266d2168d --> 33832ae1-949e-4439-9a68-c965b2e59326
2017-10-27 15:12:40.082 [Test worker] INFO  com.mytest.MyFailingTest - Apple-ipad : 33832ae1-949e-4439-9a68-c965b2e59326
2017-10-27 15:12:40.091 [Test worker] INFO  com.mytest.MyFailingTest - 0

Java Lock Corodinator illustrates a method where you can queue the database updates and execute them atomically.

lokesh@Lokesh-PC:~/IdeaProjects/jlc$ more build/test.log | grep MyWorkingTest | grep INFO
2017-10-27 15:12:40.245 [Test worker] INFO  com.mytest.MyWorkingTest - 658b75a9-d3cb-48ed-83cd-042971cd86f5
2017-10-27 15:12:40.295 [Thread-9] INFO  com.mytest.MyWorkingTest - 658b75a9-d3cb-48ed-83cd-042971cd86f5 --> 9b745c86-1f74-417b-bd73-76740f97dd93
2017-10-27 15:12:40.304 [Thread-9] INFO  com.mytest.MyWorkingTest - 9b745c86-1f74-417b-bd73-76740f97dd93 --> 94d95702-c64b-4e78-8576-46c0763806a0
2017-10-27 15:12:40.311 [Thread-9] INFO  com.mytest.MyWorkingTest - 94d95702-c64b-4e78-8576-46c0763806a0 --> c34f1226-1d19-4587-96cb-587f5667216e
2017-10-27 15:12:40.317 [Thread-9] INFO  com.mytest.MyWorkingTest - c34f1226-1d19-4587-96cb-587f5667216e --> 370dd7cd-8d8d-421f-8de4-ce605838be88
2017-10-27 15:12:40.326 [Thread-9] INFO  com.mytest.MyWorkingTest - 370dd7cd-8d8d-421f-8de4-ce605838be88 --> 2bcfe216-7399-4442-aaf4-ffa7cf3731d5
2017-10-27 15:12:40.337 [Thread-9] INFO  com.mytest.MyWorkingTest - 2bcfe216-7399-4442-aaf4-ffa7cf3731d5 --> 46e8417a-f5d3-4f98-a7e9-f72859cf7237
2017-10-27 15:12:40.351 [Thread-9] INFO  com.mytest.MyWorkingTest - 46e8417a-f5d3-4f98-a7e9-f72859cf7237 --> e799b6df-3d86-44d4-a12a-de98647429ef
2017-10-27 15:12:40.357 [Thread-9] INFO  com.mytest.MyWorkingTest - e799b6df-3d86-44d4-a12a-de98647429ef --> d7281348-fddd-4f7c-9b77-e267167ac6b1
2017-10-27 15:12:40.368 [Test worker] INFO  com.mytest.MyWorkingTest - Apple-ipad : d7281348-fddd-4f7c-9b77-e267167ac6b1

