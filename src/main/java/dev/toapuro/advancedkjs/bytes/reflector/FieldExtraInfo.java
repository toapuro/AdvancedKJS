package dev.toapuro.advancedkjs.bytes.reflector;

import dev.latvian.mods.rhino.JavaMembers;

import java.lang.reflect.Field;

public class FieldExtraInfo extends JavaMembers.FieldInfo {
    public boolean hidden;

    public FieldExtraInfo(Field f, boolean hidden) {
        super(f);
        this.hidden = hidden;
    }
}
