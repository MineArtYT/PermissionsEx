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

package ca.stellardrift.permissionsex.sponge;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of a permission description
 */
class PEXPermissionDescription implements PermissionDescription {
    private final PermissionsExPlugin plugin;
    private final String permId;
    private final Optional<Text> description;
    private final Optional<PluginContainer> owner;

    public PEXPermissionDescription(PermissionsExPlugin plugin, String permId, Text description, PluginContainer owner) {
        this.plugin = plugin;
        this.permId = permId;
        this.description = Optional.ofNullable(description);
        this.owner = Optional.ofNullable(owner);
    }

    @Override
    public String getId() {
        return this.permId;
    }

    @Override
    public Optional<Text> getDescription() {
        return this.description;
    }

    @Override
    public CompletableFuture<Map<SubjectReference, Boolean>> findAssignedSubjects(String type) {
        return plugin.loadCollection(type).thenCompose(coll -> coll.getAllWithPermission(getId()));
    }

    @Override
    public Map<Subject, Boolean> getAssignedSubjects(String collectionIdentifier) {
        return plugin.getCollection(collectionIdentifier).map(coll -> coll.getLoadedWithPermission(getId())).orElseGet(ImmutableMap::of);
    }

    @Override
    public Optional<PluginContainer> getOwner() {
        return this.owner;
    }

    static class Builder implements PermissionDescription.Builder {
        private final PluginContainer owner;
        private final PermissionsExPlugin plugin;
        private String id;
        private Text description;
        private Map<String, Integer> ranks = new HashMap<>();

        Builder(PluginContainer owner, PermissionsExPlugin plugin) {
            this.owner = owner;
            this.plugin = plugin;
        }

        @Override
        public Builder id(String id) {
            Preconditions.checkNotNull(id, "id");
            this.id = id;
            return this;
        }

        @Override
        public Builder description(Text text) {
            Preconditions.checkNotNull(text, "text");
            this.description = text;
            return this;
        }

        @Override
        public Builder assign(String s, boolean b) {
            return assign(s, b ? 1 : -1);
        }

        public Builder assign(String rankTemplate, int power) {
            ranks.put(rankTemplate, power);
            return this;
        }

        @Override
        public PEXPermissionDescription register() throws IllegalStateException {
            Preconditions.checkNotNull(id, "id");
            Preconditions.checkNotNull(description, "description");

            final PEXPermissionDescription ret = new PEXPermissionDescription(plugin, this.id, this.description, this.owner);
            this.plugin.registerDescription(ret, ranks);
            return ret;
        }
    }

}
