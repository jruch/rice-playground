/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package org.kuali.rice.kew.docsearch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.BusinessObjectBase;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;


/**
 * Model bean representing document searches.  Persisted each search as part of the users saved searches.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocSearchCriteriaDTO extends BusinessObjectBase implements BusinessObject, DocumentRouteHeaderEBO {

    private static final long serialVersionUID = -5738747438282249790L;

    public static final String ADVANCED_SEARCH_INDICATOR_STRING = "YES";
    public static final String SUPER_USER_SEARCH_INDICATOR_STRING = "YES";
    public static final int DEFAULT_PAGE_SIZE = 10;

    private String namedSearch; // if populated the name of the search that they want to save
    private Integer pageSize; // the number of items to display on a page of results
    private String routeHeaderId; // id generated by KEW
    private String docRouteStatus; // route status of the document
    private String docTitle; // document title provided by the application
    private String appDocId; // application provided ID - defaults to routeHeaderId
    private String overrideInd; // flag to indicate overridden business values - set by app
    private String initiator; // network Id of the person who initiated the document
    private String viewer; // network Id of the person who is currently viewing the document
    private String workgroupViewerNamespace; //group namespace of the group that has had an action request to the document
    private String workgroupViewerName; // workgroup Id that has had an action request to the document
    private String approver; // network Id of the person who is approving the document
    private String docRouteNodeId; // current level of routing, i.e. which route method is the document currently in
    private String docRouteNodeLogic; // exactly, before or after
    private String docVersion; // document version
    private String docTypeFullName; // the fullname for the document's docType

    //date range properties
    private String fromDateCreated; // the begin range for DateCreated
    private String fromDateLastModified; // the begin range for LastModified
    private String fromDateApproved; // the begin range for Approved
    private String fromDateFinalized; // the begin range for Finalized
    private String toDateCreated; // the end range for created
    private String toDateLastModified; // the end range for last modified
    private String toDateApproved; // the end range for approved
    private String toDateFinalized; // the end range for finalized
    private java.sql.Timestamp dateCreated; //fake date for DD
    
    // criteria processing
    private List<Row> searchableAttributeRows = new ArrayList<Row>();

    // searchable attribute properties
    private List<SearchAttributeCriteriaComponent> searchableAttributes = new ArrayList<SearchAttributeCriteriaComponent>();

    //properties to preserve view from saved and history searches as well as generate results
    private String isAdvancedSearch;
    private String superUserSearch = "NO";

    // used as an "out" parameter to indicate the threshold for the search
    private Integer threshold = Integer.valueOf(DocumentSearchGenerator.DEFAULT_SEARCH_RESULT_CAP);
    private Integer fetchLimit = Integer.valueOf(0);

    // used as an "out" parameter to indicate that the rows fetched for this criteria are over the indicated threshold
    private boolean isOverThreshold = false;

    // used as an "out" prameter to indicate the number of rows that were filtered for security
    private int securityFilteredRows = 0;

    // below used when doing a document search from API
    private boolean overridingUserSession = false;
    private boolean saveSearchForUser = false;

    private boolean onlyDocTypeFilled = false;
    
    public DocSearchCriteriaDTO() {
        super();
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#isStandardCriteriaConsideredEmpty(boolean)
	 */
    public boolean isStandardCriteriaConsideredEmpty(boolean excludeDocumentTypeName) {
        boolean docTypeNameIsEmpty = Utilities.isEmpty(this.docTypeFullName);
        boolean standardFieldsAreEmpty = ((Utilities.isEmpty(routeHeaderId)) &&
        /* (Utilities.isEmpty(overrideInd)) && */
        (Utilities.isEmpty(initiator)) && (Utilities.isEmpty(workgroupViewerName)) &&
        /* (Utilities.isEmpty(docRouteNodeLogic)) && */
        (Utilities.isEmpty(docVersion)) && (Utilities.isEmpty(fromDateCreated)) && (Utilities.isEmpty(toDateCreated)) && (Utilities.isEmpty(appDocId)) && (Utilities.isEmpty(approver)) && (Utilities.isEmpty(docRouteNodeId)) && (Utilities.isEmpty(docRouteStatus)) && (Utilities.isEmpty(docTitle)) && (Utilities.isEmpty(viewer)) && (Utilities.isEmpty(fromDateApproved)) && (Utilities.isEmpty(toDateApproved)) && (Utilities.isEmpty(fromDateFinalized)) && (Utilities.isEmpty(toDateFinalized)) && (Utilities.isEmpty(fromDateLastModified)) && (Utilities.isEmpty(toDateLastModified)));
        if (excludeDocumentTypeName) {
            return standardFieldsAreEmpty;
        } else {
            return docTypeNameIsEmpty && standardFieldsAreEmpty;
        }
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getDocRouteNodeLogic()
	 */
    public String getDocRouteNodeLogic() {
        return docRouteNodeLogic;
    }

    public void setDocRouteNodeLogic(String docRouteLevelLogic) {
        this.docRouteNodeLogic = docRouteLevelLogic;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getAppDocId()
	 */
    public String getAppDocId() {
        return appDocId;
    }

    public void setAppDocId(String appDocId) {
        this.appDocId = appDocId;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getApprover()
	 */
    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getDocRouteNodeId()
	 */
    public String getDocRouteNodeId() {
        return docRouteNodeId;
    }

    public void setDocRouteNodeId(String docRouteLevel) {
        this.docRouteNodeId = docRouteLevel;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getDocRouteStatus()
	 */
    public String getDocRouteStatus() {
        return docRouteStatus;
    }

    public void setDocRouteStatus(String docRouteStatus) {
        this.docRouteStatus = docRouteStatus;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getDocTitle()
	 */
    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getDocTypeFullName()
	 */
    public String getDocTypeFullName() {
        return docTypeFullName;
    }

    public void setDocTypeFullName(String docTypeFullName) {
        this.docTypeFullName = docTypeFullName;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getDocVersion()
	 */
    public String getDocVersion() {
        return docVersion;
    }

    public void setDocVersion(String docVersion) {
        this.docVersion = docVersion;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getInitiator()
	 */
    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getOverrideInd()
	 */
    public String getOverrideInd() {
        return overrideInd;
    }

    public void setOverrideInd(String overrideInd) {
        this.overrideInd = overrideInd;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getRouteHeaderId()
	 */
    public String getRouteHeaderId() {
        return routeHeaderId;
    }

    public void setRouteHeaderId(String routeHeaderId) {
        this.routeHeaderId = routeHeaderId;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getViewer()
	 */
    public String getViewer() {
        return viewer;
    }

    public void setViewer(String viewer) {
        this.viewer = viewer;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getPageSize()
	 */
    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getFromDateApproved()
	 */
    public String getFromDateApproved() {
        return fromDateApproved;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getFromDateCreated()
	 */
    public String getFromDateCreated() {
        return fromDateCreated;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getFromDateFinalized()
	 */
    public String getFromDateFinalized() {
        return fromDateFinalized;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getFromDateLastModified()
	 */
    public String getFromDateLastModified() {
        return fromDateLastModified;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getToDateApproved()
	 */
    public String getToDateApproved() {
        return toDateApproved;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getToDateCreated()
	 */
    public String getToDateCreated() {
        return toDateCreated;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getToDateFinalized()
	 */
    public String getToDateFinalized() {
        return toDateFinalized;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getToDateLastModified()
	 */
    public String getToDateLastModified() {
        return toDateLastModified;
    }

    public void setFromDateApproved(String fromDateApproved) {
        this.fromDateApproved = safeTrimmer(fromDateApproved);
    }

    public void setFromDateCreated(String fromDateCreated) {
        this.fromDateCreated = safeTrimmer(fromDateCreated);
    }

    public void setFromDateFinalized(String fromDateFinalized) {
        this.fromDateFinalized = safeTrimmer(fromDateFinalized);
    }

    public void setFromDateLastModified(String fromDateLastModified) {
        this.fromDateLastModified = safeTrimmer(fromDateLastModified);
    }

    public void setToDateApproved(String toDateApproved) {
        this.toDateApproved = safeTrimmer(toDateApproved);
    }

    public void setToDateCreated(String toDateCreated) {
        this.toDateCreated = safeTrimmer(toDateCreated);
    }

    public void setToDateFinalized(String toDateFinalized) {
        this.toDateFinalized = safeTrimmer(toDateFinalized);
    }

    public void setToDateLastModified(String toDateLastModified) {
        this.toDateLastModified = safeTrimmer(toDateLastModified);
    }

    private String safeTrimmer(String value) {
        if (!Utilities.isEmpty(value)) {
            return value.trim();
        }
        return value;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getNamedSearch()
	 */
    public String getNamedSearch() {
        return namedSearch;
    }

    public void setNamedSearch(String namedSearch) {
        this.namedSearch = namedSearch;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getDocumentSearchAbbreviatedString()
	 */
    public String getDocumentSearchAbbreviatedString() {
        StringBuffer abbreviatedString = new StringBuffer();
        String dateApprovedString = getRangeString(this.toDateApproved, this.fromDateApproved);
        String dateCreatedString = getRangeString(this.toDateCreated, this.fromDateCreated);
        String dateLastModifiedString = getRangeString(this.toDateLastModified, this.fromDateLastModified);
        String dateFinalizedString = getRangeString(this.toDateFinalized, this.fromDateFinalized);
        if (appDocId != null && !"".equals(appDocId.trim())) {
            abbreviatedString.append("Application Document Id=").append(appDocId).append("; ");
        }
        if (approver != null && !"".equals(approver.trim())) {
            abbreviatedString.append("Approver Network Id=").append(approver).append("; ");
        }
        if (docRouteNodeId != null && !"".equals(docRouteNodeId.trim())) {
            RouteNode routeNode = KEWServiceLocator.getRouteNodeService().findRouteNodeById(new Long(docRouteNodeId));
            abbreviatedString.append("Document Route Node=").append(routeNode.getRouteNodeName()).append("; ");
        }
        if (docRouteStatus != null && !"".equals(docRouteStatus.trim())) {
            abbreviatedString.append("Document Route Status=").append(docRouteStatus).append("; ");
        }
        if (docTitle != null && !"".equals(docTitle.trim())) {
            abbreviatedString.append("Document Title=").append(docTitle).append("; ");
        }
        if (docTypeFullName != null && !"".equals(docTypeFullName.trim())) {
            abbreviatedString.append("Document Type=").append(docTypeFullName).append("; ");
        }
        if (initiator != null && !"".equals(initiator.trim())) {
            abbreviatedString.append("Initiator Network Id=").append(initiator).append("; ");
        }
        if (routeHeaderId != null) {
            abbreviatedString.append("Document Id=").append(routeHeaderId.toString()).append("; ");
        }
        if (viewer != null && !"".equals(viewer.trim())) {
            abbreviatedString.append("Viewer Network Id=").append(viewer).append("; ");
        }
        if (workgroupViewerName != null) {
            abbreviatedString.append("Workgroup Viewer Id=").append(workgroupViewerName).append(";");
        }
        if (dateLastModifiedString != null) {
            abbreviatedString.append("Date Last Modified=").append(dateLastModifiedString).append("; ");
        }
        if (dateFinalizedString != null) {
            abbreviatedString.append("Date Finalized=").append(dateFinalizedString).append("; ");
        }
        if (dateCreatedString != null) {
            abbreviatedString.append("Date Created=").append(dateCreatedString).append("; ");
        }
        if (dateApprovedString != null) {
            abbreviatedString.append("Date Approved=").append(dateApprovedString).append("; ");
        }

        Set<String> alreadyAddedRangeAttributes = new HashSet<String>();
        for (Iterator iter = searchableAttributes.iterator(); iter.hasNext();) {
            SearchAttributeCriteriaComponent searchAttributeEntry = (SearchAttributeCriteriaComponent) iter.next();
            if (!Utilities.isEmpty(searchAttributeEntry.getValue())) {
                // single value entered
                if (searchAttributeEntry.isRangeSearch()) {
                    // if search attribute criteria component is member of a range we must find it's potential matching partner to build the string
                    if (!alreadyAddedRangeAttributes.contains(searchAttributeEntry.getSavedKey())) {
                        // the key has not been processed yet
                        String lowerSearchAttributeRangeValue = (searchAttributeEntry.getFormKey().startsWith(SearchableAttribute.RANGE_LOWER_BOUND_PROPERTY_PREFIX)) ? searchAttributeEntry.getValue() : null;
                        String upperSearchAttributeRangeValue = (searchAttributeEntry.getFormKey().startsWith(SearchableAttribute.RANGE_UPPER_BOUND_PROPERTY_PREFIX)) ? searchAttributeEntry.getValue() : null;
                        // loop through the attributes to find this search attribute criteria components potential match
                        for (Iterator iterator = searchableAttributes.iterator(); iterator.hasNext();) {
                            SearchAttributeCriteriaComponent testSearchCriteria = (SearchAttributeCriteriaComponent) iterator.next();
                            if ((testSearchCriteria.getSavedKey().equals(searchAttributeEntry.getSavedKey())) && (!(testSearchCriteria.getFormKey().equals(searchAttributeEntry.getFormKey())))) {
                                // we found the other side of the range
                                if (lowerSearchAttributeRangeValue == null) {
                                    lowerSearchAttributeRangeValue = (testSearchCriteria.getFormKey().startsWith(SearchableAttribute.RANGE_LOWER_BOUND_PROPERTY_PREFIX)) ? testSearchCriteria.getValue() : null;
                                }
                                if (upperSearchAttributeRangeValue == null) {
                                    upperSearchAttributeRangeValue = (testSearchCriteria.getFormKey().startsWith(SearchableAttribute.RANGE_UPPER_BOUND_PROPERTY_PREFIX)) ? testSearchCriteria.getValue() : null;
                                }
                                break;
                            }
                        }
                        // we should have valid values for the 'to' and 'from' range field values by now
                        abbreviatedString.append(searchAttributeEntry.getSavedKey()).append("=").append(getRangeString(lowerSearchAttributeRangeValue, upperSearchAttributeRangeValue)).append(";");
                        alreadyAddedRangeAttributes.add(searchAttributeEntry.getSavedKey());
                    }
                } else {
                    abbreviatedString.append(searchAttributeEntry.getSavedKey()).append("=").append(searchAttributeEntry.getValue()).append(";");
                }
            } else if (!Utilities.isEmpty(searchAttributeEntry.getValues())) {
                // multiple values entered
                StringBuffer tempAbbreviatedString = new StringBuffer();
                tempAbbreviatedString.append(searchAttributeEntry.getSavedKey()).append("=");
                boolean firstValue = true;
                for (Iterator iterator = searchAttributeEntry.getValues().iterator(); iterator.hasNext();) {
                    String value = (String) iterator.next();
                    if (StringUtils.isNotBlank(value)) {
                        if (firstValue) {
                            tempAbbreviatedString.append(value);
                            firstValue = false;
                        } else {
                            tempAbbreviatedString.append(" or ").append(value);
                        }
                    }
                }
                String testString = tempAbbreviatedString.toString().replaceAll("=", "").replaceAll(" or ", "");
                if (testString.trim().length() > 0) {
                    abbreviatedString.append(tempAbbreviatedString).append(";");
                }
            }
        }

        return abbreviatedString.toString();
    }

    private String getRangeString(String to, String from) {
        String dateString = null;
        if (to != null && !"".equals(to.trim()) && from != null && !"".equals(from.trim())) {
            dateString = "(" + from + " - " + to + ")";
        } else {
            if (to != null && !"".equals(to.trim())) {
                dateString = "to " + to;
            } else if (from != null && !"".equals(from.trim())) {
                dateString = "from " + from;
            } else {
                //
            }
        }
        return dateString;
    }

    /**
     * TODO this is here for historic reasons and has been replaced by a managed state in the CriteriaDTO... eliminate
     * @deprecated
     */
    public boolean isAdvancedSearch() {
        return ((appDocId != null && !"".equals(appDocId.trim())) || (approver != null && !"".equals(approver.trim())) || (docRouteNodeId != null && !"".equals(docRouteNodeId.trim())) || (docRouteStatus != null && !"".equals(docRouteStatus.trim())) || (docTitle != null && !"".equals(docTitle.trim())) || (viewer != null && !"".equals(viewer.trim())) || (fromDateApproved != null && !"".equals(fromDateApproved.trim())) || (toDateApproved != null && !"".equals(toDateApproved.trim())) || (toDateFinalized != null && !"".equals(toDateFinalized.trim())) || (fromDateFinalized != null && !"".equals(fromDateFinalized.trim())) || (toDateLastModified != null && !"".equals(toDateLastModified.trim())) || (fromDateLastModified != null && !"".equals(fromDateLastModified.trim())));
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getWorkgroupViewerName()
	 */
    public String getWorkgroupViewerName() {
        return workgroupViewerName;
    }

    public void setWorkgroupViewerName(String workgroupViewerName) {
        this.workgroupViewerName = workgroupViewerName;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getIsAdvancedSearch()
	 */
    public String getIsAdvancedSearch() {
        return isAdvancedSearch;
    }

    public void setIsAdvancedSearch(String isAdvancedSearch) {
        this.isAdvancedSearch = isAdvancedSearch;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getSuperUserSearch()
	 */
    public String getSuperUserSearch() {
        return superUserSearch;
    }

    public void setSuperUserSearch(String superUserSearch) {
        this.superUserSearch = superUserSearch;
    }
    

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#isOverThreshold()
	 */
    public boolean isOverThreshold() {
        return isOverThreshold;
    }

    public void setOverThreshold(boolean isOverThreshold) {
        this.isOverThreshold = isOverThreshold;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getSecurityFilteredRows()
	 */
    public int getSecurityFilteredRows() {
        return securityFilteredRows;
    }

    public void setSecurityFilteredRows(int securityFilteredRows) {
        this.securityFilteredRows = securityFilteredRows;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getThreshold()
	 */
    public Integer getThreshold() {
        return this.threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getFetchLimit()
	 */
    public Integer getFetchLimit() {
        return this.fetchLimit;
    }

    public void setFetchLimit(Integer fetchLimit) {
        this.fetchLimit = fetchLimit;
    }

    public void addSearchableAttribute(SearchAttributeCriteriaComponent searchableAttribute) {
        searchableAttributes.add(searchableAttribute);
    }

    /**
     * @param searchableAttributes The searchableAttributes to set.
     */
    public void setSearchableAttributes(List<SearchAttributeCriteriaComponent> searchableAttributes) {
        this.searchableAttributes = searchableAttributes;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getSearchableAttributes()
	 */
    public List<SearchAttributeCriteriaComponent> getSearchableAttributes() {
        return searchableAttributes;
    }

    public void setSearchableAttributeRows(List<Row> searchableAttributeRows) {
        this.searchableAttributeRows = searchableAttributeRows;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getSearchableAttributeRows()
	 */
    public List<Row> getSearchableAttributeRows() {
        return searchableAttributeRows;
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getProcessedSearchableAttributeRows()
	 */
    public List<Row> getProcessedSearchableAttributeRows() {
        return searchableAttributeRows;
    }

    public void addSearchableAttributeRow(Row row) {
        searchableAttributeRows.add(row);
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getSearchableAttributeRow(int)
	 */
    public Row getSearchableAttributeRow(int index) {
        while (getSearchableAttributeRows().size() <= index) {
            Row row = new Row(new ArrayList<Field>());
            getSearchableAttributeRows().add(row);
        }
        return (Row) getSearchableAttributeRows().get(index);
    }

    public void setSearchableAttributeRow(int index, Row row) {
        searchableAttributeRows.set(index, row);
    }

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#isOverridingUserSession()
	 */
    public boolean isOverridingUserSession() {
        return this.overridingUserSession;
    }

    public void setOverridingUserSession(boolean overridingUserSession) {
        this.overridingUserSession = overridingUserSession;
    }

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#isSaveSearchForUser()
	 */
	public boolean isSaveSearchForUser() {
		return this.saveSearchForUser;
	}

	public void setSaveSearchForUser(boolean saveSearchForUser) {
		this.saveSearchForUser = saveSearchForUser;
	}

    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getWorkgroupViewerNamespace()
	 */
    public String getWorkgroupViewerNamespace() {
        return this.workgroupViewerNamespace;
    }

    public void setWorkgroupViewerNamespace(String workgroupViewerNamespace) {
        this.workgroupViewerNamespace = workgroupViewerNamespace;
    }

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@Override
	protected LinkedHashMap toStringMapper() {
		// TODO chris - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.BusinessObject#refresh()
	 */
	public void refresh() {
		// TODO chris - THIS METHOD NEEDS JAVADOCS
		
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO#getDateCreated()
	 */
	public java.sql.Timestamp getDateCreated() {
		return this.dateCreated;
	}

	/**
	 * @param dateCreated the dateCreated to set
	 */
	public void setDateCreated(java.sql.Timestamp dateCreated) {
		this.dateCreated = dateCreated;
	}

	/**
	 * @return the onlyDocTypeFilled
	 */
	public boolean isOnlyDocTypeFilled() {
		return this.onlyDocTypeFilled;
	}

	/**
	 * @param onlyDocTypeFilled the onlyDocTypeFilled to set
	 */
	public void setOnlyDocTypeFilled(boolean onlyDocTypeFilled) {
		this.onlyDocTypeFilled = onlyDocTypeFilled;
	}


}