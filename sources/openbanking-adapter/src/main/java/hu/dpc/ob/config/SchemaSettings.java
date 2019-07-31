/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.config;

import hu.dpc.ob.config.type.ApplicationSettings;
import hu.dpc.ob.config.type.Binding;
import hu.dpc.ob.config.type.Header;
import hu.dpc.ob.config.type.Operation;
import hu.dpc.ob.model.internal.ApiSchema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public abstract class SchemaSettings<_H extends Header, _O extends Operation, _B extends Binding> implements ApplicationSettings {

    @Transient
    private AdapterSettings adapterSettings;

    private boolean corsEnabled;

    private Map<String, SchemaProperties<_H, _O, _B>> schemas = new HashMap<>();

    public SchemaSettings(AdapterSettings adapterSettings) {
        this.adapterSettings = adapterSettings;
    }

    public SchemaProperties<_H, _O, _B> getSchema(ApiSchema schema) {
        return schema == null ? null : getSchema(schema.getId());
    }

    public SchemaProperties<_H, _O, _B> getSchema(String schema) {
        return getSchemas().get(schema);
    }

    protected abstract _H[] getHeaders();

    public _H[] getHeaders(ApiSchema schema) {
        return (_H[]) Arrays.stream(getHeaders()).filter(h -> getHeaderProps(schema, h) != null).toArray(Header[]::new);
    }

    public HeaderProperties getHeaderProps(ApiSchema schema, @NotNull _H header) {
        return getSchema(schema).getHeaderProps(header);
    }

    public HeaderProperties getHeaderProps(String schema, String header) {
        return getSchema(schema).getHeaderProps(header);
    }

    protected abstract _B[] getBindings();

    public _B[] getBindings(ApiSchema schema) {
        return (_B[]) Arrays.stream(getBindings()).filter(b -> getBindingProps(schema, b) != null).toArray(Binding[]::new);
    }

    public BindingProperties getBindingProps(ApiSchema schema, @NotNull _B binding) {
        return getSchema(schema).getBindingProps(binding);
    }

    public BindingProperties getBindingProps(String schema, String binding) {
        return getSchema(schema).getBindingProps(binding);
    }

    public TenantProperties getBindingProps(ApiSchema schema, @NotNull _B binding, String tenant) {
        return getSchema(schema).getBindingProps(binding, tenant);
    }

    public TenantProperties getBindingProps(String schema, String binding, String tenant) {
        return getSchema(schema).getBindingProps(binding, tenant);
    }

    protected abstract _O[] getOperations();

    public _O[] getOperations(ApiSchema schema) {
        return (_O[]) Arrays.stream(getOperations()).filter(o -> getOperationProps(schema, o) != null).toArray(Operation[]::new);
    }

    public OperationProperties getOperationProps(ApiSchema schema, @NotNull _O operation) {
        return getSchema(schema).getOperationProps(operation);
    }

    public OperationProperties getOperationProps(String schema, String operation) {
        return getSchema(schema).getOperationProps(operation);
    }

    public TenantProperties getOperationProps(ApiSchema schema, @NotNull _O operation, String tenant) {
        return getSchema(schema).getOperationProps(operation, tenant);
    }

    public TenantProperties getOperationProps(String schema, String operation, String tenant) {
        return getSchema(schema).getOperationProps(operation, tenant);
    }

    private SchemaProperties<_H, _O, _B> addSchemaProps(ApiSchema schema, SchemaProperties<_H, _O, _B> properties) {
        getSchemas().put(schema.getId(), properties);
        return properties;
    }

    private void removeSchemaProps(String schema) {
        getSchemas().remove(schema);
    }

    void postConstruct() {
        String defaultSchemaName = SchemaProperties.getDefaultName();
        SchemaProperties<_H, _O, _B> defaultSchema = getSchema(defaultSchemaName);

        for (ApiSchema schema : adapterSettings.getSchemas()) {
            SchemaProperties<_H, _O, _B> schemaProps = getSchema(schema);
            if (schemaProps == null) {
                if (defaultSchema == null) {
                    throw new InvalidConfigurationPropertyValueException("schema", defaultSchemaName, "Configuration is missing on " + getClass().getSimpleName());
                }
                schemaProps = addSchemaProps(schema, new SchemaProperties<_H, _O, _B>());
            }

            if  (defaultSchema != null) {
                if (defaultSchema.getOperations() != null) {
                    for (OperationProperties defaultOps : defaultSchema.getOperations()) {
                        if (defaultOps.isDefault())
                            continue;
                        if (schemaProps.getOperationProps(defaultOps.getName()) == null)
                            schemaProps.addOperationProps(new OperationProperties(defaultOps.getName()));
                    }
                }

                if (defaultSchema.getBindings() != null) {
                    for (BindingProperties defaultBindings : defaultSchema.getBindings()) {
                        if (defaultBindings.isDefault())
                            continue;
                        if (schemaProps.getBindingProps(defaultBindings.getName()) == null)
                            schemaProps.addBindingProps(new BindingProperties(defaultBindings.getName()));
                    }
                }
            }
            schemaProps.postConstruct(adapterSettings);
            schemaProps.postConstruct(defaultSchema);
        }

        removeSchemaProps(defaultSchemaName);
    }
}
