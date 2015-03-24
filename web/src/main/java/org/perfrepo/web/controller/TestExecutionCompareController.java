/**
 *
 * PerfRepo
 *
 * Copyright (C) 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.perfrepo.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.perfrepo.model.TestExecution;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.session.TEComparatorSession;
import org.richfaces.component.SortOrder;

/**
 * @author pdrozd
 */
@Named
@RequestScoped
public class TestExecutionCompareController extends BaseController {

	private static final long serialVersionUID = 1L;

	@Inject
	private TestService testService;

	@Inject
	private TEComparatorSession teComparator;

	private Map<String, SortOrder> sortsOrders;
	private List<String> sortPriorities;

	private boolean multipleSorting = false;

	private static final String SORT_PROPERTY_PARAMETER = "sortProperty";

	@PostConstruct
	public void init() {
		sortsOrders = new HashMap<String, SortOrder>();
		sortPriorities = new ArrayList<String>();
	}

	public void sort() {
		String property = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(SORT_PROPERTY_PARAMETER);
		if (property != null) {
			SortOrder currentPropertySortOrder = sortsOrders.get(property);
			if (multipleSorting) {
				if (!sortPriorities.contains(property)) {
					sortPriorities.add(property);
				}
			} else {
				sortsOrders.clear();
			}
			if (currentPropertySortOrder == null || currentPropertySortOrder.equals(SortOrder.descending)) {
				sortsOrders.put(property, SortOrder.ascending);
			} else {
				sortsOrders.put(property, SortOrder.descending);
			}
		}
	}

	public List<TestExecution> getTestExecutions() {
		if (teComparator.getExecIds() != null && teComparator.getExecIds().size() > 0) {
			return testService.getFullTestExecutions(teComparator.getExecIds());
		}
		return new ArrayList<TestExecution>();
	}

	public List<String> getSortPriorities() {
		return sortPriorities;
	}

	public Map<String, SortOrder> getSortsOrders() {
		return sortsOrders;
	}
}