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


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;

import org.apache.mavibot.btree.BTree;
import org.apache.mavibot.btree.exception.BTreeAlreadyManagedException;
import org.apache.mavibot.btree.serializer.IntSerializer;
import org.apache.mavibot.btree.serializer.LongArraySerializer;
import org.apache.mavinot.btree.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The RecordManager is used to manage the file in which we will store the BTrees. 
 * A RecordManager will manage more than one BTree.<br/>
 * 
 * It stores data in fixed size pages (default size is 4Kb), which may be linked one to 
 * the other if the data we want to store is too bug for a page.
 * if 
 * @author <a href="mailto:labs@labs.apache.org">Mavibot labs Project</a>
 */
public class RecordManager
{
    /** The LoggerFactory used by this class */
    protected static final Logger LOG = LoggerFactory.getLogger( RecordManager.class );

    /** The file name */
    private String fileName;

    /** The associated file */
    private File file;

    /** The channel used to read and write data */
    private FileChannel fileChannel;

    /** The number of stored BTrees */
    private int nbBtree;

    /** The first and last free page */
    private long firstFreePage;
    private long lastFreePage;

    /** 
     * A Btree used to manage the page that has been copied in a new version.
     * Those page can be reclaimed when the associated version is dead. 
     **/
    private BTree<Integer, long[]> copiedPageBTree;

    /** A constant for an offset on a non existing page */
    private static final long NO_PAGE = -1L;

    /** The number of stored BTrees */
    private static final int NB_TREE_SIZE = 4;

    /** The header page size */
    private static final int PAGE_SIZE = 4;

    /** The size of the data size in a page */
    private static final int DATA_SIZE = 4;

    /** The size of the link to next page */
    private static final int LINK_SIZE = 8;

    /** The size of the link to the first and last free page */
    private static final int FIRST_FREE_PAGE_SIZE = 8;
    private static final int LAST_FREE_PAGE_SIZE = 8;

    private static final int HEADER_SIZE = NB_TREE_SIZE + PAGE_SIZE + FIRST_FREE_PAGE_SIZE + LAST_FREE_PAGE_SIZE;
    private static final ByteBuffer HEADER_BUFFER = ByteBuffer.allocate( HEADER_SIZE );

    /** The default page size */
    private static final int DEFAULT_PAGE_SIZE = 4 * 1024;

    /** The RecordManager underlying page size. */
    private int pageSize = -1;

    /** A buffer used to read a page */
    private ByteBuffer blockBuffer;

    /** The set of managed BTrees */
    private Map<String, BTree<?, ?>> managedBTrees;

    /** The default file name */
    private static final String DEFAULT_FILE_NAME = "mavibot.db";


    /**
     * Create a Record manager which will either create the underlying file
     * or load an existing one. If a folder is provider, then we will create
     * a file with a default name : mavibot.db
     * 
     * @param name The file name, or a folder name
     */
    public RecordManager( String fileName )
    {
        this.fileName = fileName;

        // Open the file or create it
        File tmpFile = new File( fileName );
        boolean isNewFile = false;

        if ( tmpFile.isDirectory() )
        {
            // It's a directory. Check that we don't have an existing mavibot file
            File mavibotFile = new File( tmpFile, DEFAULT_FILE_NAME );

            if ( !mavibotFile.exists() )
            {
                // We have to create a new file
                try
                {
                    mavibotFile.createNewFile();
                }
                catch ( IOException ioe )
                {
                    LOG.error( "Cannot create the file {}", mavibotFile.getName() );
                    return;
                }
            }

            file = mavibotFile;
        }
        else
        {
            // It's a file. Let's see if it exists, otherwise create it
            if ( !tmpFile.exists() )
            {
                isNewFile = true;

                try
                {
                    tmpFile.createNewFile();
                }
                catch ( IOException ioe )
                {
                    LOG.error( "Cannot create the file {}", tmpFile.getName() );
                    return;
                }
            }

            file = tmpFile;
        }

        try
        {
            RandomAccessFile randomFile = new RandomAccessFile( file, "rw" );
            fileChannel = randomFile.getChannel();

            if ( isNewFile )
            {
                initRecordManager();
            }
            else
            {
                loadRecordManager();
            }
        }
        catch ( IOException ioe )
        {
            LOG.error( "Error while initializing the RecordManager : {}", ioe.getMessage() );
        }
    }


    /**
     * We will create a brand new RecordManager file, containing nothing
     */
    public void initRecordManager() throws IOException
    {
        // Create a new Header
        // The page size
        HEADER_BUFFER.putInt( pageSize );

        // The number of managed BTree (currently we have only one : the discardedPage BTree
        HEADER_BUFFER.putInt( 1 );

        // The first free page
        HEADER_BUFFER.putLong( NO_PAGE );
        firstFreePage = NO_PAGE;

        // The last free page
        HEADER_BUFFER.putLong( NO_PAGE );
        lastFreePage = NO_PAGE;

        // Now, initialize the Discarded Page BTree
        copiedPageBTree = new BTree<Integer, long[]>( "copiedPageBTree", new IntSerializer(), new LongArraySerializer() );

        // Inject this BTree into the RecordManager
        try
        {
            manage( copiedPageBTree );
        }
        catch ( BTreeAlreadyManagedException btame )
        {
            // Can't happen here.
        }

        // We are all set !
    }


    /**
     * We will load all the existing BTrees in this record manager.
     */
    public void loadRecordManager() throws IOException
    {
        if ( fileChannel.size() != 0 )
        {
            // The file exists, we have to load the data now 
            fileChannel.read( HEADER_BUFFER );

            // The page size
            pageSize = HEADER_BUFFER.getInt();

            blockBuffer = ByteBuffer.allocate( pageSize );

            // The number of stored BTrees
            nbBtree = HEADER_BUFFER.getInt();

            // The first free page
            firstFreePage = HEADER_BUFFER.getLong();
            lastFreePage = HEADER_BUFFER.getLong();

            // Read the meta-data header
            ByteBuffer header = readHeader();

            // Read the btrees
            for ( int i = 0; i < nbBtree; i++ )
            {
                //----
            }
        }
    }


    /**
     * Manage a BTree. The btree will be stored and managed by this RecordManager.
     *  
     * @param btree The new BTree to manage.
     */
    public synchronized void manage( BTree<?, ?> btree ) throws BTreeAlreadyManagedException, IOException
    {
        String name = btree.getName();

        if ( managedBTrees.containsKey( name ) )
        {
            // There is already a BTree with this name in the recordManager...
            LOG.error( "There is already a BTree named '{}' managed by this recordManager", name );
            throw new BTreeAlreadyManagedException( name );
        }

        managedBTrees.put( name, btree );

        // We will add the newly managed BTree at the end of the header.
        byte[] btreeNameBytes = Strings.getBytesUtf8( name );
        String keySerializerFqcn = btree.getKeySerializer().getClass().getName();
        byte[] keySerializerBytes = Strings.getBytesUtf8( keySerializerFqcn );
        String valueSerializerFqcn = btree.getKeySerializer().getClass().getName();
        byte[] valueSerializerBytes = Strings.getBytesUtf8( valueSerializerFqcn );

        int bufferSize = btreeNameBytes.length + keySerializerBytes.length + valueSerializerBytes.length;

        // Get the pageIOs we need to store the data. We may need more than one.
        PageIO[] pageIos = fetchPageIOs( bufferSize );

        // Now store the data in the pages.
        //storeData( pageIos, btreeNameBytes, keySerializerBytes, valueSerializerBytes );

        // And flush the pages to disk now
        //flushPages( pageIos );
    }


    /**
     * Get as many pages as needed to store the data which size is provided
     *  
     * @param dataSize The data size
     * @return An array of pages, enough to store the full data
     */
    private PageIO[] fetchPageIOs( int dataSize ) throws IOException
    {
        // Compute the number of pages needed.
        // Considering that each page coan contain PageSize bytes,
        // but that the first 8 bytes are used for links and we 
        // use 4 bytes to store the data size, the number of needed
        // pages is :
        // NbPages = ( (dataSize - (PageSize - 8 - 4 )) / (PageSize - 8) ) + 1 
        // NbPages += ( if (dataSize - (PageSize - 8 - 4 )) % (PageSize - 8) > 0 : 1 : 0 )
        int availableSize = ( pageSize - 8 );
        int nbNeededPages = 1;

        // Compute the number of pages that will be full but the first page
        if ( dataSize > availableSize + 4 )
        {
            int remainingSize = dataSize - ( availableSize + 4 );
            nbNeededPages += remainingSize / availableSize;

            if ( remainingSize % availableSize > 0 )
            {
                nbNeededPages++;
            }
        }

        PageIO[] pageIOs = new PageIO[nbNeededPages];

        for ( int i = 0; i < nbNeededPages; i++ )
        {
            pageIOs[i] = fetchNewPage();
        }

        return pageIOs;
    }


    /**
     * Return a new Page 
     * @return
     */
    private PageIO fetchNewPage() throws IOException
    {
        if ( firstFreePage == NO_PAGE )
        {
            // We don't have any free page. Reclaim some new page at the end
            // of the file
            long offset = fileChannel.size();
            PageIO newPage = new PageIO( offset );

            byte[] data = new byte[pageSize];

            newPage.setData( data );
            newPage.setNextPage( NO_PAGE );
            newPage.setSize( -1 );
        }
        else
        {
            // We have some existing free page. Fetch one from there.
        }

        return null;
    }


    public void setPageSize( int pageSize )
    {
        if ( this.pageSize != -1 )
        {
        }
        else
        {
            this.pageSize = pageSize;
        }
    }


    /** 
     * Read the header. It will contain all the BTree headers. The header is stored in
     * the first block, which may be linked to some other blocks. The first block contains 
     * the header's size on 4 bytes, then the data, and if we have a linked block, the last
     * 8 bytes contain the reference on the next page.
     * <br/>
     * <pre>
     * +----+--------+-----...---+    +--------+---------...----------+
     * |Size|NextPage|    data   | -->|NextPage|        data          | --> ...
     * +----+--------+-----...---+    +--------+---------...----------+
     *   ^      ^
     *   |      |
     *   |      +--- Offset of the next page, or -1 if no linked page
     *   |
     *   +----------------------- Size of the header
     * </pre>
     * @return
     */
    private ByteBuffer readHeader() throws IOException
    {
        ByteBuffer dataBuffer = null;

        // Read the first block
        fileChannel.read( blockBuffer );

        // Now, get its size, and check if we have more pages to read
        int dataSize = blockBuffer.getInt();

        dataBuffer = ByteBuffer.allocate( dataSize );

        if ( dataSize + DATA_SIZE + LINK_SIZE > pageSize )
        {
            // We have more than one page to read
            long nextPage = blockBuffer.getLong();

            dataBuffer.put( blockBuffer );

            dataSize -= pageSize - DATA_SIZE - LINK_SIZE;

            // Loop on pages
            while ( dataSize > 0 )
            {
                blockBuffer.clear();
                fileChannel.read( blockBuffer, nextPage );

                nextPage = blockBuffer.getLong();
                dataSize -= pageSize - LINK_SIZE;

                if ( nextPage == -1L )
                {
                    dataBuffer.put( blockBuffer.array(), LINK_SIZE, dataSize );
                }
                else
                {
                    dataBuffer.put( blockBuffer );
                }
            }
        }
        else
        {
            fileChannel.read( dataBuffer );
        }

        return dataBuffer;
    }
}