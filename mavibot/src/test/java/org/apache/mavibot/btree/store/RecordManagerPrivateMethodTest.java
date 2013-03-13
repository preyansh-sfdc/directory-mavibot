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
package org.apache.mavibot.btree.store;


import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;


/**
 * Test some of the RecordManager prvate methods
 * @author <a href="mailto:labs@labs.apache.org">Mavibot labs Project</a>
 */
public class RecordManagerPrivateMethodTest
{
    /**
     * Test the getFreePageIOs method
     */
    @Test
    public void testGetFreePageIos() throws IOException, NoSuchMethodException, InvocationTargetException,
        IllegalAccessException
    {
        File tempFile = File.createTempFile( "mavibot", ".db" );
        String tempFileName = tempFile.getAbsolutePath();
        tempFile.deleteOnExit();

        RecordManager recordManager = new RecordManager( tempFileName, 32 );
        Method getFreePageIOsMethod = RecordManager.class.getDeclaredMethod( "getFreePageIOs", int.class );
        getFreePageIOsMethod.setAccessible( true );

        PageIO[] pages = ( PageIO[] ) getFreePageIOsMethod.invoke( recordManager, 0 );

        assertEquals( 0, pages.length );

        for ( int i = 1; i < 20; i++ )
        {
            pages = ( PageIO[] ) getFreePageIOsMethod.invoke( recordManager, i );
            assertEquals( 1, pages.length );
        }

        for ( int i = 21; i < 44; i++ )
        {
            pages = ( PageIO[] ) getFreePageIOsMethod.invoke( recordManager, i );
            assertEquals( 2, pages.length );
        }

        for ( int i = 45; i < 68; i++ )
        {
            pages = ( PageIO[] ) getFreePageIOsMethod.invoke( recordManager, i );
            assertEquals( 3, pages.length );
        }
    }


    /**
     * Test the ComputeNbPages method
     */
    @Test
    public void testComputeNbPages() throws IOException, SecurityException, NoSuchMethodException,
        IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        File tempFile = File.createTempFile( "mavibot", ".db" );
        String tempFileName = tempFile.getAbsolutePath();
        tempFile.deleteOnExit();

        RecordManager recordManager = new RecordManager( tempFileName, 32 );
        Method computeNbPagesMethod = RecordManager.class.getDeclaredMethod( "computeNbPages", int.class );
        computeNbPagesMethod.setAccessible( true );

        assertEquals( 0, ( ( Integer ) computeNbPagesMethod.invoke( recordManager, 0 ) ).intValue() );

        for ( int i = 1; i < 21; i++ )
        {
            assertEquals( 1, ( ( Integer ) computeNbPagesMethod.invoke( recordManager, i ) ).intValue() );
        }

        for ( int i = 21; i < 45; i++ )
        {
            assertEquals( 2, ( ( Integer ) computeNbPagesMethod.invoke( recordManager, i ) ).intValue() );
        }

        for ( int i = 45; i < 68; i++ )
        {
            assertEquals( 3, ( ( Integer ) computeNbPagesMethod.invoke( recordManager, i ) ).intValue() );
        }
    }
}
