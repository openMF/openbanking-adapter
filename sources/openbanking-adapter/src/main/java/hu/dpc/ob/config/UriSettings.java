/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@SuppressWarnings("unused")
public abstract class UriSettings<_H extends Header, _O extends Operation, _B extends Binding> implements ApplicationSettings {

    private List<HeaderProperties> headers;
    private List<OperationProperties> operations;
    private List<BindingProperties> bindings;

    public HeaderProperties getHeader(String header) {
        if (header == null)
            return null;
        for (HeaderProperties headerProps : getHeaders()) {
            if (header.equals(headerProps.getName()))
                return headerProps;
        }
        return null;
    }

    public HeaderProperties getHeader(@NotNull _H header) {
        return getHeader(header.getConfigName());
    }

    private HeaderProperties addHeader(HeaderProperties header) {
        if (getHeaders() == null)
            headers = new ArrayList<>(1);
        headers.add(header);
        return header;
    }

    public OperationProperties getOperation(@NotNull _O operation) {
        return getOperation(operation.getConfigName());
    }

    protected OperationProperties getOperation(String operation) {
        if (operation == null)
            return null;
        for (OperationProperties operationProps : getOperations()) {
            if (operation.equals(operationProps.getName()))
                return operationProps;
        }
        return null;
    }

    public TenantProperties getOperation(@NotNull _O operation, String tenant) {
        return getOperation(operation.getConfigName(), tenant);
    }

    protected TenantProperties getOperation(String operation, String tenant) {
        if (operation == null)
            return null;
        for (OperationProperties operationProps : getOperations()) {
            if (operation.equals(operationProps.getName()))
                return operationProps.getTenant(tenant);
        }
        return null;
    }

    public BindingProperties getBinding(@NotNull _B binding) {
        return getBinding(binding.getConfigName());
    }

    protected BindingProperties getBinding(String binding) {
        if (binding == null)
            return null;
        for (BindingProperties bindingProps : getBindings()) {
            if (binding.equals(bindingProps.getName()))
                return bindingProps;
        }
        return null;
    }

    public TenantProperties getBinding(@NotNull _B binding, String tenant) {
        return getBinding(binding.getConfigName(), tenant);
    }

    protected TenantProperties getBinding(String binding, String tenant) {
        if (binding == null)
            return null;
        for (BindingProperties bindingProps : getBindings()) {
            if (binding.equals(bindingProps.getName()))
                return bindingProps.getTenant(tenant);
        }
        return null;
    }

    private OperationProperties addOperation(OperationProperties operation) {
        if (getOperations() == null) {
            operations = new ArrayList<>(1);
        }
        operations.add(operation);
        return operation;
    }

    private OperationProperties removeOperation(OperationProperties operation) {
        if (getOperations() != null) {
            operations.remove(operation);
        }
        return operation;
    }

    private BindingProperties addBinding(BindingProperties binding) {
        if (getBindings() == null) {
            bindings = new ArrayList<>(1);
        }
        bindings.add(binding);
        return binding;
    }

    private BindingProperties removeBinding(BindingProperties binding) {
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
                for (String tenant : adapterSettings.getTenants()) {
                    if (operation.getTenant(tenant) == null)
                        operation.addTenant(new TenantProperties(tenant));
                }
            }
        }

        if (bindings == null)
            bindings = new ArrayList<>(0);
        else {
            for (BindingProperties binding : bindings) {
                if (binding.isDefault())
                    continue;
                for (String tenant : adapterSettings.getTenants()) {
                    if (binding.getTenant(tenant) == null)
                        binding.addTenant(new TenantProperties(tenant));
                }
            }
        }
    }
    
    protected void postConstruct(UriSettings defaultSettings) {
        if (defaultSettings != null && defaultSettings != this) {
            List<HeaderProperties> defaultHeaders = defaultSettings.getHeaders();
            if (defaultHeaders != null) {
                for (HeaderProperties defaultHeader : defaultHeaders) {
                    HeaderProperties header = getHeader(defaultHeader.getName());
                    if (header == null) {
                        header = addHeader(new HeaderProperties(defaultHeader.getName()));
                    }
                    header.postConstruct(defaultHeader);
                }
            }
            List<OperationProperties> defaultOps = defaultSettings.getOperations();
            if (defaultOps != null) {
                for (OperationProperties defaultOp : defaultOps) {
                    OperationProperties operation = getOperation(defaultOp.getName());
                    if (operation == null) {
                        operation = addOperation(new OperationProperties(defaultOp.getName()));
                    }
                    operation.postConstruct(defaultOp);
                }
            }
            List<BindingProperties> defaultBindings = defaultSettings.getBindings();
            if (defaultBindings != null) {
                for (BindingProperties defaultBinding : defaultBindings) {
                    BindingProperties binding = getBinding(defaultBinding.getName());
                    if (binding == null) {
                        binding = addBinding(new BindingProperties(defaultBinding.getName()));
                    }
                    binding.postConstruct(defaultBinding);
                }
            }
        }
        if (operations == null)
            operations = new ArrayList<>(0);
        else {
            OperationProperties defaultOperation = getOperation(OPERATION_DEFAULT_SETTINGS);
            for (OperationProperties operation : operations) {
                if (operation.isDefault())
                    continue;
                operation.postConstruct(defaultOperation);
            }
            removeOperation(defaultOperation);
        }

        if (bindings == null)
            bindings = new ArrayList<>(0);
        else {
            BindingProperties defaultBinding = getBinding(BINDING_DEFAULT_SETTINGS);
            for (BindingProperties binding : bindings) {
                if (binding.isDefault())
                    continue;
                binding.postConstruct(defaultBinding);
            }
            removeBinding(defaultBinding);
        }
    }
}
