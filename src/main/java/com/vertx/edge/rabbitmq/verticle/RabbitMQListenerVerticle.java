package com.vertx.edge.rabbitmq.verticle;

import com.vertx.edge.utils.VoidFuture;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.QueueOptions;
import io.vertx.rabbitmq.RabbitMQMessage;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class RabbitMQListenerVerticle extends RabbitMQVerticle {

  
  
  protected Future<Void> beforeStart() {
    return Future.succeededFuture();
  }

  protected abstract void onMessage(RabbitMQMessage message);

  protected abstract void onFailure(Throwable cause);

  @Override
  public final Future<Void> afterConnectOrReconnect() {
    return this.beforeStart().compose(v -> startListening());
  }

  private Future<Void> startListening() {
    return this.rabbitmq.basicConsumer(this.queue.getName(), getQueueOptions())
        .onSuccess(consumer -> {
          log.info("Listening rabbitmq queue {}...", this.queue.getName());
          consumer.handler(this::onMessage);
          consumer.exceptionHandler(this::onFailure);
          consumer.endHandler(v -> this.onClose());
        }).compose(VoidFuture.future());
  }

  private QueueOptions getQueueOptions() {
    JsonObject options = this.config().getJsonObject("queueOptions");
    if (options != null) {
      return new QueueOptions(options);
    } else {
      return new QueueOptions();
    }
  }
  
  private void onClose() {
    log.info("listening rabbitmq queue stopped, trying to reconnect...");
    this.declareExchangeAndQueues().compose(v -> this.startListening()).onFailure(v -> onClose());
  }
}
