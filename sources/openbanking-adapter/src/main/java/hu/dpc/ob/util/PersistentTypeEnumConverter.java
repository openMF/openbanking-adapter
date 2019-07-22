/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.util;

import hu.dpc.ob.domain.entity.PersistentType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.AttributeConverter;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.EnumSet;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
//@Converter
public class PersistentTypeEnumConverter<_T extends Enum & PersistentType<_T, _A>, _A extends Serializable> implements AttributeConverter<_T, _A> {

    public _A convertToDatabaseColumn(_T attribute) {
        return attribute == null ? null : attribute.toId();
    }

    public _T convertToEntityAttribute(_A dbData) {
        Class<_T> type = (Class<_T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        EnumSet enumSet = EnumSet.allOf(type);
        for(Object entityType : enumSet){
            Serializable id = ((PersistentType) entityType).toId();
            if (dbData == null ? id == null : dbData.equals(id))
                return (_T) entityType;
        }
        return null; // TODO: exception
    }
}
