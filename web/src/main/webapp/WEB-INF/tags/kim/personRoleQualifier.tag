<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<%@ tag body-content="empty" %>
<%@ attribute name="roleIdx" required="true" %>
<%@ attribute name="role" required="true" type="org.kuali.rice.kim.bo.ui.PersonDocumentRole" %>
<c:set var="docRolePrncplAttributes" value="${DataDictionary.KimDocumentRoleMember.attributes}" />
<c:set var="docRoleRspActionAttributes" value="${DataDictionary.KimDocumentRoleResponsibilityAction.attributes}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:subtab lookedUpCollectionName="roleQualifier" width="${tableWidth}" subTabTitle="Role Qualifier" useCurrentTabIndexAsKey="true">      
	<table cellpadding="0" cellspacing="0" summary="">
    	<tr>
        	<th width="5%" rowspan="20" style="border-style:none">&nbsp;</th>
			<th>&nbsp;</th> 
			<c:forEach var="attrDefn" items="${role.definitions}" varStatus="status">
       			<c:set var="attr" value="${attrDefn.value}" />
    			<%-- AttrDefn: ${attr}<br /> --%>
				<c:set var="fieldName" value="${attr.name}" />
				<c:set var="attrEntry" value="${role.attributeEntry[fieldName]}" />
		    	<kul:htmlAttributeHeaderCell attributeEntry="${attrEntry}" useShortLabel="false" />
			</c:forEach>
			<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docRolePrncplAttributes.activeFromDate}" noColon="true" /></div></th>
			<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docRolePrncplAttributes.activeToDate}" noColon="true" /></div></th>
           <c:if test="${not inquiry}">	
 			 <kul:htmlAttributeHeaderCell literalLabel="Actions"/>
           </c:if>   			
		</tr>                		
		<c:if test="${not inquiry and not readOnly}">			              					
			<tr>
				<th class="infoline">
					<c:out value="Add:" />
				</th>
				<c:forEach var="attrDefn" items="${role.definitions}" varStatus="status1">
					<c:set var="attr" value="${attrDefn.value}" />
					<c:set var="fieldName" value="${attr.name}" />
					<c:set var="attrEntry" value="${role.attributeEntry[fieldName]}" />
			       	<td align="left" valign="middle">
			       		<div align="center"> 
			      		   <kul:htmlControlAttribute property="document.roles[${roleIdx}].newRolePrncpl.qualifiers[${status1.index}].attrVal"  attributeEntry="${attrEntry}" readOnly="${readOnly}" />
			      		   <%-- 
			      		   TODO: code (probably) does not pull the remote property name properly
			      		   TODO: code does not handle multiple lookup/conversion parameters 
			      		   --%>
			       		   <c:if test="${!empty attr.lookupBoClass and not readOnly}">
			       		       <kim:roleQualifierLookup role="${role}" pathPrefix="document.roles[${roleIdx}].newRolePrncpl" attr="${attr}" />
			          	   </c:if>
			          	   
			  			</div>
						<%--
						Field: ${fieldName}<br />
						Attribute Definition: ${attr}<br />
						--%>
					</td>
				</c:forEach>	
				<td>
					<div align="center">
			            <kul:htmlControlAttribute property="document.roles[${roleIdx}].newRolePrncpl.activeFromDate"  attributeEntry="${docRolePrncplAttributes.activeFromDate}" datePicker="true" readOnly="${readOnly}" />
			  		</div>
				</td>
				<td>
					<div align="center">
					   <kul:htmlControlAttribute property="document.roles[${roleIdx}].newRolePrncpl.activeToDate"  attributeEntry="${docRolePrncplAttributes.activeToDate}" datePicker="true" readOnly="${readOnly}" />
					</div>
				</td>			        		
			   	<td class="infoline">
					<div align=center>
						<html:image property="methodToCall.addRoleQualifier.line${roleIdx}.anchor${tabKey}"
									src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton"/>
					</div>
				</td>
			</tr> 
		</c:if>        				        		
		        		<!-- use definitions map -->
		<c:if test="${fn:length(role.rolePrncpls) > 0}">	
		     <c:forEach var="rolePrncpl" items="${role.rolePrncpls}" varStatus="status1">
		         <c:if test="${fn:length(rolePrncpl.qualifiers) > 0}">	
		        	<tr>
		        		  <%-- TODO : rowspan=2 if there is responsibilityaction only --%>
		        		<c:set var="rows" value="1"/>
		        		<c:if test="${fn:length(rolePrncpl.roleRspActions) > 0}">	
		        		 	<c:set var="rows" value="2"/>
		        		 
		        		</c:if> 
						<th rowspan="${rows}"  class="infoline">
							<c:out value="${status1.index+1}" />
						</th>
				        <c:forEach var="attrDefn" items="${role.definitions}" varStatus="status">
			        	    <c:forEach var="qualifier" items="${rolePrncpl.qualifiers}" varStatus="status2">			        			    
				        		<c:if test="${attrDefn.value.name == qualifier.kimAttribute.attributeName}">
					        		<c:set var="attr" value="${attrDefn.value}" />
					        		<c:set var="fieldName" value="${attr.name}" />
					        		<c:set var="attrEntry" value="${role.attributeEntry[fieldName]}" />
				            <td align="left" valign="middle">
				                <div align="center"> 
				                	<kul:htmlControlAttribute property="document.roles[${roleIdx}].rolePrncpls[${status1.index}].qualifiers[${status.index}].attrVal"  attributeEntry="${attrEntry}" readOnly="${readOnly}" />
						      		   <c:if test="${!empty attr.lookupBoClass  and not readOnly}">
						      		       <kim:roleQualifierLookup role="${role}" pathPrefix="document.roles[${roleIdx}].rolePrncpls[${status1.index}]" attr="${attr}" />
						         	   </c:if>
								</div>
							</td>
				        		</c:if>    
				        	</c:forEach>
						</c:forEach>									
						<td>
							<div align="center">
				            <kul:htmlControlAttribute property="document.roles[${roleIdx}].rolePrncpls[${status1.index}].activeFromDate"  attributeEntry="${docRolePrncplAttributes.activeFromDate}" datePicker="true" readOnly="${readOnly}" />
			        		</div>
		        		</td>
		        		<td>
			        		<div align="center">
				            <kul:htmlControlAttribute property="document.roles[${roleIdx}].rolePrncpls[${status1.index}].activeToDate"  attributeEntry="${docRolePrncplAttributes.activeToDate}" datePicker="true" readOnly="${readOnly}" />
			        		</div>
		        		</td>
           				<c:if test="${not inquiry}">									
								<td class="infoline">
								<div align=center>
				        	     <c:choose>
				        	       <c:when test="${rolePrncpl.edit or readOnly}">
				        	          <img class='nobord' src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete2.gif' styleClass='tinybutton'/>
				        	       </c:when>
				        	       <c:otherwise>
				        	          <html:image property='methodToCall.deleteRoleQualifier.line${roleIdx}:${status1.index}.anchor${currentTabIndex}'
											src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif' styleClass='tinybutton'/>
				        	       </c:otherwise>
				        	     </c:choose>  
									</div>
                		   		</td>
               			</c:if> 		   		
					</tr>	
			        <c:if test="${fn:length(rolePrncpl.roleRspActions) > 0}">	
	     			    <tr>
			              <td colspan="12" style="padding:0px;">
			              	<kim:roleResponsibilityAction roleIdx="${roleIdx}" mbrIdx="${status1.index}" />
				          </td>
				        </tr>
					</c:if>	      
				</c:if> 
			</c:forEach>
			<tr>
	   <!-- need to decide colspan -->
	             <td colspan=15 style="padding:0px; border-style:none; height:22px; background-color:#F6F6F6">&nbsp;</td>
	        </tr>								
		</c:if>	
	</table>       
</kul:subtab>
