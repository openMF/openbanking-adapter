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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@SuppressWarnings("unused")
public abstract class UriSettings<_H extends Header, _O extends Operation, _B extends Binding> implements ApplicationSettings {

    @Valid
    private List<HeaderProperties> headers;
    @Valid
    private List<OperationProperties> operations;
    @Valid
    private List<BindingProperties> bindings;

    HeaderProperties getHeaderProps(String header) {
        if (header == null)
            return null;
        if (getHeaders() == null)
            return null;
        for (HeaderProperties headerProps : getHeaders()) {
            if (header.equals(headerProps.getName()))
                return headerProps;
        }
        return null;
    }

    public HeaderProperties getHeaderProps(@NotNull _H header) {
        return getHeaderProps(header.getConfigName());
    }

    private HeaderProperties addHeaderProps(HeaderProperties header) {
        if (getHeaders() == null)
            headers = new ArrayList<>(1);
        headers.add(header);
        return header;
    }

    public OperationProperties getOperationProps(@NotNull _O operation) {
        return getOperationProps(operation.getConfigName());
    }

    OperationProperties getOperationProps(String operation) {
        if (operation == null)
            return null;
        if (getOperations() == null)
            return null;
        for (OperationProperties operationProps : operations) {
            if (operation.equals(operationProps.getName()))
                return operationProps;
        }
        return null;
    }

    public TenantProperties getOperationProps(@NotNull _O operation, String tenant) {
        return getOperationProps(operation.getConfigName(), tenant);
    }

    TenantProperties getOperationProps(String operation, String tenant) {
        if (operation == null)
            return null;
        if (getOperations() == null)
            return null;
        for (OperationProperties operationProps : operations) {
            if (operation.equals(operationProps.getName()))
                return operationProps.getTenantProps(tenant);
        }
        return null;
    }

    OperationProperties addOperationProps(OperationProperties operation) {
        if (getOperations() == null) {
            operations = new ArrayList<>(1);
        }
        operations.add(operation);
        return operation;
    }

    private OperationProperties removeOperationProps(OperationProperties operation) {
        if (getOperations() != null) {
            operations.remove(operation);
        }
        return operation;
    }

    BindingProperties getBindingProps(@NotNull _B binding) {
        return getBindingProps(binding.getConfigName());
    }

    BindingProperties getBindingProps(String binding) {
        if (binding == null)
            return null;
        if (getBindings() == null)
            return null;
        for (BindingProperties bindingProps : bindings) {
            if (binding.equals(bindingProps.getName()))
                return bindingProps;
        }
        return null;
    }

    public TenantProperties getBindingProps(@NotNull _B binding, String tenant) {
        return this.getBindingProps(binding.getConfigName(), tenant);
    }

    TenantProperties getBindingProps(@NotNull String binding, String tenant) {
        if (binding == null)
            return null;
        if (getBindings() == null)
            return null;
        for (BindingProperties bindingProps : bindings) {
            if (binding.equals(bindingProps.getName()))
                return bindingProps.getTenantProps(tenant);
        }
        return null;
    }

    BindingProperties addBindingProps(BindingProperties binding) {
        if (getBindings() == null) {
            bindings = new ArrayList<>(1);
        }
        bindings.add(binding);
        return binding;
    }

    private BindingProperties removeBindingProps(BindingProperties binding) {
        if (getBindings() != null) {
            bindings.remove(binding);
        }
        return binding;
    }

    protected void postConstruct(AdapterSettings adapterSettings) {
        if (adapterSettings == null)
            return;

        if (operations == null)
            operations = new ArrayList<>(0);
        else {
            for (OperationProperties operation : operations) {
                if (operation.isDefault())
                    continue;
                for (TenantConfig tenant : adapterSettings.getTenants()) {
                    @NotEmpty String tenantName = tenant.getName();
                    if (operation.getTenantProps(tenantName) == null)
                        operation.addTenantProps(new TenantProperties(tenantName));
                }
            }
        }

        if (bindings == null)
            bindings = new ArrayList<>(0);
        else {
            for (BindingProperties binding : bindings) {
                if (binding.isDefault())
                    continue;
                for (TenantConfig tenant : adapterSettings.getTenants()) {
                    @NotEmpty String tenantName = tenant.getName();
                    if (binding.getTenantProps(tenantName) == null)
                        binding.addTenantProps(new TenantProperties(tenantName));
                }
            }
        }
    }
    
    protected void postConstruct(UriSettings defaultSettings) {
        if (defaultSettings != null && defaultSettings != this) {
            List<HeaderProperties> defaultHeaders = defaultSettings.getHeaders();
            if (defaultHeaders != null) {
                for (HeaderProperties defaultHeader : defaultHeaders) {
                    HeaderProperties header = getHeaderProps(defaultHeader.getName());
                    if (header == null) {
                        header = addHeaderProps(new HeaderProperties(defaultHeader.getName()));
                    }
                    header.postConstruct(defaultHeader);
                }
            }
            List<OperationProperties> defaultOps = defaultSettings.getOperations();
            if (defaultOps != null) {
                for (OperationProperties defaultOp : defaultOps) {
                    OperationProperties operation = getOperationProps(defaultOp.getName());
                    if (operation == null) {
                        operation = addOperationProps(new OperationProperties(defaultOp.getName()));
                    }
                    operation.postConstruct(defaultOp);
                }
            }
            List<BindingProperties> defaultBindings = defaultSettings.getBindings();
            if (defaultBindings != null) {
                for (BindingProperties defaultBinding : defaultBindings) {
                    BindingProperties binding = getBindingProps(defaultBinding.getName());
                    if (binding == null) {
                        binding = addBindingProps(new BindingProperties(defaultBinding.getName()));
                    }
                    binding.postConstruct(defaultBinding);
                }
            }
        }
        if (operations == null)
            operations = new ArrayList<>(0);
        else {
            OperationProperties defaultOperation = getOperationProps(OPERATION_DEFAULT_SETTINGS);
            for (OperationProperties operation : operations) {
                if (operation.isDefault())
                    continue;
                operation.postConstruct(defaultOperation);
            }
            removeOperationProps(defaultOperation);
        }

        if (bindings == null)
            bindings = new ArrayList<>(0);
        else {
            BindingProperties defaultBinding = getBindingProps(BINDING_DEFAULT_SETTINGS);
            for (BindingProperties binding : bindings) {
                if (binding.isDefault())
                    continue;
                binding.postConstruct(defaultBinding);
            }
            removeBindingProps(defaultBinding);
        }
    }
}
