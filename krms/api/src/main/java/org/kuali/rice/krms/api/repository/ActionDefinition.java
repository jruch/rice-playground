package org.kuali.rice.krms.api.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;

/**
 * Concrete model object implementation of KRMS Repository Action 
 * immutable. 
 * Instances of Action can be (un)marshalled to and from XML.
 *
 * @see ActionDefinitionContract
 */
@XmlRootElement(name = ActionDefinition.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = ActionDefinition.Constants.TYPE_NAME, propOrder = {
		ActionDefinition.Elements.ID,
		ActionDefinition.Elements.NAME,
		ActionDefinition.Elements.NAMESPACE,
		ActionDefinition.Elements.DESC,
		ActionDefinition.Elements.TYPE_ID,
		ActionDefinition.Elements.RULE_ID,
		ActionDefinition.Elements.SEQUENCE_NUMBER,
		ActionDefinition.Elements.ATTRIBUTES,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class ActionDefinition implements ActionDefinitionContract, ModelObjectComplete{
	private static final long serialVersionUID = 2783959459503209577L;

	@XmlElement(name = Elements.ID, required=true)
	private String actionId;
	@XmlElement(name = Elements.NAME, required=true)
	private String name;
	@XmlElement(name = Elements.NAMESPACE, required=true)
	private String namespace;
	@XmlElement(name = Elements.DESC, required=true)
	private String description;
	@XmlElement(name = Elements.TYPE_ID, required=true)
	private String typeId;
	@XmlElement(name = Elements.RULE_ID, required=true)
	private String ruleId;
	@XmlElement(name = Elements.SEQUENCE_NUMBER, required=true)
	private Integer sequenceNumber;
	
	@XmlElementWrapper(name = Elements.ATTRIBUTES)
	@XmlElement(name = Elements.ATTRIBUTE, required=false)
	private Set<ActionAttribute> attributes;
	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _futureElements = null;
	
	
	 /** 
     * This constructor should never be called.  
     * It is only present for use during JAXB unmarshalling. 
     */
    private ActionDefinition() {
    	this.actionId = null;
    	this.name = null;
    	this.namespace = null;
    	this.description = null;
    	this.typeId = null;
    	this.ruleId = null;
    	this.sequenceNumber = null;
    	this.attributes = null;
    }
    
    /**
	 * Constructs a KRMS Repository Action object from the given builder.  
	 * This constructor is private and should only ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the Action
	 */
    private ActionDefinition(Builder builder) {
        this.actionId = builder.getActionId();
        this.name = builder.getName();
        this.namespace = builder.getNamespace();
        this.description = builder.getDescription();
        this.typeId = builder.getTypeId();
        this.ruleId = builder.getRuleId();
        this.sequenceNumber = builder.getSequenceNumber();
        Set<ActionAttribute> attrSet = Collections.emptySet();
        for (ActionAttribute.Builder b : builder.attributes){
        	attrSet.add(b.build());
        }
        this.attributes = Collections.unmodifiableSet(attrSet);
    }
    
	@Override
	public String getActionId() {
		return this.actionId;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getNamespace() {
		return this.namespace;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String getTypeId() {
		return this.typeId;
	}

	@Override
	public String getRuleId() {
		return this.ruleId;
	}

	@Override
	public Integer getSequenceNumber() {
		return this.sequenceNumber;
	}

	@Override
	public Set<ActionAttribute> getAttributes() {
		return this.attributes; 
	}

	/**
     * This builder is used to construct instances of KRMS Repository Action.  It enforces the constraints of the {@link ActionDefinitionContract}.
     */
    public static class Builder implements ActionDefinitionContract, ModelBuilder, Serializable {
        private static final long serialVersionUID = -6773634512570180267L;

        private String actionId;
        private String name;
        private String namespace;
        private String description;
        private String typeId;
        private String ruleId;
        private Integer sequenceNumber;
        private Set<ActionAttribute.Builder> attributes;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String actionId, String name, String namespace, String typeId, String ruleId, Integer sequenceNumber) {
            setActionId(actionId);
            setName(name);
            setNamespace(namespace);
            setTypeId(typeId);
            setRuleId(ruleId);
            setSequenceNumber(sequenceNumber);
        }
        
        public Builder description (String description){
        	setDescription(description);
        	return this;
        }
        public Builder attributes (Set<ActionAttribute.Builder> attributes){
        	setAttributes(attributes);
        	return this;
        }
 
        public static Builder create(String actionId, String name, String namespace, String typeId, String ruleId, Integer sequenceNumber){
        	return new Builder(actionId, name, namespace, typeId, ruleId, sequenceNumber);
        }
        /**
         * Creates a builder by populating it with data from the given {@link ActionDefinitionContract}.
         * 
         * @param contract the contract from which to populate this builder
         * @return an instance of the builder populated with data from the contract
         */
        public static Builder create(ActionDefinitionContract contract) {
        	if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }
        	Set <ActionAttribute.Builder> attrBuilderList = Collections.emptySet();
        	if (contract.getAttributes() != null){
        		for (ActionAttributeContract attrContract : contract.getAttributes()){
        			ActionAttribute.Builder myBuilder = ActionAttribute.Builder.create(attrContract);
        			attrBuilderList.add(myBuilder);
        		}
        	}
            Builder builder =  new Builder(contract.getActionId(), contract.getName(),
            		contract.getNamespace(), contract.getTypeId(), contract.getRuleId(),
            		contract.getSequenceNumber())
            			.description(contract.getDescription())
            			.attributes(attrBuilderList);
            return builder;
        }

		/**
		 * Sets the value of the id on this builder to the given value.
		 * 
		 * @param id the id value to set, must not be null or blank
		 * @throws IllegalArgumentException if the id is null or blank
		 */

        public void setActionId(String actionId) {
            if (StringUtils.isBlank(actionId)) {
                throw new IllegalArgumentException("actionId is blank");
            }
			this.actionId = actionId;
		}

     
        public void setName(String name) {
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("name is blank");
            }
			this.name = name;
		}

     
        public void setNamespace(String namespace) {
            if (StringUtils.isBlank(namespace)) {
                throw new IllegalArgumentException("namespace is blank");
            }
			this.namespace = namespace;
		}

     
		public void setDescription(String desc) {
			this.description = desc;
		}
		
		public void setTypeId(String typeId) {
			if (StringUtils.isBlank(typeId)) {
	                throw new IllegalArgumentException("KRMS type id is blank");
			}
			this.typeId = typeId;
		}
		
		public void setRuleId(String ruleId) {
			if (StringUtils.isBlank(ruleId)) {
	                throw new IllegalArgumentException("rule id is blank");
			}
			this.ruleId = ruleId;
		}
		
		public void setSequenceNumber(Integer sequenceNumber) {
			if (sequenceNumber == null) {
	                throw new IllegalArgumentException("sequence number is null");
			}
			this.sequenceNumber = sequenceNumber;
		}
		
		public void setAttributes(Set<ActionAttribute.Builder> attributes){
			if (attributes == null){
				this.attributes = Collections.emptySet();
			}
			this.attributes = Collections.unmodifiableSet(attributes);
		}
		
		@Override
		public String getActionId() {
			return actionId;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getNamespace() {
			return namespace;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getTypeId() {
			return typeId;
		}

		@Override
		public String getRuleId() {
			return ruleId;
		}

		@Override
		public Integer getSequenceNumber() {
			return sequenceNumber;
		}

		@Override
		public Set<ActionAttribute.Builder> getAttributes() {
			return attributes;
		}

		/**
		 * Builds an instance of a Action based on the current state of the builder.
		 * 
		 * @return the fully-constructed Action
		 */
        @Override
        public ActionDefinition build() {
            return new ActionDefinition(this);
        }
		
    }
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(obj, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	/**
	 * Defines some internal constants used on this class.
	 */
	static class Constants {
		final static String ROOT_ELEMENT_NAME = "Action";
		final static String TYPE_NAME = "ActionType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { CoreConstants.CommonElements.FUTURE_ELEMENTS };
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements {
		final static String ID = "actionId";
		final static String NAME = "name";
		final static String NAMESPACE = "namespace";
		final static String DESC = "description";
		final static String TYPE_ID = "typeId";
		final static String RULE_ID = "ruleId";
		final static String SEQUENCE_NUMBER = "sequenceNumber";
		final static String ATTRIBUTE = "attribute";
		final static String ATTRIBUTES = "attributes";
	}

}
