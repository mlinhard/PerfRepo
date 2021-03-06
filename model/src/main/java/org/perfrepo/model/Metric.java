/**
 * PerfRepo
 * <p>
 * Copyright (C) 2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.perfrepo.model;

import org.perfrepo.model.auth.EntityType;
import org.perfrepo.model.auth.SecuredEntity;
import org.perfrepo.model.builder.MetricBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a test metric.
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@javax.persistence.Entity
@Table(name = "metric")
@NamedQueries({
    @NamedQuery(name = Metric.GET_TEST, query = "SELECT test from TestMetric tm inner join tm.test test inner join tm.metric m where m= :entity"),
    @NamedQuery(name = Metric.FIND_BY_NAME_GROUPID, query = "SELECT m from TestMetric tm inner join tm.metric m inner join tm.test test where test.groupId= :groupId and m.name= :name"),
    @NamedQuery(name = Metric.FIND_BY_GROUPID, query = "SELECT DISTINCT m from Metric m, TestMetric tm, Test t WHERE t.groupId= :groupId AND tm.test.id = t.id AND tm.metric.id = m.id ORDER BY m.name"),
    @NamedQuery(name = Metric.FIND_BY_TESTID, query = "SELECT m from Metric m, TestMetric tm WHERE tm.test.id = :testId AND tm.metric.id = m.id")})
@XmlRootElement(name = "metric")
@SecuredEntity(type = EntityType.TEST)
public class Metric implements Entity<Metric>, Comparable<Metric> {

   private static final long serialVersionUID = -5234628391341278215L;

   public static final String GET_TEST = "Metric.getTest";
   public static final String FIND_BY_NAME_GROUPID = "Metric.findByNameGroupId";
   public static final String FIND_BY_GROUPID = "Metric.findByGroupId";
   public static final String FIND_BY_TESTID = "Metric.findByTestId";

   @Id
   @SequenceGenerator(name = "METRIC_ID_GENERATOR", sequenceName = "METRIC_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "METRIC_ID_GENERATOR")
   private Long id;

   @Column(name = "comparator")
   @Enumerated(EnumType.STRING)
   private MetricComparator comparator;

   @Column(name = "name")
   @NotNull
   @Size(max = 2047)
   private String name;

   @OneToMany(mappedBy = "metric")
   private Collection<Value> values;

   @OneToMany(mappedBy = "metric")
   private Collection<TestMetric> testMetrics;

   @OneToMany(mappedBy = "metric")
   private Collection<Alert> alerts;

   @Column(name = "description")
   @NotNull
   @Size(max = 10239)
   private String description;

   public Metric() {
      super();
   }

   @Override
   public Metric clone() {
      try {
         return (Metric) super.clone();
      } catch (CloneNotSupportedException e) {
         throw new RuntimeException(e);
      }
   }

   public Metric(String name, MetricComparator comparator, String description) {
      super();
      this.name = name;
      this.comparator = comparator;
      this.description = description;
   }

   @XmlTransient
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   @XmlID
   @XmlAttribute(name = "id")
   public String getStringId() {
      return id == null ? null : String.valueOf(id);
   }

   public Collection<Alert> getAlerts() {
      return alerts;
   }

   public void setAlerts(Collection<Alert> alerts) {
      this.alerts = alerts;
   }

   public void setStringId(String id) {
      this.id = Long.valueOf(id);
   }

   public void setComparator(MetricComparator comparator) {
      this.comparator = comparator;
   }

   @XmlAttribute(name = "comparator")
   public MetricComparator getComparator() {
      return this.comparator;
   }

   public void setName(String name) {
      this.name = name;
   }

   @XmlAttribute(name = "name")
   public String getName() {
      return this.name;
   }

   public void setValues(Collection<Value> values) {
      this.values = values;
   }

   @XmlTransient
   public Collection<Value> getValues() {
      return this.values;
   }

   @XmlElement(name = "description")
   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   @XmlTransient
   public Collection<TestMetric> getTestMetrics() {
      return testMetrics;
   }

   public void setTestMetrics(Collection<TestMetric> testMetrics) {
      this.testMetrics = testMetrics;
   }

   @Override
   public int compareTo(Metric o) {
      return this.getName().compareTo(o.getName());
   }

   /**
    * Hack to evade listing intermediate {@link TestMetric} objects in XML.
    */
   private class TestCollection extends AbstractCollection<Test> {

      private class TestIterator implements Iterator<Test> {

         private Iterator<TestMetric> iterator;

         @Override
         public boolean hasNext() {
            return iterator.hasNext();
         }

         @Override
         public Test next() {
            return iterator.next().getTest();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      }

      @Override
      public Iterator<Test> iterator() {
         TestIterator i = new TestIterator();
         i.iterator = testMetrics.iterator();
         return i;
      }

      @Override
      public int size() {
         return testMetrics.size();
      }

      @Override
      public boolean add(Test e) {
         TestMetric tm = new TestMetric();
         tm.setTest(e);
         return testMetrics.add(tm);
      }
   }

   @XmlElementWrapper(name = "tests")
   @XmlElement(name = "test")
   public Collection<Test> getTests() {
      return testMetrics == null ? null : new TestCollection();
   }

   public void setTests(Collection<Test> tests) {
      testMetrics = new ArrayList<TestMetric>();
      getTests().addAll(tests);
   }

   public static MetricBuilder builder() {
      return new MetricBuilder(null, new Metric());
   }
}