/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.apache.mavibot.btree.serializer;


import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * Test the ShortSerializer class
 * 
 * @author <a href="mailto:labs@labs.apache.org">Mavibot labs Project</a>
 */
public class ShortSerializerTest
{
    private static ShortSerializer serializer = new ShortSerializer();


    @Test
    public void testShortSerializer()
    {
        short value = 0x0000;
        byte[] result = serializer.serialize( value );

        assertEquals( ( byte ) 0x00, result[1] );
        assertEquals( ( byte ) 0x00, result[0] );

        assertEquals( value, serializer.deserialize( result ).shortValue() );

        // ------------------------------------------------------------------
        value = 0x0001;
        result = serializer.serialize( value );

        assertEquals( ( byte ) 0x01, result[1] );
        assertEquals( ( byte ) 0x00, result[0] );

        assertEquals( value, serializer.deserialize( result ).shortValue() );

        // ------------------------------------------------------------------
        value = 0x00FF;
        result = serializer.serialize( value );

        assertEquals( ( byte ) 0xFF, result[1] );
        assertEquals( ( byte ) 0x00, result[0] );

        assertEquals( value, serializer.deserialize( result ).shortValue() );

        // ------------------------------------------------------------------
        value = 0x0100;
        result = serializer.serialize( value );

        assertEquals( ( byte ) 0x00, result[1] );
        assertEquals( ( byte ) 0x01, result[0] );

        assertEquals( value, serializer.deserialize( result ).shortValue() );

        // ------------------------------------------------------------------
        value = 0x7FFF;
        result = serializer.serialize( value );

        assertEquals( ( byte ) 0xFF, result[1] );
        assertEquals( ( byte ) 0x7F, result[0] );

        assertEquals( value, serializer.deserialize( result ).shortValue() );

        // ------------------------------------------------------------------
        value = ( short ) 0x8000;
        result = serializer.serialize( value );

        assertEquals( ( byte ) 0x00, result[1] );
        assertEquals( ( byte ) 0x80, result[0] );

        assertEquals( value, serializer.deserialize( result ).shortValue() );

        // ------------------------------------------------------------------
        value = ( short ) 0xFFFF;
        result = serializer.serialize( value );

        assertEquals( ( byte ) 0xFF, result[1] );
        assertEquals( ( byte ) 0xFF, result[0] );

        assertEquals( value, serializer.deserialize( result ).shortValue() );
    }
}
