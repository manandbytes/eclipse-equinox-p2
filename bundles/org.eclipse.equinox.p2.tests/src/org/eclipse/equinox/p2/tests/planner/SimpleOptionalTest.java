/*******************************************************************************
 * Copyright (c) 2008, 2017 IBM Corporation and others.
 *
 * This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License 2.0 which accompanies this distribution, and is
 * available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.equinox.p2.tests.planner;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.internal.p2.director.ProfileChangeRequest;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProvisioningPlan;
import org.eclipse.equinox.p2.metadata.*;
import org.eclipse.equinox.p2.planner.IPlanner;
import org.eclipse.equinox.p2.tests.AbstractProvisioningTest;

public class SimpleOptionalTest extends AbstractProvisioningTest {
	//A dep B op, C, D op
	//X dep B

	private IInstallableUnit a1;
	private IInstallableUnit b1;

	private IProfile profile;
	private IPlanner planner;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		b1 = createIU("B", Version.create("1.0.0"), true);

		//B's dependency is missing
		IRequirement[] reqA = new IRequirement[1];
		reqA[0] = MetadataFactory.createRequirement(IInstallableUnit.NAMESPACE_IU_ID, "B", VersionRange.emptyRange, null, true, false, true);
		a1 = createIU("A", Version.create("1.0.0"), reqA);

		createTestMetdataRepository(new IInstallableUnit[] {a1, b1});

		profile = createProfile("TestProfile." + getName());
		planner = createPlanner();
	}

	public void testInstallation() {
		//Ensure that D's installation does not fail because of C's absence
		ProfileChangeRequest req = new ProfileChangeRequest(profile);
		req.addInstallableUnits(a1);
		IProvisioningPlan plan = planner.getProvisioningPlan(req, null, null);
		assertEquals(IStatus.OK, plan.getStatus().getSeverity());
		assertInstallOperand(plan, a1);
		assertInstallOperand(plan, b1);
	}
}
