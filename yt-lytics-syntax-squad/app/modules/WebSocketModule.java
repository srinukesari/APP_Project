package modules;

import akka.actor.ActorSystem;
import akka.stream.Materializer;
import com.google.inject.AbstractModule;

public class WebSocketModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ActorSystem.class).toInstance(ActorSystem.create());
        bind(Materializer.class).toInstance(Materializer.createMaterializer(ActorSystem.create()));
    }
}
