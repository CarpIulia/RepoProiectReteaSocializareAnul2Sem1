package socialnetwork.utils.events;

import socialnetwork.domain.Entity;
import socialnetwork.domain.Prietenie;

public class EntityChangeEvent implements Event {
    private ChangeEventType type;
    private Entity data;

    public EntityChangeEvent(ChangeEventType type, Entity data) {
        this.type = type;
        this.data = data;
    }
}
