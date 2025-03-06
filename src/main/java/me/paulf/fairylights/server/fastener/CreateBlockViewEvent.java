package me.paulf.fairylights.server.fastener;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class CreateBlockViewEvent {
    public static final Event<CreateBlockViewCallback> EVENT = EventFactory.createArrayBacked(CreateBlockViewCallback.class, callbacks -> event -> {
        for (CreateBlockViewCallback callback : callbacks) {
            callback.onCreateBlockView(event);
        }
    });

    private BlockView view;

    public CreateBlockViewEvent(final BlockView view) {
        this.view = view;
    }

    public BlockView getView() {
        return this.view;
    }

    public void setView(final BlockView view) {
        this.view = view;
    }

    @FunctionalInterface
    public interface CreateBlockViewCallback {
        void onCreateBlockView(CreateBlockViewEvent event);
    }
}
