/* 
 * Copyright 2013 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.qa.perfrepo.dao;

import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;

import org.jboss.qa.perfrepo.model.TestExecutionParameter;

/**
 * DAO for {@link TestExecutionParameter}
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named
public class TestExecutionParameterDAO extends DAO<TestExecutionParameter, Long> {

   public List<String> getAllSelectionExecutionParams(Long testId) {
      TypedQuery<String> q = createNamedQuery(TestExecutionParameter.FIND_BY_TEST_ID, String.class);
      q.setParameter("testId", testId);
      return q.getResultList();
   }

}