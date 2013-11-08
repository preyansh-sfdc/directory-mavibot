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
package org.apache.directory.mavibot.btree.managed;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.apache.directory.mavibot.btree.Tuple;
import org.apache.directory.mavibot.btree.TupleCursor;
import org.apache.directory.mavibot.btree.exception.BTreeAlreadyManagedException;
import org.apache.directory.mavibot.btree.exception.EndOfFileExceededException;
import org.apache.directory.mavibot.btree.serializer.LongSerializer;
import org.apache.directory.mavibot.btree.serializer.StringSerializer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests the browse methods on a managed BTree
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ManagedBTreeBrowseTest
{
    private BTree<Long, String> btree = null;

    private RecordManager recordManager1 = null;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private File dataDir = null;


    @Before
    public void createBTree()
    {
        dataDir = tempFolder.newFolder( UUID.randomUUID().toString() );

        openRecordManagerAndBtree();

        try
        {
            // Create a new BTree
            btree = recordManager1.addBTree( "test", new LongSerializer(), new StringSerializer(), false );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }


    private void openRecordManagerAndBtree()
    {
        try
        {
            if ( recordManager1 != null )
            {
                recordManager1.close();
            }

            // Now, try to reload the file back
            recordManager1 = new RecordManager( dataDir.getAbsolutePath() );

            // load the last created btree
            if ( btree != null )
            {
                btree = recordManager1.getManagedTree( btree.getName() );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }


    /**
     * Check a next() call
     */
    private void checkNext( TupleCursor<Long, String> cursor, long key, String value, boolean next, boolean prev ) throws EndOfFileExceededException, IOException
    {
        Tuple<Long, String> tuple = cursor.next();
        assertNotNull( tuple );
        assertEquals( key, (long)tuple.getKey() );
        assertEquals( value, tuple.getValue() );
        assertEquals( next, cursor.hasNext() );
        assertEquals( prev, cursor.hasPrev() );
    }


    /**
     * Check a prev() call
     */
    private void checkPrev( TupleCursor<Long, String> cursor, long key, String value, boolean next, boolean prev ) throws EndOfFileExceededException, IOException
    {
        Tuple<Long, String> tuple = cursor.prev();
        assertNotNull( tuple );
        assertEquals( key, (long)tuple.getKey() );
        assertEquals( value, tuple.getValue() );
        assertEquals( next, cursor.hasNext() );
        assertEquals( prev, cursor.hasPrev() );
    }

    
    /**
     * Test the browse methods on an empty btree  
     */
    @Test
    public void testBrowseEmptyBTree() throws IOException, BTreeAlreadyManagedException
    {
        TupleCursor<Long, String> cursor = btree.browse();
        
        assertFalse( cursor.hasNext() );
        assertFalse( cursor.hasPrev() );
        
        try
        {
            cursor.next();
            fail();
        }
        catch ( NoSuchElementException nsee )
        {
            // Expected
        }
        
        try
        {
            cursor.prev();
            fail();
        }
        catch ( NoSuchElementException nsee )
        {
            // Expected
        }
        
        assertEquals( -1L, cursor.getRevision() );
    }


    /**
     * Test the browse methods on a btree containing just a leaf
     */
    @Test
    public void testBrowseBTreeLeafNext() throws IOException, BTreeAlreadyManagedException
    {
        // Inject some data
        btree.insert( 1L, "1" );
        btree.insert( 4L, "4" );
        btree.insert( 2L, "2" );
        btree.insert( 3L, "3" );
        btree.insert( 5L, "5" );

        // Create the cursor
        TupleCursor<Long, String> cursor = btree.browse();
        
        // Move forward
        cursor.beforeFirst();
        
        assertFalse( cursor.hasPrev() );
        assertTrue( cursor.hasNext() );
        
        checkNext( cursor, 1L, "1", true, false );
        checkNext( cursor, 2L, "2", true, true );
        checkNext( cursor, 3L, "3", true, true );
        checkNext( cursor, 4L, "4", true, true );
        checkNext( cursor, 5L, "5", false, true );
    }


    /**
     * Test the browse methods on a btree containing just a leaf
     */
    @Test
    public void testBrowseBTreeLeafPrev() throws IOException, BTreeAlreadyManagedException
    {
        // Inject some data
        btree.insert( 1L, "1" );
        btree.insert( 4L, "4" );
        btree.insert( 2L, "2" );
        btree.insert( 3L, "3" );
        btree.insert( 5L, "5" );

        // Create the cursor
        TupleCursor<Long, String> cursor = btree.browse();
        
        // Move backward
        cursor.afterLast();
        
        checkPrev( cursor, 5L, "5", false, true );
        checkPrev( cursor, 4L, "4", true, true );
        checkPrev( cursor, 3L, "3", true, true );
        checkPrev( cursor, 2L, "2", true, true );
        checkPrev( cursor, 1L, "1", true, false );
    }


    /**
     * Test the browse methods on a btree containing just a leaf and see if we can
     * move at the end or at the beginning
     */
    @Test
    public void testBrowseBTreeLeafFirstLast() throws IOException, BTreeAlreadyManagedException
    {
        // Inject some data
        btree.insert( 1L, "1" );
        btree.insert( 4L, "4" );
        btree.insert( 2L, "2" );
        btree.insert( 3L, "3" );
        btree.insert( 5L, "5" );

        // Create the cursor
        TupleCursor<Long, String> cursor = btree.browse();
        
        // We should not be able to move backward
        try
        {
            cursor.prev();
            fail();
        }
        catch ( NoSuchElementException nsee )
        {
            // Expected
        }

        // Start browsing three elements
        assertFalse( cursor.hasPrev() );
        assertTrue( cursor.hasNext() );
        Tuple<Long, String> tuple = cursor.next();
        tuple = cursor.next();
        tuple = cursor.next();
        
        // We should be at 3 now
        assertTrue( cursor.hasPrev() );
        assertTrue( cursor.hasNext() );
        assertEquals( 3L, (long)tuple.getKey() );
        assertEquals( "3", tuple.getValue() );
        
        // Move to the end
        cursor.afterLast();

        assertTrue( cursor.hasPrev() );
        assertFalse( cursor.hasNext() );

        // We should not be able to move forward
        try
        {
            cursor.next();
            fail();
        }
        catch ( NoSuchElementException nsee )
        {
            // Expected
        }

        
        // We should be at 5
        tuple = cursor.prev();
        assertEquals( 5L, (long)tuple.getKey() );
        assertEquals( "5", tuple.getValue() );
        
        assertTrue( cursor.hasPrev() );
        assertFalse( cursor.hasNext() );

        // Move back to the origin
        cursor.beforeFirst();
        
        assertFalse( cursor.hasPrev() );
        assertTrue( cursor.hasNext() );

        // We should be at 1
        tuple = cursor.next();
        assertEquals( 1L, (long)tuple.getKey() );
        assertEquals( "1", tuple.getValue() );
        
        assertFalse( cursor.hasPrev() );
        assertTrue( cursor.hasNext() );
    }


    /**
     * Test the browse methods on a btree containing just a leaf and see if we can
     * move back and forth
     */
    @Test
    public void testBrowseBTreeLeafNextPrev() throws IOException, BTreeAlreadyManagedException
    {
        // Inject some data
        btree.insert( 1L, "1" );
        btree.insert( 4L, "4" );
        btree.insert( 2L, "2" );
        btree.insert( 3L, "3" );
        btree.insert( 5L, "5" );

        // Create the cursor
        TupleCursor<Long, String> cursor = btree.browse();
        
        // We should not be able to move backward
        try
        {
            cursor.prev();
            fail();
        }
        catch ( NoSuchElementException nsee )
        {
            // Expected
        }

        // Start browsing three elements
        assertFalse( cursor.hasPrev() );
        assertTrue( cursor.hasNext() );
        Tuple<Long, String> tuple = cursor.next();
        tuple = cursor.next();
        tuple = cursor.next();
        
        // We should be at 3 now
        assertTrue( cursor.hasPrev() );
        assertTrue( cursor.hasNext() );
        assertEquals( 3L, (long)tuple.getKey() );
        assertEquals( "3", tuple.getValue() );
        
        // Now, move to the prev value
        cursor.prev();
        assertEquals( 2L, (long)tuple.getKey() );
        assertEquals( "2", tuple.getValue() );
        
        // And to the next value
        cursor.next();
        assertEquals( 3L, (long)tuple.getKey() );
        assertEquals( "3", tuple.getValue() );
    }


    /**
     * Test the browse methods on a btree containing many nodes
     */
    @Test
    public void testBrowseBTreeNodesNext() throws IOException, BTreeAlreadyManagedException
    {
        // Inject some data
        for ( long i = 1; i < 1000L; i++ )
        {
            btree.insert( i, Long.toString( i ) );
        }

        // Create the cursor
        TupleCursor<Long, String> cursor = btree.browse();
        
        // Move forward
        cursor.beforeFirst();
        
        assertFalse( cursor.hasPrev() );
        assertTrue( cursor.hasNext() );

        checkNext( cursor, 1L, "1", true, false );
        
        for ( long i = 2L; i < 999L; i++ )
        {
            checkNext( cursor, i, Long.toString( i ), true, true );
        }

        checkNext( cursor, 999L, "999", false, true );
    }


    /**
     * Test the browse methods on a btree containing many nodes
     */
    @Test
    public void testBrowseBTreeNodesPrev() throws IOException, BTreeAlreadyManagedException
    {
        // Inject some data
        for ( long i = 1; i < 1000L; i++ )
        {
            btree.insert( i, Long.toString( i ) );
        }

        // Create the cursor
        TupleCursor<Long, String> cursor = btree.browse();
        
        // Move backward
        cursor.afterLast();
        
        assertTrue( cursor.hasPrev() );
        assertFalse( cursor.hasNext() );
        
        checkPrev( cursor, 999L, "999", false, true );
        
        for ( long i = 998L; i > 1L; i-- )
        {
            checkPrev( cursor, i, Long.toString( i ), true, true );
        }

        checkPrev( cursor, 1L, "1", true, false );
    }


    /**
     * Test the browse methods on a btree containing just a leaf with duplicate values
     */
    @Test
    public void testBrowseBTreeLeafNextDups1() throws IOException, BTreeAlreadyManagedException
    {
        // Inject some duplicate data
        btree.insert( 1L, "1" );
        btree.insert( 1L, "4" );
        btree.insert( 1L, "2" );
        btree.insert( 1L, "3" );
        btree.insert( 1L, "5" );

        // Create the cursor
        TupleCursor<Long, String> cursor = btree.browse();
        
        // Move forward
        cursor.beforeFirst();
        
        assertFalse( cursor.hasPrev() );
        assertTrue( cursor.hasNext() );
        
        checkNext( cursor, 1L, "1", true, false );
        checkNext( cursor, 1L, "2", true, true );
        checkNext( cursor, 1L, "3", true, true );
        checkNext( cursor, 1L, "4", true, true );
        checkNext( cursor, 1L, "5", false, true );
    }


    /**
     * Test the browse methods on a btree containing just a leaf with duplicate values
     */
    @Test
    public void testBrowseBTreeLeafNextDupsN() throws IOException, BTreeAlreadyManagedException
    {
        // Inject some duplicate data
        btree.insert( 1L, "1" );
        btree.insert( 1L, "4" );
        btree.insert( 1L, "2" );
        btree.insert( 2L, "3" );
        btree.insert( 3L, "5" );
        btree.insert( 3L, "7" );
        btree.insert( 3L, "6" );

        // Create the cursor
        TupleCursor<Long, String> cursor = btree.browse();
        
        // Move forward
        cursor.beforeFirst();
        
        assertFalse( cursor.hasPrev() );
        assertTrue( cursor.hasNext() );
        
        checkNext( cursor, 1L, "1", true, false );
        checkNext( cursor, 1L, "2", true, true );
        checkNext( cursor, 1L, "4", true, true );
        checkNext( cursor, 2L, "3", true, true );
        checkNext( cursor, 3L, "5", true, true );
        checkNext( cursor, 3L, "6", true, true );
        checkNext( cursor, 3L, "7", false, true );
    }


    /**
     * Test the browse methods on a btree containing just a leaf with duplicate values
     */
    @Test
    public void testBrowseBTreeLeafPrevDups1() throws IOException, BTreeAlreadyManagedException
    {
        // Inject some duplicate data
        btree.insert( 1L, "1" );
        btree.insert( 1L, "4" );
        btree.insert( 1L, "2" );
        btree.insert( 1L, "3" );
        btree.insert( 1L, "5" );

        // Create the cursor
        TupleCursor<Long, String> cursor = btree.browse();
        
        // Move backward
        cursor.afterLast();
        
        assertTrue( cursor.hasPrev() );
        assertFalse( cursor.hasNext() );
        
        checkPrev( cursor, 1L, "5", false, true );
        checkPrev( cursor, 1L, "4", true, true );
        checkPrev( cursor, 1L, "3", true, true );
        checkPrev( cursor, 1L, "2", true, true );
        checkPrev( cursor, 1L, "1", true, false );
    }


    /**
     * Test the browse methods on a btree containing just a leaf with duplicate values
     */
    @Test
    public void testBrowseBTreeLeafPrevDupsN() throws IOException, BTreeAlreadyManagedException
    {
        // Inject some duplicate data
        btree.insert( 1L, "1" );
        btree.insert( 1L, "4" );
        btree.insert( 1L, "2" );
        btree.insert( 2L, "3" );
        btree.insert( 3L, "5" );
        btree.insert( 3L, "7" );
        btree.insert( 3L, "6" );

        // Create the cursor
        TupleCursor<Long, String> cursor = btree.browse();
        
        // Move backward
        cursor.afterLast();
        
        assertTrue( cursor.hasPrev() );
        assertFalse( cursor.hasNext() );
        
        checkPrev( cursor, 3L, "7", false, true );
        checkPrev( cursor, 3L, "6", true, true );
        checkPrev( cursor, 3L, "5", true, true );
        checkPrev( cursor, 2L, "3", true, true );
        checkPrev( cursor, 1L, "4", true, true );
        checkPrev( cursor, 1L, "2", true, true );
        checkPrev( cursor, 1L, "1", true, false );
    }


    /**
     * Test the browse methods on a btree containing just a leaf with duplicate values
     */
    @Test
    public void testBrowseBTreeNodesNextDupsN() throws IOException, BTreeAlreadyManagedException
    {
        // Inject some data
        for ( long i = 1; i < 1000L; i++ )
        {
            for ( long j = 1; j < 10; j++ )
            {
                btree.insert( i, Long.toString( j ) );
            }
        }

        // Create the cursor
        TupleCursor<Long, String> cursor = btree.browse();
        
        // Move backward
        cursor.beforeFirst();
        
        assertFalse( cursor.hasPrev() );
        assertTrue( cursor.hasNext() );
        boolean next = true;
        boolean prev = false;
        
        for ( long i = 1L; i < 1000L; i++ )
        {
            for ( long j = 1L; j < 10L; j++ )
            {
                checkNext( cursor, i, Long.toString( j ), next, prev );
                
                if ( ( i == 1L ) && ( j == 1L ) )
                {
                    prev = true;
                }
                
                if ( ( i == 999L ) && ( j == 8L ) )
                {
                    next = false;
                }
            }
        }
    }


    /**
     * Test the browse methods on a btree containing just a leaf with duplicate values
     */
    @Test
    public void testBrowseBTreeNodesPrevDupsN() throws IOException, BTreeAlreadyManagedException
    {
        // Inject some data
        long t0 = System.currentTimeMillis();
        for ( long i = 1; i < 1000L; i++ )
        {
            for ( int j = 1; j < 10; j++ )
            {
                btree.insert( i, Long.toString( j ) );
            }
        }
        long t1 = System.currentTimeMillis();
        
        
        System.out.println( "Delta = " + ( t1 - t0) );

        // Create the cursor
        TupleCursor<Long, String> cursor = btree.browse();
        
        // Move backward
        cursor.afterLast();
        
        assertTrue( cursor.hasPrev() );
        assertFalse( cursor.hasNext() );
        boolean next = false;
        boolean prev = true;
        
        for ( long i = 999L; i > 0L; i-- )
        {
            for ( long j = 9L; j > 0L; j-- )
            {
                checkPrev( cursor, i, Long.toString( j ), next, prev );
                
                if ( ( i == 1L ) && ( j == 2L ) )
                {
                    prev = false;
                }
                
                if ( ( i == 999L ) && ( j == 9L ) )
                {
                    next = true;
                }
            }
        }
    }

    
    private String toString( long value, int size )
    {
        String valueStr = Long.toString( value );
        
        StringBuilder sb = new StringBuilder();
        
        if ( size > valueStr.length() )
        {
            for ( int i = valueStr.length(); i < size; i++ )
            {
                sb.append( "0" );
            }
        }
        
        sb.append( valueStr );
        
        return sb.toString();
    }

    /**
     * Test the browse methods on a btree containing just a leaf with duplicate values
     * stored into a sub btree
     */
    @Test
    public void testBrowseBTreeLeafNextDupsSubBTree1() throws IOException, BTreeAlreadyManagedException
    {
        // Inject some duplicate data which will be stored into a sub btree
        for ( long i = 1L; i < 32L; i++ )
        {
            btree.insert( 1L, toString( i, 2 ) );
        }

        // Create the cursor
        TupleCursor<Long, String> cursor = btree.browse();
        
        // Move forward
        cursor.beforeFirst();
        
        assertFalse( cursor.hasPrev() );
        assertTrue( cursor.hasNext() );
        
        checkNext( cursor, 1L, "01", true, false );
        
        for ( long i = 2L; i < 31L; i++ )
        {
            checkNext( cursor, 1L, toString( i, 2 ), true, true );
        }
        
        checkNext( cursor, 1L, "31", false, true );
    }

    /**
     * Test the browse methods on a btree containing just a leaf with duplicate values
     */
    @Test
    public void testBrowseBTreeLeafPrevDupsSubBTree1() throws IOException, BTreeAlreadyManagedException
    {
        // Inject some duplicate data which will be stored into a sub btree
        for ( long i = 1L; i < 32L; i++ )
        {
            btree.insert( 1L, toString( i, 2 ) );
        }

        // Create the cursor
        TupleCursor<Long, String> cursor = btree.browse();
        
        // Move backward
        cursor.afterLast();
        
        assertTrue( cursor.hasPrev() );
        assertFalse( cursor.hasNext() );
        
        checkPrev( cursor, 1L, "31", false, true );
        
        for ( long i = 30L; i > 1L; i-- )
        {
            checkPrev( cursor, 1L, toString( i, 2 ), true, true );
        }
        
        checkPrev( cursor, 1L, "01", true, false );
    }
}