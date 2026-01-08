package dev.toapuro.advancedkjs.api.compat;

import dev.toapuro.advancedkjs.content.classjs.ClassJSCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CompatRegistry {

    public static final CompatRegistry INSTANCE = new CompatRegistry();

    private final List<ICompat> compats = new ArrayList<>();

    public CompatRegistry() {
        registerCompats(compats::add);
    }

    public void registerCompats(Consumer<ICompat> register) {
        register.accept(new ClassJSCompat());
    }

    public List<ICompat> getCompats() {
        return compats;
    }
}
