/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.framework.engine.expression;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.engine.Term;
import org.kuali.rice.krms.api.engine.TermResolutionEngine;
import org.kuali.rice.krms.api.engine.TermResolutionException;

/**
 * TODO...
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public final class TermExpression implements Expression<Object> {

	private final Term term;
	private final TermResolutionEngine termResolutionEngine;

	public TermExpression(Term term, TermResolutionEngine termResolutionEngine) {
		this.term = term;
		this.termResolutionEngine = termResolutionEngine;
	}

	@Override
	public Object invoke(ExecutionEnvironment environment) {
		try {
			return termResolutionEngine.resolveTerm(term);
		} catch (TermResolutionException e) {
			// TODO - term resolution exception needs to become runtime
			throw new RuntimeException(e);
		}
	}

}
