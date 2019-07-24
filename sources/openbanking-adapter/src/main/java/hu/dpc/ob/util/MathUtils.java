/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MathUtils {

    public final static MathContext MATHCONTEXT  = new MathContext(23, RoundingMode.HALF_EVEN);

    public static Long nullToZero(Long value) {
        return nullToDefault(value, 0L);
    }

    public static Long nullToDefault(Long value, Long def) {
        return value == null ? def : value;
    }

    public static Long zeroToNull(Long value) {
        return isEmpty(value) ? null : value;
    }

    /** @return parameter value or ZERO if it is negative */
    public static Long negativeToZero(Long value) {
        return isGreaterThanZero(value) ? value : 0L;
    }

    public static boolean isEmpty(Long value) {
        return value == null || value.equals(0L);
    }

    public static boolean isGreaterThanZero(Long value) {
        return value != null && value > 0L;
    }

    public static boolean isLessThanZero(Long value) {
        return value != null && value < 0L;
    }

    public static boolean isZero(Long value) {
        return value != null && value.equals(0L);
    }

    public static boolean isEqualTo(Long first, Long second) {
        return nullToZero(first).equals(nullToZero(second));
    }

    public static boolean isGreaterThan(Long first, Long second) {
        return nullToZero(first) > nullToZero(second);
    }

    public static boolean isLessThan(Long first, Long second) {
        return nullToZero(first) < nullToZero(second);
    }

    public static boolean isGreaterThanOrEqualTo(Long first, Long second) {
        return nullToZero(first) >= nullToZero(second);
    }

    public static boolean isLessThanOrEqualZero(Long value) {
        return nullToZero(value) <= 0L;
    }

    /** @return parameter value or negated value to positive */
    public static Long abs(Long value) {
        return value == null ? 0L : Math.abs(value);
    }

    /** @return calculates minimum of the two values considering null values
     * @param notNull if true then null parameter is omitted, otherwise returns null */
    public static Long min(Long first, Long second, boolean notNull) {
        return first == null
                ? (notNull ? second : null)
                : second == null ? (notNull ? first : null) : Math.min(first, second);
    }

    /** @return calculates minimum of the values considering null values
     * @param notNull if true then null parameter is omitted, otherwise returns null */
    public static Long min(Long first, Long second, Long third, boolean notNull) {
        return min(min(first, second, notNull), third, notNull);
    }

    /** @return sum the two values considering null values */
    public static Long add(Long first, Long second) {
        return first == null
                ? second
                : second == null ? first : Math.addExact(first, second);
    }

    /** @return sum the values considering null values */
    public static Long add(Long first, Long second, Long third) {
        return add(add(first, second), third);
    }

    /** @return sum the values considering null values */
    public static Long add(Long first, Long second, Long third, Long fourth) {
        return add(add(add(first, second), third), fourth);
    }

    /** @return sum the values considering null values */
    public static Long add(Long first, Long second, Long third, Long fourth, Long fifth) {
        return add(add(add(add(first, second), third), fourth), fifth);
    }

    /** @return first minus second considering null values, maybe negative */
    public static Long subtract(Long first, Long second) {
        return first == null
                ? null
                : second == null ? first : Math.subtractExact(first, second);
    }

    /** @return first minus the others considering null values, maybe negative */
    public static Long subtractToZero(Long first, Long second, Long third) {
        return subtractToZero(subtract(first, second), third);
    }

    /** @return first minus the others considering null values, maybe negative */
    public static Long subtractToZero(Long first, Long second, Long third, Long fourth) {
        return subtractToZero(subtract(subtract(first, second), third), fourth);
    }

    /** @return NONE negative first minus second considering null values */
    public static Long subtractToZero(Long first, Long second) {
        return negativeToZero(subtract(first, second));
    }

    /** @return BigDecimal null safe negate */
    public static Long negate(Long amount) {
        return isEmpty(amount) ? amount : Math.negateExact(amount);
    }


    // ----------------- BigDecimal -----------------

    public static BigDecimal nullToZero(BigDecimal value) {
        return nullToDefault(value, BigDecimal.ZERO);
    }

    public static BigDecimal nullToDefault(BigDecimal value, BigDecimal def) {
        return value == null ? def : value;
    }

    public static BigDecimal zeroToNull(BigDecimal value) {
        return isEmpty(value) ? null : value;
    }

    /** @return parameter value or ZERO if it is negative */
    public static BigDecimal negativeToZero(BigDecimal value) {
        return isGreaterThanZero(value) ? value : BigDecimal.ZERO;
    }

    public static boolean isEmpty(BigDecimal value) {
        return value == null || BigDecimal.ZERO.compareTo(value) == 0;
    }

    public static boolean isGreaterThanZero(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isLessThanZero(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) < 0;
    }

    public static boolean isZero(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) == 0;
    }

    public static boolean isEqualTo(BigDecimal first, BigDecimal second) {
        return nullToZero(first).compareTo(nullToZero(second)) == 0;
    }

    public static boolean isGreaterThan(BigDecimal first, BigDecimal second) {
        return nullToZero(first).compareTo(nullToZero(second)) > 0;
    }

    public static boolean isLessThan(BigDecimal first, BigDecimal second) {
        return nullToZero(first).compareTo(nullToZero(second)) < 0;
    }

    public static boolean isGreaterThanOrEqualTo(BigDecimal first, BigDecimal second) {
        return nullToZero(first).compareTo(nullToZero(second)) >= 0;
    }

    public static boolean isLessThanOrEqualZero(BigDecimal value) {
        return nullToZero(value).compareTo(BigDecimal.ZERO) <= 0;
    }

    /** @return parameter value or negated value to positive */
    public static BigDecimal abs(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.abs();
    }

    /** @return calculates minimum of the two values considering null values
     * @param notNull if true then null parameter is omitted, otherwise returns null */
    public static BigDecimal min(BigDecimal first, BigDecimal second, boolean notNull) {
        return notNull
                ? first == null
                ? second
                : second == null ? first : min(first, second, false)
                : isLessThan(first, second) ? first : second;
    }

    /** @return calculates minimum of the values considering null values
     * @param notNull if true then null parameter is omitted, otherwise returns null */
    public static BigDecimal min(BigDecimal first, BigDecimal second, BigDecimal third, boolean notNull) {
        return min(min(first, second, notNull), third, notNull);
    }

    /** @return sum the two values considering null values */
    public static BigDecimal add(BigDecimal first, BigDecimal second, MathContext mc) {
        return first == null
                ? second
                : second == null ? first : first.add(second, mc);
    }

    /** @return sum the values considering null values */
    public static BigDecimal add(BigDecimal first, BigDecimal second, BigDecimal third, MathContext mc) {
        return add(add(first, second, mc), third, mc);
    }

    /** @return sum the values considering null values */
    public static BigDecimal add(BigDecimal first, BigDecimal second, BigDecimal third, BigDecimal fourth, MathContext mc) {
        return add(add(add(first, second, mc), third, mc), fourth, mc);
    }

    /** @return sum the values considering null values */
    public static BigDecimal add(BigDecimal first, BigDecimal second, BigDecimal third, BigDecimal fourth, BigDecimal fifth, MathContext mc) {
        return add(add(add(add(first, second, mc), third, mc), fourth, mc), fifth, mc);
    }

    /** @return first minus second considering null values, maybe negative */
    public static BigDecimal subtract(BigDecimal first, BigDecimal second, MathContext mc) {
        return first == null
                ? null
                : second == null ? first : first.subtract(second, mc);
    }

    /** @return BigDecimal null safe negate */
    public static BigDecimal negate(BigDecimal amount, MathContext mc) {
        return isEmpty(amount) ? amount : amount.negate(mc);
    }
}
