package dev.toapuro.advancedkjs.content.reflector;

import dev.latvian.mods.rhino.JavaMembers;

public class FieldExtraInfo extends JavaMembers.FieldInfo {
    public boolean hidden;

    public FieldExtraInfo(JavaMembers.FieldInfo info, boolean hidden) {
        super(info.field);
        this.name = info.name;
        this.hidden = hidden;
    }
}
