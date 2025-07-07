package dev.toapuro.advancedkjs.claasgen.annotation;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.ScriptableObject;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.*;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class AnnotationHelper {
    @Nullable
    public static MemberValue getMemberValue(Object object, ConstPool cp) {
        if(object == null) return null;

        if(object instanceof Integer value) return new IntegerMemberValue(cp, value);
        else if(object instanceof Double value) return new DoubleMemberValue(value, cp);
        else if(object instanceof Float value) return new FloatMemberValue(value, cp);
        else if(object instanceof Boolean value) return new BooleanMemberValue(value, cp);
        else if(object instanceof Enum<?> value) return new EnumMemberValue(cp.addUtf8Info(value.getDeclaringClass().getName()), cp.addUtf8Info(value.name()), cp);
        else if(object instanceof Short value) return new ShortMemberValue(value, cp);
        else if(object instanceof Byte value) return new ByteMemberValue(value, cp);
        else if(object instanceof Character value) return new CharMemberValue(value, cp);
        else if(object instanceof Long value) return new LongMemberValue(value, cp);
        else if(object instanceof Annotation value) return new AnnotationMemberValue(value, cp);
        else if(object instanceof String value) return new StringMemberValue(value, cp);
        else if(object instanceof Class<?> value) return new ClassMemberValue(value.getName(), cp);

        if (object instanceof Object[] objectArray) {
            ArrayMemberValue arrayMemberValue = new ArrayMemberValue(cp);
            MemberValue[] memberValues = Arrays.stream(objectArray)
                    .map(obj -> getMemberValue(obj, cp))
                    .toArray(MemberValue[]::new);
            arrayMemberValue.setValue(memberValues);
            return arrayMemberValue;
        }

        return null;
    }

    public static Annotation fromScriptable(String annotationName, Scriptable annotationParams, Context context, ConstPool constPool) {
        Annotation annotation = new Annotation(annotationName, constPool);
        List<String> propKeys = Arrays.stream(ScriptableObject.getPropertyIds(context, annotationParams))
                .map(Object::toString).toList();
        for (String propKey : propKeys) {
            Object property = ScriptableObject.getProperty(annotationParams, propKey, context);
            MemberValue memberValue = getMemberValue(property, constPool);
            annotation.addMemberValue(propKey, memberValue);
        }
        return annotation;
    }
}
