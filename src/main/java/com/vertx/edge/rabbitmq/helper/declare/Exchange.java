package com.vertx.edge.rabbitmq.helper.declare;

import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public class Exchange {

  @NonNull
  private String name;
  private ExchangeType type = ExchangeType.TOPIC;
  private boolean autoDelete;
  private boolean internal;
  private boolean durable = true;
  private JsonObject arguments = new JsonObject();

  public static Exchange fromObject(Object value) {
    if (value != null) {
      if (value instanceof String) {
        return new Exchange((String) value);
      } else if (value instanceof JsonObject) {
        return ((JsonObject) value).mapTo(Exchange.class);
      }
    }
    return null;
  }

}
