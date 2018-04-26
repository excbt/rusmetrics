package ru.excbt.datafuse.nmk.domain.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class KeyEnumTool {

    private static final Logger log = LoggerFactory.getLogger(KeyEnumTool.class);


    private KeyEnumTool() {
    }

    /**
     *
     * @param clazz
     * @param keyStrings
     * @param <T>
     * @return
     */
    public static <T extends Enum<T> & KeynameObject> boolean checkKeys(Class<T> clazz, String ... keyStrings) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(keyStrings);
        boolean result = true;
        Set<T> values = EnumSet.allOf(clazz);
        Set<String> allKeyNames = values.stream().map(i -> i.getKeyname()).collect(Collectors.toSet());
        for (String s: keyStrings) {
            result = result && allKeyNames.contains(s);
        }
        return result;
    }


    /**
     *
     * @param enumType
     * @param keyString
     * @param <T>
     * @return
     */
    public static <T extends Enum<T> & KeynameObject> Optional<T> searchKey(Class<T> enumType, String keyString) {
        Objects.requireNonNull(enumType);
        Set<T> values = EnumSet.allOf(enumType);
        return values.stream().filter(i -> i.getKeyname().equals(keyString)).findFirst();
    }

    /**
     *
     * @param enumType
     * @param keyString
     * @param nullValue
     * @param <T>
     * @return
     */
    public static <T extends Enum<T> & KeynameObject> Optional<T> searchKey(Class<T> enumType, String keyString, T nullValue) {
        Objects.requireNonNull(enumType);
        if (keyString == null) {
            return Optional.ofNullable(nullValue);
        }
        Set<T> values = EnumSet.allOf(enumType);
        return values.stream().filter(i -> keyString.equals(i.getKeyname())).findFirst();
    }

    /**
     *
     * @param enumType
     * @param names
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> boolean checkNames(Class<T> enumType, String ... names) {

        boolean result = true;
        Set<T> values = EnumSet.allOf(enumType);
        Set<String> allKeyNames = values.stream().map(i -> i.name()).collect(Collectors.toSet());
        for (String s: names) {
            result = result && allKeyNames.contains(s);
        }
        return result;
    }

    /**
     *
     * @param enumType
     * @param name
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> Optional<T> searchName(Class<T> enumType, String name) {
        Objects.requireNonNull(enumType);
        if (name == null) {
            return Optional.empty();
        }
        Set<T> values = EnumSet.allOf(enumType);
        return values.stream().filter(i -> name.equals(i.name())).findFirst();
    }



}
