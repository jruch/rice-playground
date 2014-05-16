/*
 * Copyright 2006-2007 The Kuali Foundation
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
package org.kuali.rice.kns.lookup.keyvalues;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KeyValuesService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is a Generic ValuesFinder that builds the list of KeyValuePairs it returns
 * in getKeyValues() based on a BO along with a keyAttributeName and labelAttributeName
 * that are specified.
 */
@Transactional
public class PersistableBusinessObjectValuesFinder extends KeyValuesBase {

    private static final Log LOG = LogFactory.getLog(PersistableBusinessObjectValuesFinder.class);

    private Class businessObjectClass;
    private String keyAttributeName;
    private String labelAttributeName;
    private boolean includeKeyInDescription = false;
    private boolean includeBlankRow = false;

    /**
     * Build the list of KeyLabelPairs using the key (keyAttributeName) and
     * label (labelAttributeName) of the list of all business objects found
     * for the BO class specified.
     *
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyLabelPair> getKeyValues() {
    	List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>();

    	try {
    	    KeyValuesService boService = KNSServiceLocator.getKeyValuesService();
            Collection objects = boService.findAll(businessObjectClass);
            if(includeBlankRow) {
            	labels.add(new KeyLabelPair("", ""));
            }
            for (Object object : objects) {
            	Object key = PropertyUtils.getProperty(object, keyAttributeName);
            	String label = (String)PropertyUtils.getProperty(object, labelAttributeName);
            	if (includeKeyInDescription) {
            	    label = key + " - " + label;
            	}
            	labels.add(new KeyLabelPair(key, label));
    	    }
    	} catch (IllegalAccessException e) {
            LOG.debug(e.getMessage(), e);
            LOG.error(e.getMessage());
            throw new RuntimeException("IllegalAccessException occurred while trying to build keyValues List. businessObjectClass: " + businessObjectClass + "; keyAttributeName: " + keyAttributeName + "; labelAttributeName: " + labelAttributeName + "; includeKeyInDescription: " + includeKeyInDescription, e);
    	} catch (InvocationTargetException e) {
            LOG.debug(e.getMessage(), e);
            LOG.error(e.getMessage());
            throw new RuntimeException("InvocationTargetException occurred while trying to build keyValues List. businessObjectClass: " + businessObjectClass + "; keyAttributeName: " + keyAttributeName + "; labelAttributeName: " + labelAttributeName + "; includeKeyInDescription: " + includeKeyInDescription, e);
    	} catch (NoSuchMethodException e) {
            LOG.debug(e.getMessage(), e);
            LOG.error(e.getMessage());
            throw new RuntimeException("NoSuchMethodException occurred while trying to build keyValues List. businessObjectClass: " + businessObjectClass + "; keyAttributeName: " + keyAttributeName + "; labelAttributeName: " + labelAttributeName + "; includeKeyInDescription: " + includeKeyInDescription, e);
    	}

        return labels;
    }

    /**
     * @return the businessObjectClass
     */
    public Class getBusinessObjectClass() {
        return this.businessObjectClass;
    }

    /**
     * @param businessObjectClass the businessObjectClass to set
     */
    public void setBusinessObjectClass(Class businessObjectClass) {
        this.businessObjectClass = businessObjectClass;
    }

    /**
     * @return the includeKeyInDescription
     */
    public boolean isIncludeKeyInDescription() {
        return this.includeKeyInDescription;
    }

    /**
     * @param includeKeyInDescription the includeKeyInDescription to set
     */
    public void setIncludeKeyInDescription(boolean includeKeyInDescription) {
        this.includeKeyInDescription = includeKeyInDescription;
    }

    /**
     * @return the keyAttributeName
     */
    public String getKeyAttributeName() {
        return this.keyAttributeName;
    }

    /**
     * @param keyAttributeName the keyAttributeName to set
     */
    public void setKeyAttributeName(String keyAttributeName) {
        this.keyAttributeName = keyAttributeName;
    }

    /**
     * @return the labelAttributeName
     */
    public String getLabelAttributeName() {
        return this.labelAttributeName;
    }

    /**
     * @param labelAttributeName the labelAttributeName to set
     */
    public void setLabelAttributeName(String labelAttributeName) {
        this.labelAttributeName = labelAttributeName;
    }

	/**
	 * @return the includeBlankRow
	 */
	public boolean isIncludeBlankRow() {
		return this.includeBlankRow;
	}

	/**
	 * @param includeBlankRow the includeBlankRow to set
	 */
	public void setIncludeBlankRow(boolean includeBlankRow) {
		this.includeBlankRow = includeBlankRow;
	}

}