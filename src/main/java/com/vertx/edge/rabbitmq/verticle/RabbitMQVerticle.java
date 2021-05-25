package com.vertx.edge.rabbitmq.verticle;

import java.util.Objects;

import com.vertx.edge.rabbitmq.RabbitMQService;
import com.vertx.edge.rabbitmq.helper.RabbitMQHelper;
import com.vertx.edge.rabbitmq.helper.declare.Exchange;
import com.vertx.edge.rabbitmq.helper.declare.Queue;
import com.vertx.edge.verticle.BaseVerticle;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.rabbitmq.RabbitMQClient;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class RabbitMQVerticle extends BaseVerticle {

  private static final int DEFAULT_CHECK_INTERVAL = 15;
  private static final long SECONDS = 1000L;
  private static final String CHECK_INTERVAL = "networkRecoveryInterval";

  protected RabbitMQClient rabbitmq;
  protected Exchange exchange;
  protected Queue queue;
  protected boolean bind;
  protected String routingKey;

  public abstract Future<Void> afterConnectOrReconnect();

  @Override
  protected final void up(Promise<Void> promise) {
    RabbitMQService.rabbitmq(discovery).compose(client -> {
      this.rabbitmq = client;
      this.exchange = Exchange.fromObject(config().getValue("exchange"));
      this.queue = Queue.fromObject(config().getValue("queue"));
      this.bind = config().getBoolean("bind", Boolean.TRUE);
      this.routingKey = config().getString("routingKey");

      Objects.requireNonNull(queue, "Missing configuration 'queue'.");
      return this.connect();
    }).onSuccess(client -> promise.complete()).onFailure(promise::fail);
  }

  private final Future<Void> connect() {
    return this.rabbitmq.start()
        .compose(v -> declareExchangeAndQueues())
        .compose(v -> this.afterConnectOrReconnect())
        .onComplete(v -> this.createReconnectListener());
  }

  protected Future<Void> declareExchangeAndQueues() {
    return new RabbitMQHelper(this.rabbitmq).declare(exchange, queue, routingKey, bind);
  }

  private final Future<Void> createReconnectListener() {
    vertx.setTimer(config().getInteger(CHECK_INTERVAL, DEFAULT_CHECK_INTERVAL) * SECONDS, res -> {
      if (!this.rabbitmq.isConnected()) {
        log.warn("[AUTO] rabbitmq is not connected, trying to reconnect...");
        this.connect().onSuccess(v -> log.info("[AUTO] rabbitmq successfully reconnect!"));
      } else {
        this.createReconnectListener();
      }
    });
    return Future.succeededFuture();
  }
}
