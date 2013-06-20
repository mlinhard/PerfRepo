package org.jboss.qa.perfrepo.client.test;

import org.jboss.qa.perfrepo.client.PerfRepoClient;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.builder.TestBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Manually test {@link PerfRepoClient} with perfrepo running on localhost
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class CreateManualTestData {

   private PerfRepoClient client;
   private String testUserRole;

   @BeforeClass
   public void createClient() {
      String host = System.getProperty("perfrepo.client.host", "localhost:8080");
      String auth = System.getProperty("perfrepo.client.auth", "cDpw");
      testUserRole = System.getProperty("perfrepo.client.role", "perfrepouser");
      client = new PerfRepoClient(host, auth);
   }

   @AfterClass
   public void destroyClient() {
      client.shutdown();
      client = null;
      testUserRole = null;
   }

   Test createJDGCSTest() {
      return Test.builder().name("JDG client/server test").groupId(testUserRole).uid("jdg_cs_test").description("JDG Client/server test")
            .metric("jdg_cs_avg_read_resp_time", "0", "Average Read response time").metric("jdg_cs_avg_write_resp_time", "1", "Average Write response time")
            .metric("jdg_cs_requests_per_sec", "2", "Number of requests per second").build();
   }

   Test createJDGRGTest() {
      TestBuilder test = Test.builder();
      test.name("JDG RadarGun test");
      test.groupId(testUserRole);
      test.uid("jdg_rg_test");
      test.description("JDG RadarGun test");
      test.metric("jdg_lib_rg_reads_per_sec", "0", "Number of reads per second (READS_PER_SEC)");
      test.metric("jdg_lib_rg_writes_per_sec", "1", "Number of writes per second (WRITES_PER_SEC)");
      test.metric("jdg_lib_rg_reads_per_sec_mrd", "2", "Maximum Relative Difference in READS_PER_SEC over all slaves");
      test.metric("jdg_lib_rg_writes_per_sec_mrd", "3", "Maximum Relative Difference in WRITES_PER_SEC over all slaves");
      test.metric("jdg_lib_rg_read_tx_overhead", "4", "Transaction overhead in read operations");
      test.metric("jdg_lib_rg_write_tx_overhead", "5", "Transaction overhead in write operations");
      return test.build();
   }

   //   private TestExecution createTestExecution(Long testId) {
   //      TestExecution testExecution = new TestExecution();
   //      Test idHolder = new Test();
   //      idHolder.setId(testId);
   //      testExecution.setTest(idHolder);
   //      testExecution.setName("execution1");
   //      testExecution.setStarted(new Date());
   //      testExecution.addParameter("param1", "value1");
   //      testExecution.addParameter("param2", "value2");
   //      testExecution.addTag("tag1");
   //      testExecution.addTag("tag2");
   //      testExecution.addValue("metric1", 12.0d);
   //      Value v2 = testExecution.addValue("metric2", null);
   //      v2.addParameter("10", "20.0");
   //      v2.addParameter("20", "40.0");
   //      v2.addParameter("30", "60.0");
   //      return testExecution;
   //   }

   @org.testng.annotations.Test
   public void testCreate() throws Exception {
      Test test = createJDGRGTest();
      Long id = client.createTest(test);
      System.out.println("Created test: " + id);
   }

}