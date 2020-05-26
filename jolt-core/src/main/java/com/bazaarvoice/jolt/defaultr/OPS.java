/*
 * Copyright 2013 Bazaarvoice, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bazaarvoice.jolt.defaultr;

import com.bazaarvoice.jolt.Defaultr;
import com.bazaarvoice.jolt.exception.SpecException;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public enum OPS {

    STAR, OR, LITERAL, LAST;

    public static OPS parse( String key ) {
        if ( key.contains( Defaultr.WildCards.STAR ) ){

            if ( ! Defaultr.WildCards.STAR.equals( key ) ) {
                throw new SpecException("Defaultr key " + key + " is invalid.  * keys can only contain *, and no other characters." );
            }

            return STAR;
        }
        if ( key.contains( Defaultr.WildCards.OR ) ) {
            return OR;
        }
        if ( key.contains( Defaultr.WildCards.LAST ) ){

            if ( ! Defaultr.WildCards.LAST.equals( key ) ) {
                throw new SpecException("Defaultr key " + key + " is invalid.  * keys can only contain !, and no other characters." );
            }

            return LAST;
        }
        return LITERAL;
    }

    public static class OpsPrecedenceComparator implements Comparator<OPS> {
        /**
         * The order we want to apply Defaultr logic is Literals, Or, and then Star.
         * Since we walk the sorted data from 0 to n, that means Literals need to low, and Star should be high.
         */
        @Override
        public int compare(OPS ops, OPS ops1) {

            Map<OPS,Integer> opsMap = new HashMap<OPS, Integer>();

            opsMap.put(LITERAL, 1);
            opsMap.put(OR, 2);
            opsMap.put(LAST, 3);
            opsMap.put(STAR, 4);

            // a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
            // s < s1 -> -1
            // s = s1 -> 0
            // s > s1 -> 1

            if (opsMap.get(ops).equals(null) || opsMap.get(ops1).equals(null)) throw new IllegalStateException( "Someone has added an op type without changing this method." );

            return opsMap.get(ops).compareTo(opsMap.get(ops1));
        }
    }
}
