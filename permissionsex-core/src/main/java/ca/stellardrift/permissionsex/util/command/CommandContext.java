/*
 * PermissionsEx
 * Copyright (C) zml and PermissionsEx contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.stellardrift.permissionsex.util.command;

import ca.stellardrift.permissionsex.commands.commander.Commander;
import ca.stellardrift.permissionsex.util.TranslatableProvider;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;

/**
 * Context that a command is executed in
 */
public class CommandContext {
    private final CommandSpec spec;
    private final String rawInput;
    private final Multimap<String, Object> parsedArgs;

    public CommandContext(CommandSpec spec, String rawInput) {
        this.spec = spec;
        this.rawInput = rawInput;
        this.parsedArgs = ArrayListMultimap.create();
    }

    public String getRawInput() {
        return this.rawInput;
    }

    public CommandSpec getSpec() {
        return spec;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> Collection<T> getAll(String key) {
        return (Collection) parsedArgs.get(key);
    }

    public <T> Collection<T> getAll(TranslatableProvider key) {
        return getAll(key.getKey());
    }

    @SuppressWarnings("unchecked")
    public <T> T getOne(String key) {
        Collection<Object> values = parsedArgs.get(key);
        if (values.size() != 1) {
            return null;
        } else {
            return (T) values.iterator().next();
        }
    }

    public <T> T getOne(TranslatableProvider key) {
        return getOne(key.getKey());
    }

    public void putArg(String key, Object value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        parsedArgs.put(key, value);
    }

    public void checkPermission(Commander<?> commander, String permission) throws CommandException {
        if (!commander.hasPermission(permission)) {
            throw new CommandException(CommonMessages.ERROR_PERMISSION.get());
        }
    }

    public boolean hasAny(String key) {
        return parsedArgs.containsKey(key);
    }

    public boolean hasAny(TranslatableProvider key) {
        return parsedArgs.containsKey(key.getKey());
    }
}
