/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.test.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.test.KIMTestCase;

/**
 * Test the RoleService
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RoleServiceTest extends KIMTestCase {

	private RoleService roleService;

	public void setUp() throws Exception {
		super.setUp();
		setRoleService(KIMServiceLocator.getRoleService());
	}
	
	@Test
	public void testPrincipaHasRoleOfDirectAssignment() {
		List <String>roleIds = new ArrayList<String>();
		roleIds.add("r1");
		assertTrue( "p1 has direct role r1", getRoleService().principalHasRole("p1", roleIds, null ));	
		//assertFalse( "p4 has no direct/higher level role r1", getRoleService().principalHasRole("p4", roleIds, null ));	
		AttributeSet qualification = new AttributeSet();
		qualification.put("Attribute 2", "CHEM");
		assertTrue( "p1 has direct role r1 with rp2 attr data", getRoleService().principalHasRole("p1", roleIds, qualification ));	
		qualification.clear();
		//requested qualification rolls up to a higher element in some hierarchy 
		// method not implemented yet, not quite clear how this works
		qualification.put("Attribute 3", "PHYS");
		assertTrue( "p1 has direct role r1 with rp2 attr data", getRoleService().principalHasRole("p1", roleIds, qualification ));	
	}

	@Test
	public void testPrincipalHasRoleOfHigherLevel() {
		// "p3" is in "r2" and "r2 contains "r1"
		List <String>roleIds = new ArrayList<String>();
		roleIds.add("r2");
		assertTrue( "p1 has assigned in higher level role r1", getRoleService().principalHasRole("p1", roleIds, null ));		
	}
	
	@Test
	public void testPrincipalHasRoleContainsGroupAssigned() {
		// "p2" is in "g1" and "g1" assigned to "r2"
		List <String>roleIds = new ArrayList<String>();
		roleIds.add("r2");
		assertTrue( "p2 is assigned to g1 and g1 assigned to r2", getRoleService().principalHasRole("p2", roleIds, null ));		
	}

	@Test
	public void testGetPrincipalsFromCircularRoles() {
		// "p2" is in "g1" and "g1" assigned to "r2"
		List <String>roleIds = new ArrayList<String>();
		Collection <String>rolePrincipalIds = null;
		roleIds.add("r101");
		rolePrincipalIds = getRoleService().getRoleMemberPrincipalIds("ADDL_ROLES_TESTS", "Role A", null);
		assertNotNull(rolePrincipalIds);
		assertEquals("RoleTwo should have 6 principal ids", 6, rolePrincipalIds.size());
//		assertTrue( "p3 must belong to role", memberPrincipalIds.contains(principal3Id) );
//		assertTrue( "p2 must belong to role (assigned via group)", memberPrincipalIds.contains(principal2Id) );
//		assertTrue( "p1 must belong to r2 (via r1)", memberPrincipalIds.contains(principal1Id) );
	}
	
	public RoleService getRoleService() {
		return this.roleService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

}
