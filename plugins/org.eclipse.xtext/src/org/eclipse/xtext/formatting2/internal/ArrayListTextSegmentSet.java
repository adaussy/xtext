/*******************************************************************************
 * Copyright (c) 2014 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.formatting2.internal;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.xtext.formatting2.IMerger;
import org.eclipse.xtext.formatting2.ITextSegment;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author Moritz Eysholdt - Initial contribution and API
 */
public class ArrayListTextSegmentSet<T> extends TextSegmentSet<T> {

	private final List<T> contents = Lists.newArrayList();

	public ArrayListTextSegmentSet(Function<? super T, ? extends ITextSegment> region, Function<? super T, String> title) {
		super(region, title);
	}

	@Override
	public void add(T segment, IMerger<T> merger) throws ConflictingRegionsException {
		Preconditions.checkNotNull(segment);
		getTraces().put(segment, new RegionTrace(getTitle(segment), getRegion(segment)));
		if (contents.isEmpty()) {
			contents.add(segment);
		} else {
			int searchResult = Collections.binarySearch(contents, segment, new RegionComparator<T>(getRegionAccess()));
			if (searchResult >= 0)
				replaceExistingEntry(segment, searchResult, merger);
			else
				insertAtIndex(segment, -searchResult - 1, merger);
		}
	}

	@Override
	public T get(T segment) {
		int searchResult = Collections.binarySearch(contents, segment, new RegionComparator<T>(getRegionAccess()));
		return searchResult >= 0 ? contents.get(searchResult) : null;
	}

	protected void insertAtIndex(T segment, int newIndex, IMerger<T> merger) throws ConflictingRegionsException {
		List<T> conflicting = null;
		int low = newIndex;
		while (--low >= 0) {
			T item = contents.get(low);
			if (isConflict(item, segment)) {
				if (conflicting == null)
					conflicting = Lists.newArrayList();
				conflicting.add(item);
			} else
				break;
		}
		int high = newIndex - 1;
		while (++high < contents.size()) {
			T item = contents.get(high);
			if (isConflict(item, segment)) {
				if (conflicting == null)
					conflicting = Lists.newArrayList();
				conflicting.add(item);
			} else
				break;
		}
		if (conflicting == null) {
			getTraces().put(segment, new RegionTrace(getTitle(segment), getRegion(segment)));
			contents.add(newIndex, segment);
		} else {
			conflicting.add(0, segment);
			try {
				T merged = merger != null ? merger.merge(conflicting) : null;
				if (merged != null) {
					for (int i = high - 1; i > low; i--)
						contents.remove(i);
					getTraces().put(merged, new RegionTrace(getTitle(merged), getRegion(merged)));
					contents.add(low + 1, merged);
				} else {
					int segmentLengh = getRegion(segment).getLength();
					int totalLength = 0;
					for (int i = 1; i < conflicting.size(); i++)
						totalLength += getRegion(conflicting.get(i)).getLength();
					if (segmentLengh >= totalLength)
						for (int i = high - 1; i > low; i--)
							contents.remove(i);
					if (segmentLengh > totalLength) {
						getTraces().put(segment, new RegionTrace(getTitle(segment), getRegion(segment)));
						contents.add(low + 1, segment);
					}
					handleConflict(conflicting, null);
				}
			} catch (ConflictingRegionsException e) {
				throw e;
			} catch (Exception e) {
				handleConflict(conflicting, e);
			}
		}
	}

	@Override
	public Iterator<T> iterator() {
		return Iterables.unmodifiableIterable(contents).iterator();
	}

	protected void replaceExistingEntry(T segment, int index, IMerger<T> merger) throws ConflictingRegionsException {
		T existing = contents.get(index);
		List<T> conflicting = ImmutableList.of(segment, existing);
		try {
			T merged = merger != null ? merger.merge(conflicting) : null;
			if (merged != null) {
				getTraces().put(merged, new RegionTrace(getTitle(merged), getRegion(merged)));
				contents.set(index, merged);
			} else {
				contents.remove(index);
				handleConflict(conflicting, null);
			}
		} catch (ConflictingRegionsException e) {
			throw e;
		} catch (Exception e) {
			handleConflict(conflicting, e);
		}
	}

}
