package com.vertx.edge.rabbitmq.type;

import com.vertx.edge.rabbitmq.reference.RabbitMQServiceReference;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;

public class RabbitMQTypeImpl implements RabbitMQType {

  @Override
  public String name() {
    return RabbitMQType.TYPE;
  }

  @Override
  public ServiceReference get(Vertx vertx, ServiceDiscovery discovery, Record record, JsonObject configuration) {
    return new RabbitMQServiceReference(vertx, discovery, record, configuration);
  }

}
