//
// Created by juicefries
// The project name is fct
// Data 2026/11/11 02:36
//

package io.github.juicefries.fct.event;

import io.github.juicefries.fct.sign.CopyFailedException;
import io.github.juicefries.fct.sign.Copyable;
import io.github.juicefries.fct.sign.Readonly;
import io.github.juicefries.fct.util.Lock;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Experimental
public abstract class EventObject implements Copyable,Readonly {

    private final static Lock ETL = Lock.create("EventObjectTypeLock");
    private final static Set<String> types = ConcurrentHashMap.newKeySet();

    @Contract(pure = true)
    protected EventObject() {

    }

    public abstract EventObject copy();
    public abstract String type();

    @Override
    @SuppressWarnings("deprecation")
    @Deprecated(since = "1.0.7.2",forRemoval = true)
    public void copy(Copyable template) throws CopyFailedException {
        throw new CopyFailedException("This class does not allow this type of operation.");
    }

    @Contract("null -> fail")
    protected static @NotNull String registerEventType(String type) {
        if (type == null) {
            throw new NullPointerException("type is null!");
        }
        if (type.isBlank()) {
            throw new IllegalArgumentException("The event type cannot be empty or a blank character!");
        }
        synchronized (ETL) {
            var val = "Event-%s".formatted(type);
            if (types.contains(val)) {
                throw new IllegalArgumentException("This event type already exists! type:%s.".formatted(val));
            }
            types.add(val);
            return val;
        }
    }

    public static String[] getEventTypes() {
        synchronized (ETL) {
            return types.toArray(String[]::new);
        }
    }

}
