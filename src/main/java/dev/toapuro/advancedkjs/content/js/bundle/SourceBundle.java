package dev.toapuro.advancedkjs.content.js.bundle;

import java.util.List;

public record SourceBundle(
        SourceBundleRuntime wrapper,
        String name,
        List<String> lines
) {
}
