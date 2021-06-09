package com.vertx.edge.rabbitmq.helper;

import java.util.Locale;

import com.vertx.edge.rabbitmq.helper.declare.Exchange;
import com.vertx.edge.rabbitmq.helper.declare.Queue;
import com.vertx.edge.utils.CompositeFutureBuilder;
import com.vertx.edge.utils.VoidFuture;

import io.vertx.core.Future;
import io.vertx.rabbitmq.RabbitMQClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RabbitMQHelper {

  @NonNull
  private RabbitMQClient client;

  public Future<Void> declare(Exchange exchange, Queue queue) {
    return this.declare(exchange, queue, null, true);
  }

  public Future<Void> declare(Exchange exchange, Queue queue, String routingKey) {
    return this.declare(exchange, queue, routingKey, true);
  }

  public Future<Void> declare(Exchange exchange, Queue queue, String routingKey, boolean bind) {
    RabbitMQHelper helper = new RabbitMQHelper(client);

    if (exchange != null && queue != null) {
      return CompositeFutureBuilder.create()
          .add(helper.declare(exchange))
          .add(helper.declare(queue))
          .all().compose(v -> {
            if (bind) {
              return helper.bindQueue(queue.getName(), exchange.getName(), routingKey);
            } else {
              return Future.succeededFuture();
            }
          });
    } else if (exchange != null) {
      return helper.declare(exchange);
    } else if (queue != null) {
      return helper.declare(queue);
    }
    return Future.succeededFuture();
  }

  public Future<Void> declare(Exchange exchange) {
    return client.exchangeDeclare(exchange.getName(), exchange.getType().name().toLowerCase(Locale.getDefault()), exchange.isDurable(),
        exchange.isAutoDelete(), exchange.getArguments());
  }

  public Future<Void> declare(Queue queue) {
    return client.queueDeclare(queue.getName(), queue.isDurable(), queue.isExclusive(), queue.isAutoDelete(),
        queue.getArguments()).compose(VoidFuture.future());
  }

  private Future<Void> bindQueue(String queue, String exchange, String routingKey) {
    return client.queueBind(queue, exchange, routingKey);
  }
}
