package io.github.agentrkid.anvilmenuapi.menu;

@FunctionalInterface
public interface AnvilConsumer {
    void accept(CloseResult closeResult, String result);
}
