/*
 * Copyright 2013 Bazaarvoice, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bazaarvoice.ostrich.dropwizard.healthcheck;

import com.bazaarvoice.ostrich.HealthCheckResult;
import com.bazaarvoice.ostrich.HealthCheckResults;
import com.bazaarvoice.ostrich.ServicePool;
import com.google.common.base.Strings;
import com.yammer.metrics.core.HealthCheck;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A simple health check that verifies a pool has a healthy end point. Will perform a health check on at least one end
 * point, so beware the possibility of overloading your services with health checks if this is run excessively.
 */
public class ContainsHealthyEndPointCheck extends HealthCheck {
    private ServicePool<?> _pool;

    /**
     * Constructs a health check for the given pool that will show as healthy if it has at least one healthy end point.
     * @param pool The {@code ServicePool} to look for healthy end points in.
     * @param name The name of the health check. May not be empty or null.
     */
    public ContainsHealthyEndPointCheck(ServicePool<?> pool, String name) {
        super(name);
        checkArgument(!Strings.isNullOrEmpty(name));
        _pool = checkNotNull(pool);
    }

    @Override
    public Result check() throws Exception {
        HealthCheckResults results = _pool.checkForHealthyEndPoint();
        boolean healthy = results.hasHealthyResult();
        HealthCheckResult healthyResult = results.getHealthyResult();

        // Get stats about any failed health checks
        int numUnhealthy = 0;
        long totalUnhealthyResponseTimeInMicros = 0;
        for (HealthCheckResult unhealthy : results.getUnhealthyResults()) {
            ++numUnhealthy;
            totalUnhealthyResponseTimeInMicros += unhealthy.getResponseTime(TimeUnit.MICROSECONDS);
        }

        if (!healthy && numUnhealthy == 0) {
            return Result.unhealthy("No end points.");
        }

        String unhealthyMessage = numUnhealthy + " failures in " + totalUnhealthyResponseTimeInMicros + "us";
        if (!healthy) {
            return Result.unhealthy(unhealthyMessage);
        }
        return Result.healthy(healthyResult.getEndPointId() + " succeeded in " +
                healthyResult.getResponseTime(TimeUnit.MICROSECONDS) + "us; " + unhealthyMessage);
    }
}
