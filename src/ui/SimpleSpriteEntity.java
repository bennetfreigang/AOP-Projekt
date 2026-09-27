package ui;

import engine.Entity;

public class SimpleSpriteEntity extends Entity {
    public SimpleSpriteEntity(String spriteIdentifier, OriginPresets originPreset) {
        setSprite(spriteIdentifier);
        this.origin = originPreset;
    }
}
