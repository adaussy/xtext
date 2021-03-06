/*******************************************************************************
 * Copyright (c) 2014 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.xbase.scoping.batch;

import java.util.List;

import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class FeatureScopeSessionWithCapturedLocalElements extends AbstractNestedFeatureScopeSession {

	protected FeatureScopeSessionWithCapturedLocalElements(AbstractFeatureScopeSession parent) {
		super(parent);
	}
	
	@Override
	public IFeatureScopeSession getNextCaptureLayer() {
		return getParent();
	}
	
	@Override
	protected void addLocalElements(List<IEObjectDescription> result) {
		// don't add any local elements from the parent
	}
	
	@Override
	protected void addExtensionProviders(List<ExpressionBucket> result) {
		// don't add any extension providers
	}
	
	@Override
	/* @Nullable */
	public IEObjectDescription getLocalElement(QualifiedName name) {
		return null;
	}

}
