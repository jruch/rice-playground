/*
 * Copyright 2006-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.rice.kim.api;

import org.kuali.rice.core.api.CoreConstants;

public final class KimConstants {
    public static final class Namespaces {
    	public static final String KIM_NAMESPACE_PREFIX = CoreConstants.Namespaces.ROOT_NAMESPACE_PREFIX + "/kim";

    	/**
    	 * Namespace for the core module which is compatible with Kuali Rice 2.0.x.
    	 */
    	public static final String KIM_NAMESPACE_2_0 = KIM_NAMESPACE_PREFIX + "/" + CoreConstants.Versions.VERSION_2_0;

        private Namespaces() {
		    throw new UnsupportedOperationException("do not call");
	    }
    }

    public static final class RestrictedMasks {
        public static final String RESTRICTED_DATA_MASK = "Xxxxxx";
        public static final String RESTRICTED_DATA_MASK_CODE = "XX";
        public static final String RESTRICTED_DATA_MASK_ZIP = "00000";
        public static final String RESTRICTED_DATA_MASK_PHONE = "000-000-0000";
    }
}
