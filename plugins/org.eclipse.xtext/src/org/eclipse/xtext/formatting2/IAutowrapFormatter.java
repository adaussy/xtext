/*******************************************************************************
 * Copyright (c) 2014 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.formatting2;

/**
 * A strategy for formatting that is to be applied on auto wrapping.
 * 
 * @author Moritz Eysholdt - Initial contribution and API
 * 
 * @see IHiddenRegionFormatter#setOnAutowrap(IAutowrapFormatter)
 */
public interface IAutowrapFormatter { // TODO: add region
	/**
	 * Called if the region is supposed to be wrapped.
	 */
	void format(IHiddenRegionFormatter wrapped, IFormattableDocument document);
}