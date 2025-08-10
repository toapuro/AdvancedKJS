package dev.toapuro.advancedkjs.content.claasgen.kubejs.builder;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.rhino.Context;
import dev.toapuro.advancedkjs.content.claasgen.construction.GenAnnotation;
import dev.toapuro.advancedkjs.content.claasgen.construction.GenClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class KubeFieldBuilderJS extends EventJS implements IModifierBuilder<KubeFieldBuilderJS>, IAnnotationBuilder<KubeFieldBuilderJS> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KubeFieldBuilderJS.class);

    private final String fieldName;
    private final GenClass genClass;
    private final Context context;
    private final List<GenAnnotation> annotations;
    private int modifiers;

    private KubeFieldBuilderJS(String fieldName, GenClass genClass, Context context) {
        this.fieldName = fieldName;
        this.genClass = genClass;
        this.context = context;
        this.annotations = new ArrayList<>();
        this.modifiers = 0;
    }

    public static KubeFieldBuilderJS create(String fieldName, GenClass genClass, Context context) {
        return new KubeFieldBuilderJS(fieldName, genClass, context);
    }

    @Override
    public KubeFieldBuilderJS annotation(GenAnnotation annotation) {
        annotations.add(annotation);
        return this;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void applyModifier(int modifier) {
        this.modifiers |= modifier;
    }

    public Result build() {
        return new Result(genClass, fieldName, annotations, modifiers);
    }


    public static class Result {
        private final GenClass genClass;
        private final String fieldName;
        private final List<GenAnnotation> annotations;
        private final int modifiers;

        public Result(GenClass genClass, String fieldName, List<GenAnnotation> annotations, int modifiers) {
            this.genClass = genClass;
            this.fieldName = fieldName;
            this.annotations = annotations;
            this.modifiers = modifiers;
        }

        public GenClass getKubeClass() {
            return genClass;
        }

        public String getFieldName() {
            return fieldName;
        }

        public List<GenAnnotation> getAnnotations() {
            return annotations;
        }

        public int getModifiers() {
            return modifiers;
        }
    }
}
