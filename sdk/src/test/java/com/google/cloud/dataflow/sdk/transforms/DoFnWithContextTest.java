/*
 * Copyright (C) 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.cloud.dataflow.sdk.transforms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.dataflow.sdk.transforms.Combine.CombineFn;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for {@link DoFnWithContext}. */
@RunWith(JUnit4.class)
public class DoFnWithContextTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private class NoOpDoFnWithContext extends DoFnWithContext<Void, Void> {

    private static final long serialVersionUID = 0;

    /**
     * @param c context
     */
    @ProcessElement
    public void processElement(ProcessContext c) {
    }
  }

  @Test
  public void testCreateAggregatorWithCombinerSucceeds() {
    String name = "testAggregator";
    Sum.SumLongFn combiner = new Sum.SumLongFn();

    DoFnWithContext<Void, Void> doFn = new NoOpDoFnWithContext();

    Aggregator<Long, Long> aggregator = doFn.createAggregator(name, combiner);

    assertEquals(name, aggregator.getName());
    assertEquals(combiner, aggregator.getCombineFn());
  }

  @Test
  public void testCreateAggregatorWithNullNameThrowsException() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("name cannot be null");

    DoFnWithContext<Void, Void> doFn = new NoOpDoFnWithContext();

    doFn.createAggregator(null, new Sum.SumLongFn());
  }

  @Test
  public void testCreateAggregatorWithNullCombineFnThrowsException() {
    CombineFn<Object, Object, Object> combiner = null;

    thrown.expect(NullPointerException.class);
    thrown.expectMessage("combiner cannot be null");

    DoFnWithContext<Void, Void> doFn = new NoOpDoFnWithContext();

    doFn.createAggregator("testAggregator", combiner);
  }

  @Test
  public void testCreateAggregatorWithNullSerializableFnThrowsException() {
    SerializableFunction<Iterable<Object>, Object> combiner = null;

    thrown.expect(NullPointerException.class);
    thrown.expectMessage("combiner cannot be null");

    DoFnWithContext<Void, Void> doFn = new NoOpDoFnWithContext();

    doFn.createAggregator("testAggregator", combiner);
  }

  @Test
  public void testCreateAggregatorWithSameNameThrowsException() {
    String name = "testAggregator";
    CombineFn<Double, ?, Double> combiner = new Max.MaxDoubleFn();

    DoFnWithContext<Void, Void> doFn = new NoOpDoFnWithContext();

    doFn.createAggregator(name, combiner);

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Cannot create");
    thrown.expectMessage(name);
    thrown.expectMessage("already exists");

    doFn.createAggregator(name, combiner);
  }

  @Test
  public void testCreateAggregatorsWithDifferentNamesSucceeds() {
    String nameOne = "testAggregator";
    String nameTwo = "aggregatorPrime";
    CombineFn<Double, ?, Double> combiner = new Max.MaxDoubleFn();

    DoFnWithContext<Void, Void> doFn = new NoOpDoFnWithContext();

    Aggregator<Double, Double> aggregatorOne =
        doFn.createAggregator(nameOne, combiner);
    Aggregator<Double, Double> aggregatorTwo =
        doFn.createAggregator(nameTwo, combiner);

    assertNotEquals(aggregatorOne, aggregatorTwo);
  }

  @Test
  public void testDoFnWithContextUsingAggregators() {
    NoOpDoFn<Object, Object> noOpFn = new NoOpDoFn<>();
    DoFn<Object, Object>.Context context = noOpFn.context();

    DoFn<Object, Object> fn = spy(noOpFn);
    context = spy(context);

    @SuppressWarnings("unchecked")
    Aggregator<Long, Long> agg = mock(Aggregator.class);

    Sum.SumLongFn combiner = new Sum.SumLongFn();
    Aggregator<Long, Long> delegateAggregator =
        fn.createAggregator("test", combiner);

    when(context.createAggregatorInternal("test", combiner)).thenReturn(agg);

    context.setupDelegateAggregators();
    delegateAggregator.addValue(1L);

    verify(agg).addValue(1L);
  }
}
