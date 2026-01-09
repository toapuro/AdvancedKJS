package dev.toapuro.advancedkjs.content.typejs.bundle;

import java.util.List;

public record BundleSource(
        SourceBundleRuntime wrapper,
        String name,
        List<String> lines
) {
}
