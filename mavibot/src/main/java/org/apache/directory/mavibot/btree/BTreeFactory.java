/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.mavibot.btree;


import java.util.LinkedList;

import org.apache.directory.mavibot.btree.serializer.ElementSerializer;


/**
 * This class construct a B-tree from a serialized version of a B-tree. We need it
 * to avoid exposing all the methods of the B-tree class.<br>
 *
 * All its methods are static.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 *
 * @param <K> The B-tree key type
 * @param <V> The B-tree value type
 */
public class BTreeFactory<K, V>
{
    //--------------------------------------------------------------------------------------------
    // Create persisted btrees
    //--------------------------------------------------------------------------------------------
    /**
     * Creates a new persisted B-tree, with no initialization.
     *
     * @return a new B-tree instance
     */
    public static <K, V> BTree<K, V> createPersistedBTree()
    {
        BTree<K, V> btree = new BTreeImpl<K, V>();

        return btree;
    }


    /**
     * Creates a new persisted B-tree, with no initialization.
     *
     * @return a new B-tree instance
     */
    public static <K, V> BTree<K, V> createPersistedBTree( BTreeTypeEnum type )
    {
        BTree<K, V> btree = new BTreeImpl<K, V>();
        ( ( AbstractBTree<K, V> ) btree ).setType( type );

        return btree;
    }


    /**
     * Sets the btreeHeader offset for a Persisted BTree
     *
     * @param btree The btree to update
     * @param btreeHeaderOffset The offset
     */
    public static <K, V> void setBtreeHeaderOffset( BTreeImpl<K, V> btree, long btreeHeaderOffset )
    {
        btree.setBtreeHeaderOffset( btreeHeaderOffset );
    }


    /**
     * Creates a new persisted B-tree using the BTreeConfiguration to initialize the
     * B-tree
     *
     * @param configuration The configuration to use
     * @return a new B-tree instance
     */
    public static <K, V> BTree<K, V> createPersistedBTree( BTreeConfiguration<K, V> configuration )
    {
        BTree<K, V> btree = new BTreeImpl<K, V>( configuration );

        return btree;
    }


    /**
     * Creates a new persisted B-tree using the parameters to initialize the
     * B-tree
     *
     * @param name The B-tree's name
     * @param keySerializer Key serializer
     * @param valueSerializer Value serializer
     * @return a new B-tree instance
     */
    public static <K, V> BTree<K, V> createPersistedBTree( String name, ElementSerializer<K> keySerializer,
        ElementSerializer<V> valueSerializer )
    {
        BTreeConfiguration<K, V> configuration = new BTreeConfiguration<K, V>();

        configuration.setName( name );
        configuration.setKeySerializer( keySerializer );
        configuration.setValueSerializer( valueSerializer );
        configuration.setPageSize( BTree.DEFAULT_PAGE_SIZE );
        configuration.setCacheSize( BTreeImpl.DEFAULT_CACHE_SIZE );
        configuration.setWriteBufferSize( BTree.DEFAULT_WRITE_BUFFER_SIZE );

        BTree<K, V> btree = new BTreeImpl<K, V>( configuration );

        return btree;
    }


    /**
     * Creates a new persisted B-tree using the parameters to initialize the
     * B-tree
     *
     * @param name The B-tree's name
     * @param keySerializer Key serializer
     * @param valueSerializer Value serializer
     * @param allowDuplicates Tells if the B-tree allows multiple value for a given key
     * @return a new B-tree instance
     */
    public static <K, V> BTree<K, V> createPersistedBTree( String name, ElementSerializer<K> keySerializer,
        ElementSerializer<V> valueSerializer, boolean allowDuplicates )
    {
        BTreeConfiguration<K, V> configuration = new BTreeConfiguration<K, V>();

        configuration.setName( name );
        configuration.setKeySerializer( keySerializer );
        configuration.setValueSerializer( valueSerializer );
        configuration.setPageSize( BTree.DEFAULT_PAGE_SIZE );
        configuration.setCacheSize( BTreeImpl.DEFAULT_CACHE_SIZE );
        configuration.setWriteBufferSize( BTree.DEFAULT_WRITE_BUFFER_SIZE );

        BTree<K, V> btree = new BTreeImpl<K, V>( configuration );

        return btree;
    }


    /**
     * Creates a new persisted B-tree using the parameters to initialize the
     * B-tree
     *
     * @param name The B-tree's name
     * @param keySerializer Key serializer
     * @param valueSerializer Value serializer
     * @param allowDuplicates Tells if the B-tree allows multiple value for a given key
     * @param cacheSize The size to be used for this B-tree cache
     * @return a new B-tree instance
     */
    public static <K, V> BTree<K, V> createPersistedBTree( String name, ElementSerializer<K> keySerializer,
        ElementSerializer<V> valueSerializer, boolean allowDuplicates, int cacheSize )
    {
        BTreeConfiguration<K, V> configuration = new BTreeConfiguration<K, V>();

        configuration.setName( name );
        configuration.setKeySerializer( keySerializer );
        configuration.setValueSerializer( valueSerializer );
        configuration.setPageSize( BTree.DEFAULT_PAGE_SIZE );
        configuration.setCacheSize( cacheSize );
        configuration.setWriteBufferSize( BTree.DEFAULT_WRITE_BUFFER_SIZE );

        BTree<K, V> btree = new BTreeImpl<K, V>( configuration );

        return btree;
    }


    /**
     * Creates a new persisted B-tree using the parameters to initialize the
     * B-tree
     *
     * @param name The B-tree's name
     * @param keySerializer Key serializer
     * @param valueSerializer Value serializer
     * @param pageSize Size of the page
     * @return a new B-tree instance
     */
    public static <K, V> BTree<K, V> createPersistedBTree( String name, ElementSerializer<K> keySerializer,
        ElementSerializer<V> valueSerializer, int pageSize )
    {
        BTreeConfiguration<K, V> configuration = new BTreeConfiguration<K, V>();

        configuration.setName( name );
        configuration.setKeySerializer( keySerializer );
        configuration.setValueSerializer( valueSerializer );
        configuration.setPageSize( pageSize );
        configuration.setCacheSize( BTreeImpl.DEFAULT_CACHE_SIZE );
        configuration.setWriteBufferSize( BTree.DEFAULT_WRITE_BUFFER_SIZE );

        BTree<K, V> btree = new BTreeImpl<K, V>( configuration );

        return btree;
    }


    /**
     * Creates a new persisted B-tree using the parameters to initialize the
     * B-tree
     *
     * @param name The B-tree's name
     * @param keySerializer Key serializer
     * @param valueSerializer Value serializer
     * @param pageSize Size of the page
     * @param allowDuplicates Tells if the B-tree allows multiple value for a given key
     * @return a new B-tree instance
     */
    public static <K, V> BTree<K, V> createPersistedBTree( String name, ElementSerializer<K> keySerializer,
        ElementSerializer<V> valueSerializer, int pageSize, boolean allowDuplicates )
    {
        BTreeConfiguration<K, V> configuration = new BTreeConfiguration<K, V>();

        configuration.setName( name );
        configuration.setKeySerializer( keySerializer );
        configuration.setValueSerializer( valueSerializer );
        configuration.setPageSize( pageSize );
        configuration.setCacheSize( BTreeImpl.DEFAULT_CACHE_SIZE );
        configuration.setWriteBufferSize( BTree.DEFAULT_WRITE_BUFFER_SIZE );

        BTree<K, V> btree = new BTreeImpl<K, V>( configuration );

        return btree;
    }


    /**
     * Creates a new persisted B-tree using the parameters to initialize the
     * B-tree
     *
     * @param name The B-tree's name
     * @param keySerializer Key serializer
     * @param valueSerializer Value serializer
     * @param pageSize Size of the page
     * @param allowDuplicates Tells if the B-tree allows multiple value for a given key
     * @param cacheSize The size to be used for this B-tree cache
     * @return a new B-tree instance
     */
    public static <K, V> BTree<K, V> createPersistedBTree( String name, ElementSerializer<K> keySerializer,
        ElementSerializer<V> valueSerializer, int pageSize, boolean allowDuplicates, int cacheSize )
    {
        BTreeConfiguration<K, V> configuration = new BTreeConfiguration<K, V>();

        configuration.setName( name );
        configuration.setKeySerializer( keySerializer );
        configuration.setValueSerializer( valueSerializer );
        configuration.setPageSize( pageSize );
        configuration.setCacheSize( cacheSize );
        configuration.setWriteBufferSize( BTree.DEFAULT_WRITE_BUFFER_SIZE );

        BTree<K, V> btree = new BTreeImpl<K, V>( configuration );

        return btree;
    }


    //--------------------------------------------------------------------------------------------
    // Create Pages
    //--------------------------------------------------------------------------------------------
    /**
     * Create a new Leaf for the given B-tree.
     *
     * @param btree The B-tree which will contain this leaf
     * @param revision The Leaf's revision
     * @param nbElems The number or elements in this leaf
     *
     * @return A Leaf instance
     */
    /* no qualifier*/static <K, V> Page<K, V> createLeaf( BTree<K, V> btree, long revision, int nbElems )
    {
        return new Leaf<K, V>( btree, revision, nbElems );
    }


    /**
     * Create a new Node for the given B-tree.
     *
     * @param btree The B-tree which will contain this node
     * @param revision The Node's revision
     * @param nbElems The number or elements in this node
     * @return A Node instance
     */
    /* no qualifier*/static <K, V> Page<K, V> createNode( BTree<K, V> btree, long revision, int nbElems )
    {
        //System.out.println( "Creating a node with nbElems : " + nbElems );
        return new Node<K, V>( btree, revision, nbElems );
    }


    //--------------------------------------------------------------------------------------------
    // Update pages
    //--------------------------------------------------------------------------------------------
    /**
     * Set the key at a give position
     *
     * @param btree The B-tree to update
     * @param page The page to update
     * @param pos The position in the keys array
     * @param key The key to inject
     */
    /* no qualifier*/static <K, V> void setKey( BTree<K, V> btree, Page<K, V> page, int pos, K key )
    {
        KeyHolder<K> keyHolder = new KeyHolderImpl<K>( btree.getKeySerializer(), key );

        ( ( AbstractPage<K, V> ) page ).setKey( pos, keyHolder );
    }


    /**
     * Set the value at a give position
     *
     * @param btree The B-tree to update
     * @param page The page to update
     * @param pos The position in the values array
     * @param value the value to inject
     */
    /* no qualifier*/static <K, V> void setValue( BTree<K, V> btree, Page<K, V> page, int pos, ValueHolder<V> value )
    {
        ( ( Leaf<K, V> ) page ).setValue( pos, value );
    }


    /**
     * Set the page at a give position
     *
     * @param btree The B-tree to update
     * @param page The page to update
     * @param pos The position in the values array
     * @param child the child page to inject
     */
    /* no qualifier*/static <K, V> void setPage( BTree<K, V> btree, Page<K, V> page, int pos, Page<K, V> child )
    {
        ( ( Node<K, V> ) page ).setValue( pos, new PageHolderImpl<K, V>( btree, child ) );
    }


    //--------------------------------------------------------------------------------------------
    // Update B-tree
    //--------------------------------------------------------------------------------------------
    /**
     * Sets the KeySerializer into the B-tree
     *
     * @param btree The B-tree to update
     * @param keySerializerFqcn the Key serializer FQCN to set
     * @throws ClassNotFoundException If the key serializer class cannot be found
     * @throws InstantiationException If the key serializer class cannot be instanciated
     * @throws IllegalAccessException If the key serializer class cannot be accessed
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    /* no qualifier*/static <K, V> void setKeySerializer( BTree<K, V> btree, String keySerializerFqcn )
        throws ClassNotFoundException, IllegalAccessException, InstantiationException, IllegalArgumentException,
        SecurityException, NoSuchFieldException
    {
        Class<?> keySerializer = Class.forName( keySerializerFqcn );
        @SuppressWarnings("unchecked")
        ElementSerializer<K> instance = null;
        try
        {
            instance = ( ElementSerializer<K> ) keySerializer.getDeclaredField( "INSTANCE" ).get( null );
        }
        catch ( NoSuchFieldException e )
        {
            // ignore
        }

        if ( instance == null )
        {
            instance = ( ElementSerializer<K> ) keySerializer.newInstance();
        }

        btree.setKeySerializer( instance );
    }


    /**
     * Sets the ValueSerializer into the B-tree
     *
     * @param btree The B-tree to update
     * @param valueSerializerFqcn the Value serializer FQCN to set
     * @throws ClassNotFoundException If the value serializer class cannot be found
     * @throws InstantiationException If the value serializer class cannot be instanciated
     * @throws IllegalAccessException If the value serializer class cannot be accessed
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    /* no qualifier*/static <K, V> void setValueSerializer( BTree<K, V> btree, String valueSerializerFqcn )
        throws ClassNotFoundException, IllegalAccessException, InstantiationException, IllegalArgumentException,
        SecurityException, NoSuchFieldException
    {
        Class<?> valueSerializer = Class.forName( valueSerializerFqcn );
        @SuppressWarnings("unchecked")
        ElementSerializer<V> instance = null;
        try
        {
            instance = ( ElementSerializer<V> ) valueSerializer.getDeclaredField( "INSTANCE" ).get( null );
        }
        catch ( NoSuchFieldException e )
        {
            // ignore
        }

        if ( instance == null )
        {
            instance = ( ElementSerializer<V> ) valueSerializer.newInstance();
        }

        btree.setValueSerializer( instance );
    }


    /**
     * Set the new root page for this tree. Used for debug purpose only. The revision
     * will always be 0;
     *
     * @param btree The B-tree to update
     * @param root the new root page.
     */
    /* no qualifier*/static <K, V> void setRootPage( BTree<K, V> btree, Page<K, V> root )
    {
        ( ( AbstractBTree<K, V> ) btree ).setRootPage( root );
    }


    /**
     * Return the B-tree root page
     *
     * @param btree The B-tree we want to root page from
     * @return The root page
     */
    /* no qualifier */static <K, V> Page<K, V> getRootPage( BTree<K, V> btree )
    {
        return btree.getRootPage();
    }


    /**
     * Update the B-tree number of elements
     *
     * @param btree The B-tree to update
     * @param nbElems the nbElems to set
     */
    /* no qualifier */static <K, V> void setNbElems( BTree<K, V> btree, long nbElems )
    {
        ( ( AbstractBTree<K, V> ) btree ).setNbElems( nbElems );
    }


    /**
     * Update the B-tree revision
     *
     * @param btree The B-tree to update
     * @param revision the revision to set
     */
    /* no qualifier*/static <K, V> void setRevision( BTree<K, V> btree, long revision )
    {
        ( ( AbstractBTree<K, V> ) btree ).setRevision( revision );
    }


    /**
     * Set the B-tree name
     *
     * @param btree The B-tree to update
     * @param name the name to set
     */
    /* no qualifier */static <K, V> void setName( BTree<K, V> btree, String name )
    {
        btree.setName( name );
    }


    /**
     * Set the maximum number of elements we can store in a page.
     *
     * @param btree The B-tree to update
     * @param pageSize The requested page size
     */
    /* no qualifier */static <K, V> void setPageSize( BTree<K, V> btree, int pageSize )
    {
        btree.setPageSize( pageSize );
    }


    //--------------------------------------------------------------------------------------------
    // Utility method
    //--------------------------------------------------------------------------------------------
    /**
     * Includes the intermediate nodes in the path up to and including the right most leaf of the tree
     *
     * @param btree the B-tree
     * @return a LinkedList of all the nodes and the final leaf
     */
    /* no qualifier*/static <K, V> LinkedList<ParentPos<K, V>> getPathToRightMostLeaf( BTree<K, V> btree )
    {
        LinkedList<ParentPos<K, V>> stack = new LinkedList<ParentPos<K, V>>();

        ParentPos<K, V> last = new ParentPos<K, V>( btree.getRootPage(), btree.getRootPage().getNbElems() );
        stack.push( last );

        if ( btree.getRootPage().isLeaf() )
        {
            Page<K, V> leaf = btree.getRootPage();
            ValueHolder<V> valueHolder = ( ( AbstractPage<K, V> ) leaf ).getValue( last.pos );
        }
        else
        {
            Page<K, V> node = btree.getRootPage();

            while ( true )
            {
                Page<K, V> p = ( ( AbstractPage<K, V> ) node ).getPage( node.getNbElems() );

                last = new ParentPos<K, V>( p, p.getNbElems() );
                stack.push( last );

                if ( p.isLeaf() )
                {
                    Page<K, V> leaf = last.page;
                    ValueHolder<V> valueHolder = ( ( AbstractPage<K, V> ) leaf ).getValue( last.pos );
                    break;
                }
            }
        }

        return stack;
    }


    //--------------------------------------------------------------------------------------------
    // Persisted B-tree methods
    //--------------------------------------------------------------------------------------------
    /**
     * Set the rootPage offset of the B-tree
     *
     * @param btree The B-tree to update
     * @param rootPageOffset The rootPageOffset to set
     */
    /* no qualifier*/static <K, V> void setRootPageOffset( BTree<K, V> btree, long rootPageOffset )
    {
        if ( btree instanceof BTreeImpl )
        {
            ( ( BTreeImpl<K, V> ) btree ).getBtreeHeader().setRootPageOffset( rootPageOffset );
        }
        else
        {
            throw new IllegalArgumentException( "The B-tree must be a PersistedBTree" );
        }
    }


    /**
     * Set the RecordManager
     *
     * @param btree The B-tree to update
     * @param recordManager The injected RecordManager
     */
    /* no qualifier*/static <K, V> void setRecordManager( BTree<K, V> btree, RecordManager recordManager )
    {
        if ( btree instanceof BTreeImpl )
        {
            ( ( BTreeImpl<K, V> ) btree ).setRecordManager( recordManager );
        }
        else
        {
            throw new IllegalArgumentException( "The B-tree must be a PersistedBTree" );
        }
    }


    /**
     * Set the key at a give position
     *
     * @param btree The B-tree to update
     * @param page The page to update
     * @param pos The position of this key in the page
     * @param buffer The byte[] containing the serialized key
     */
    /* no qualifier*/static <K, V> void setKey( BTree<K, V> btree, Page<K, V> page, int pos, byte[] buffer )
    {
        if ( btree instanceof BTreeImpl )
        {
            KeyHolder<K> keyHolder = new KeyHolderImpl<K>( btree.getKeySerializer(), buffer );
            ( ( AbstractPage<K, V> ) page ).setKey( pos, keyHolder );
        }
        else
        {
            throw new IllegalArgumentException( "The B-tree must be a PersistedBTree" );
        }
    }


    /**
     * Includes the intermediate nodes in the path up to and including the left most leaf of the tree
     *
     * @param btree The B-tree to process
     * @return a LinkedList of all the nodes and the final leaf
     */
    /* no qualifier*/static <K, V> LinkedList<ParentPos<K, V>> getPathToLeftMostLeaf( BTree<K, V> btree )
    {
        if ( btree instanceof BTreeImpl )
        {
            LinkedList<ParentPos<K, V>> stack = new LinkedList<ParentPos<K, V>>();

            ParentPos<K, V> first = new ParentPos<K, V>( btree.getRootPage(), 0 );
            stack.push( first );

            Page<K, V> node = btree.getRootPage();

            while ( true )
            {
                Page<K, V> page = ( ( AbstractPage<K, V> ) node ).getPage( 0 );

                first = new ParentPos<K, V>( page, 0 );
                stack.push( first );

                if ( page.isLeaf() )
                {
                    break;
                }
            }

            return stack;
        }
        else
        {
            throw new IllegalArgumentException( "The B-tree must be a PersistedBTree" );
        }
    }
}
