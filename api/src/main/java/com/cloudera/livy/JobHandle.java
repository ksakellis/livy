/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudera.livy;

import java.util.List;
import java.util.concurrent.Future;

/**
 * A handle to a submitted job. Allows for monitoring and controlling of the running remote job.
 */
public interface JobHandle<T> extends Future<T> {

  /**
   * A collection of metrics collected from the Spark jobs triggered by this job.
   *
   * To collect job metrics on the client, Spark jobs must be registered with JobContext::monitor()
   * on the remote end.
   */
  MetricsCollection getMetrics();

  /**
   * Get corresponding spark job IDs for this job.
   */
  List<Integer> getSparkJobIds();

  /**
   * Return the current state of the job.
   */
  State getState();

  /**
   * Add a listener to the job handle. If the job's state is not SENT, a callback for the
   * corresponding state will be invoked immediately.
   *
   * @param l The listener to add.
   */
  void addListener(Listener<T> l);

  /**
   * The current state of the submitted job.
   */
  static enum State {
    SENT,
    QUEUED,
    STARTED,
    CANCELLED,
    FAILED,
    SUCCEEDED;
  }

  /**
   * A listener for monitoring the state of the job in the remote context. Callbacks are called
   * when the corresponding state change occurs.
   */
  static interface Listener<T> {

    void onJobQueued(JobHandle<T> job);

    void onJobStarted(JobHandle<T> job);

    void onJobCancelled(JobHandle<T> job);

    void onJobFailed(JobHandle<T> job, Throwable cause);

    void onJobSucceeded(JobHandle<T> job, T result);

    /**
     * Called when a monitored Spark job is started on the remote context. This callback
     * does not indicate a state change in the client job's status.
     */
    void onSparkJobStarted(JobHandle<T> job, int sparkJobId);

  }

}
