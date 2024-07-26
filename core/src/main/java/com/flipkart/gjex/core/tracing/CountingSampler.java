/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.flipkart.gjex.core.tracing;

import java.util.BitSet;
import java.util.Random;

/**
 * A counting sampler that check if sampling must happen based on a counter initialized for a sampling rate ranging from 0.0 to 1.0.
 * This implementation is based on the Openzipkin-Brave {@linkplain https://github.com/openzipkin/brave/blob/master/brave/src/main/java/brave/sampler/CountingSampler.java}
 *
 * Description from source:
 * This initializes a random bitset of size 100 (corresponding to 1% granularity). This means that it is accurate in units of 100
 * traces. At runtime, this loops through the bitset, returning the value according to a counter.
 *
 * @author regu.b
 *
 */
public class CountingSampler {

	private int i; // counter
	private final BitSet sampleDecisions;

	public CountingSampler (float rate) {
		if (rate < 0.0f || rate > 1) {
			throw new IllegalArgumentException("rate should be between 0.0 and 1: was " + rate);
		}
		if (rate == 0.0f) {
			this.sampleDecisions = new BitSet(100); // empty bitset to denote nothing is sampled
		} else {
			int outOf100 = (int) (rate * 100.0f);
		    this.sampleDecisions = this.randomBitSet(100, outOf100, new Random());
		}
	}

	public boolean isSampled() {
		boolean result = sampleDecisions.get(i++);
		if (i == 100) i = 0;
		return result;
	}

	/**
	 * Reservoir sampling algorithm borrowed from Stack Overflow.
	 *
	 * http://stackoverflow.com/questions/12817946/generate-a-random-bitset-with-n-1s
	 */
	private BitSet randomBitSet(int size, int cardinality, Random rnd) {
		BitSet result = new BitSet(size);
		int[] chosen = new int[cardinality];
		int i;
		for (i = 0; i < cardinality; ++i) {
			chosen[i] = i;
			result.set(i);
		}
		for (; i < size; ++i) {
			int j = rnd.nextInt(i + 1);
			if (j < cardinality) {
				result.clear(chosen[j]);
				result.set(i);
				chosen[j] = i;
			}
		}
		return result;
	}
}
