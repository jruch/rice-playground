/*
 * Copyright 2005-2007 The Kuali Foundation.
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

package org.kuali.rice.kns.datadictionary.control;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.rice.kns.datadictionary.exception.CompletionException;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;

/**
 * A single HTML control definition in the DataDictionary, which contains information relating to the HTML control used to realize a
 * specific attribute. All types of controls are represented by an instance of this class; you have to call one of the is* methods
 * to figure out which of the other accessors should return useful values.
 *
 *
 */
public abstract class ControlDefinitionBase extends DataDictionaryDefinitionBase implements ControlDefinition {

    protected boolean datePicker;
    protected String script;
    protected Class<? extends KeyValuesFinder> valuesFinderClass;
    protected Class<? extends BusinessObject> businessObjectClass;
    protected String keyAttribute;
    protected String labelAttribute;
    protected Boolean includeBlankRow;
    protected Boolean includeKeyInLabel;
    protected Integer size;
    protected Integer rows;
    protected Integer cols;


    public ControlDefinitionBase() {
    }

    public boolean isDatePicker() {
        return datePicker;
    }

    /** Whether this control should have a date picker button next to the field.
     *  Valid for text fields.
     *  
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#setDatePicker(boolean)
     */
    public void setDatePicker(boolean datePicker) {
        this.datePicker=datePicker;
    }


    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#isCheckbox()
     */
    public boolean isCheckbox() {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#isHidden()
     */
    public boolean isHidden() {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#isRadio()
     */
    public boolean isRadio() {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#isSelect()
     */
    public boolean isSelect() {
        return false;
    }
    
    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#isSelect()
     */
    public boolean isMultiselect() {
        return false;
    }

    /**
     *
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#isApcSelect()
     */

    public boolean isApcSelect() {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#isText()
     */
    public boolean isText() {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#isTextarea()
     */
    public boolean isTextarea() {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#isCurrency()
     */
    public boolean isCurrency() {
        return false;
    }

    /**
     *
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#isKualiUser()
     */
    public boolean isKualiUser() {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#isWorkgroup()
     */
    public boolean isWorkflowWorkgroup() {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#isFile()
     */
    public boolean isFile() {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#isLookupHidden()
     */
    public boolean isLookupHidden() {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#isLookupReadonly()
     */
    public boolean isLookupReadonly() {
        return false;
    }
    
    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#isButton()
     */
    public boolean isButton() {
        return false;
    }
    
    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#isLink()
     */
    public boolean isLink() {
        return false;
    }
    

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#setKeyValuesFinder(java.lang.String)
     */
    public void setValuesFinderClass(Class<? extends KeyValuesFinder> valuesFinderClass) {
        if (valuesFinderClass == null) {
            throw new IllegalArgumentException("invalid (null) valuesFinderClass");
        }

        this.valuesFinderClass = valuesFinderClass;
    }

    /**
     * @return the businessObjectClass
     */
    public Class<? extends BusinessObject> getBusinessObjectClass() {
        return this.businessObjectClass;
    }

    /**
     * Used by a PersistableBusinessObjectValuesFinder to automatically query and display a list
     * of business objects as part of a select list or set of radio buttons.
     * 
     * The keyAttribute, labelAttribute, and includeKeyInLabel are used with this property.
     * 
     * @param businessObjectClass the businessObjectClass to set
     */
    public void setBusinessObjectClass(Class<? extends BusinessObject> businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) businessObjectClass");
        }

        this.businessObjectClass = businessObjectClass;
    }

    /**
     * @return the includeBlankRow
     */
    public Boolean getIncludeBlankRow() {
        return this.includeBlankRow;
    }

    /**
     * @return the includeBlankRow
     */
    public void setIncludeBlankRow(Boolean includeBlankRow) {
        this.includeBlankRow = includeBlankRow;
    }

    /**
     * @return the includeKeyInLabel
     */
    public Boolean getIncludeKeyInLabel() {
        return this.includeKeyInLabel;
    }

    /**
     * Whether to include the key in the label for select lists and radio buttons.
     */
    public void setIncludeKeyInLabel(Boolean includeKeyInLabel) {
        this.includeKeyInLabel = includeKeyInLabel;
    }

    /**
     * @return the keyAttribute
     */
    public String getKeyAttribute() {
        return this.keyAttribute;
    }

    /**
     * Attribute of the given businessObjectClass to use as the value of a select list 
     * or set of radio buttons. 
     */
    public void setKeyAttribute(String keyAttribute) {
        this.keyAttribute = keyAttribute;
    }

    /**
     * @return the labelAttribute
     */
    public String getLabelAttribute() {
        return this.labelAttribute;
    }

    /**
     * Attribute of the given businessObjectClass to use as the displayed label on a select list 
     * or set of radio buttons. 
     */
    public void setLabelAttribute(String labelAttribute) {
        this.labelAttribute = labelAttribute;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#getKeyValuesFinder()
     */
    public Class<? extends KeyValuesFinder> getValuesFinderClass() {
        return valuesFinderClass;
    }

    /**
     * Size of a text control.
     * 
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#setSize(int)
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#getSize()
     */
    public Integer getSize() {
        return size;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#hasScript()
     */
    public boolean hasScript() {
        return false;
    }

    /**
     * Number of rows to display on a text-area widget.
     * 
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#setRows(int)
     */
    public void setRows(Integer rows) {
        this.rows = rows;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#getRows()
     */
    public Integer getRows() {
        return rows;
    }

    /**
     * Number of columns to display on a text-area widget.
     * 
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#setCols(int)
     */
    public void setCols(Integer cols) {
        this.cols = cols;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#getCols()
     */
    public Integer getCols() {
        return cols;
    }

    /**
     * Directly validate simple fields.
     *
     * @see org.kuali.rice.kns.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (!isCheckbox() && !isHidden() && !isRadio() && !isSelect() && !isMultiselect() && !isApcSelect() && !isText() && !isTextarea() && !isCurrency() && !isKualiUser() && !isLookupHidden() && !isLookupReadonly() && !isWorkflowWorkgroup() && !isFile()&& !isButton() && !isLink()) {
            throw new CompletionException("error validating " + rootBusinessObjectClass.getName() + " control: unknown control type in control definition (" + "" + ")");
        }
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#getScript()
     */
    public String getScript() {
        return script;
    }

    /**
     * JavaScript script to run when a select control's value is changed.
     * 
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#setScript()
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object object) {
    	if ( !(object instanceof ControlDefinitionBase) ) {
    		return false;
    	}
    	ControlDefinitionBase rhs = (ControlDefinitionBase)object;
    	return new EqualsBuilder()
    	        .append( this.cols, rhs.cols )
    			.append( this.businessObjectClass, rhs.businessObjectClass )
    			.append( this.valuesFinderClass, rhs.valuesFinderClass )
    			.append( this.rows, rhs.rows )
    			.append( this.script, rhs.script )
    			.append( this.size, rhs.size )
    			.append( this.datePicker, rhs.datePicker )
    			.append( this.labelAttribute,rhs.labelAttribute )
    			.append( this.includeKeyInLabel, rhs.includeKeyInLabel )
    			.append( this.keyAttribute, rhs.keyAttribute )
    			.isEquals();
    }
    
    
}