/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * LICENSE file that was distributed with this source code.
 */

package org.kitodo.mediaserver.core.processors;

import java.util.HashMap;
import java.util.Map;

/**
 * Comparison operator representation.
 */
public enum Operator {
    LESS("<"),
    LESS_OR_EQUAL("<="),
    GREATER(">"),
    GREATER_OR_EQUAL(">="),
    EQUAL("="),
    NOT_EQUAL("!="),
    CONTAINS(":"),
    NOT_CONTAINS("!:")
    ;

    private final String operator;

    private static final Map<String, Operator> reverseLookup = new HashMap<>();

    static {
        for (Operator operator : Operator.values()) {
            reverseLookup.put(operator.toString(), operator);
        }
    }

    Operator(String operator) {
        this.operator = operator;
    }

    public static Operator get(String operator) {
        return reverseLookup.get(operator);
    }

    @Override
    public String toString() {
        return operator;
    }
}
