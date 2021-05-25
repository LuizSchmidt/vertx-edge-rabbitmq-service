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
public class Queue {

  @NonNull
  private String name;
  private boolean autoDelete;
  private boolean exclusive;
  private boolean durable = true;
  private JsonObject arguments = new JsonObject();

  public static Queue fromObject(Object value) {
    if (value != null) {
      if (value instanceof String) {
        return new Queue((String) value);
      } else if (value instanceof JsonObject) {
        return ((JsonObject) value).mapTo(Queue.class);
      }
    }
    return null;
  }
}
