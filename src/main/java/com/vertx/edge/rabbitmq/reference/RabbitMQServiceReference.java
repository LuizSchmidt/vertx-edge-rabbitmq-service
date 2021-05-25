package com.vertx.edge.rabbitmq.reference;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.AbstractServiceReference;

public class RabbitMQServiceReference extends AbstractServiceReference<RabbitMQClient> {

  private final JsonObject config;

  public RabbitMQServiceReference(Vertx vertx, ServiceDiscovery discovery, Record record, JsonObject config) {
    super(vertx, discovery, record);
    this.config = config;
  }

  @Override
  public RabbitMQClient retrieve() {
    JsonObject result = record().getMetadata().copy();
    result.mergeIn(record().getLocation());

    if (config != null) {
      result.mergeIn(config);
    }

    RabbitMQOptions options = new RabbitMQOptions(result);
    return RabbitMQClient.create(vertx, options);
  }
}
