/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.config;

import hu.dpc.ob.model.internal.ApiSchema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public abstract class SchemaSettings<_H extends Header, _O extends Operation, _B extends Binding> implements ApplicationSettings {

    @Transient
    private AdapterSettings adapterSettings;

    private boolean corsEnabled;

    private List<SchemaProperties<_H, _O, _B>> schemas = new ArrayList<>(2);

    public SchemaSettings(AdapterSettings adapterSettings) {
        this.adapterSettings = adapterSettings;
    }

    public SchemaProperties<_H, _O, _B> getSchema(ApiSchema schema) {
        return schema == null ? null : getSchema(schema.getId());
    }

    public SchemaProperties<_H, _O, _B> getSchema(String schema) {
        if (schema == null)
            return null;
        for (SchemaProperties<_H, _O, _B> schemaProps : getSchemas()) {
            if (schema.equals(schemaProps.getName()))
                return schemaProps;
        }
        return null;
    }

    protected abstract _H[] getHeaders();

    public _H[] getHeaders(ApiSchema schema) {
        return (_H[]) Arrays.stream(getHeaders()).filter(h -> getHeader(schema, h) != null).toArray(Header[]::new);
    }

    public HeaderProperties getHeader(ApiSchema schema, @NotNull _H header) {
        return getSchema(schema).getHeader(header);
    }

    public HeaderProperties getHeader(String schema, String header) {
        return getSchema(schema).getHeader(header);
    }

    protected abstract _B[] getBindings();

    public _B[] getBindings(ApiSchema schema) {
        return (_B[]) Arrays.stream(getBindings()).filter(b -> getBinding(schema, b) != null).toArray(Binding[]::new);
    }

    public BindingProperties getBinding(ApiSchema schema, @NotNull _B binding) {
        return getSchema(schema).getBinding(binding);
    }

    public BindingProperties getBinding(String schema, String binding) {
        return getSchema(schema).getBinding(binding);
    }

    public TenantProperties getBinding(ApiSchema schema, @NotNull _B binding, String tenant) {
        return getSchema(schema).getBinding(binding, tenant);
    }

    public TenantProperties getBinding(String schema, String binding, String tenant) {
        return getSchema(schema).getBinding(binding, tenant);
    }

    protected abstract _O[] getOperations();

    public _O[] getOperations(ApiSchema schema) {
        return (_O[]) Arrays.stream(getOperations()).filter(o -> getOperation(schema, o) != null).toArray(Operation[]::new);
    }

    public OperationProperties getOperation(ApiSchema schema, @NotNull _O operation) {
        return getSchema(schema).getOperation(operation);
    }

    public OperationProperties getOperation(String schema, String operation) {
        return getSchema(schema).getOperation(operation);
    }

    public TenantProperties getOperation(ApiSchema schema, @NotNull _O operation, String tenant) {
        return getSchema(schema).getOperation(operation, tenant);
    }

    public TenantProperties getOperation(String schema, String operation, String tenant) {
        return getSchema(schema).getOperation(operation, tenant);
    }

    private SchemaProperties<_H, _O, _B> addSchema(SchemaProperties<_H, _O, _B> schema) {
        getSchemas().add(schema);
        return schema;
    }

    private SchemaProperties<_H, _O, _B> removeSchema(SchemaProperties<_H, _O, _B> schema) {
        getSchemas().remove(schema);
        return schema;
    }

    void postConstruct() {
        SchemaProperties<_H, _O, _B> defaultSchema = getSchema(SCHEMA_DEFAULT_SETTINGS);
        for (ApiSchema schema : adapterSettings.getSchemas()) {
            SchemaProperties<_H, _O, _B> schemaProps = getSchema(schema);
            if (schemaProps == null) {
                if (defaultSchema == null) {
                    throw new InvalidConfigurationPropertyValueException("schema", SCHEMA_DEFAULT_SETTINGS, "Configuration is missing on " + getClass().getSimpleName());
                }
                schemaProps = addSchema(new SchemaProperties<_H, _O, _B>(schema));
            }
            if (schemaProps.isDefault())
                continue;

            if  (defaultSchema != null) {
                if (defaultSchema.getOperations() != null) {
                    for (OperationProperties defaultOps : defaultSchema.getOperations()) {
                        if (defaultOps.isDefault())
                            continue;
                        if (schemaProps.getOperation(defaultOps.getName()) == null)
                            schemaProps.addOperation(new OperationProperties(defaultOps.getName()));
                    }
                }

                if (defaultSchema.getBindings() != null) {
                    for (BindingProperties bindingProps : defaultSchema.getBindings()) {
                        if (defaultSchema.isDefault())
                            continue;
                        if (schemaProps.getBinding(defaultSchema.getName()) == null)
                            schemaProps.addBinding(new BindingProperties(defaultSchema.getName()));
                    }
                }
            }
            schemaProps.postConstruct(adapterSettings);
            schemaProps.postConstruct(defaultSchema);
        }

        removeSchema(defaultSchema);
    }
}
