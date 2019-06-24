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
import org.apache.logging.log4j.util.Strings;

import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class BaseUriProperties extends ListProperties {

    private String user;
    private String password;
    @NotNull
    private String host;
    private String port;
    private String base;
    @NotNull
    private String path;

    protected BaseUriProperties(String name) {
        super(name);
    }

    public String getUrl() {
        return host + (Strings.isEmpty(port) ? "" : (':' + port)) + getUriPath();
    }

    public String getUriPath() {
        return (Strings.isEmpty(base) ? "" : ((base.charAt(0) != '/' ? '/' : "") + base))
                + ((path.charAt(0) != '/' ? '/' : "") + path);
    }

    void postConstruct(BaseUriProperties parentProps) {
        if (parentProps == null || parentProps == this)
            return;

        // empty is a valid value
        if (user == null)
            user = parentProps.getUser();
        if (password == null)
            password = parentProps.getPassword();
        if (host == null)
            host = parentProps.getHost();
        if (port == null)
            port = parentProps.getPort();
        if (base == null)
            base = parentProps.getBase();
        if (path == null)
            path = parentProps.getPath();
    }
}
