/*
 * Vert.x Edge, open source.
 * Copyright (C) 2020-2021 Vert.x Edge
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vertx.edge.rabbitmq;

import com.vertx.edge.annotations.ServiceProvider;
import com.vertx.edge.deploy.service.RecordService;
import com.vertx.edge.deploy.service.secret.Secret;
import com.vertx.edge.rabbitmq.type.RabbitMQType;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;

/**
 * @author Luiz Schmidt
 */
@ServiceProvider(name = RabbitMQService.SERVICE)
public class RabbitMQService implements RecordService {

  public static final String SERVICE = "rabbitmq-service";

  public static Future<RabbitMQClient> rabbitmq(ServiceDiscovery discovery) {
    Promise<RabbitMQClient> promise = Promise.promise();

    RabbitMQType.getRabbitClient(discovery, new JsonObject().put("name", SERVICE)).onSuccess(promise::complete)
        .onFailure(cause -> promise.fail(RecordService.buildErrorMessage(SERVICE, cause)));

    return promise.future();
  }

  public Future<Record> newRecord(Vertx vertx, JsonObject config) {
    Promise<Record> promise = Promise.promise();
    buildRabbitMQOptions(vertx, config)
        .onSuccess(opts -> promise.complete(RabbitMQType.createRecord(SERVICE, new JsonObject(), opts)))
        .onFailure(promise::fail);
    return promise.future();
  }

  private static Future<JsonObject> buildRabbitMQOptions(Vertx vertx, JsonObject config) {
    return Secret.getUsernameAndPassword(vertx, config).compose(v -> Future.succeededFuture(config.mergeIn(v)));
  }
}
