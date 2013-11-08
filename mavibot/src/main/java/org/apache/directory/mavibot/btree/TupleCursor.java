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
package org.apache.directory.mavibot.btree;


import java.io.IOException;

import org.apache.directory.mavibot.btree.exception.EndOfFileExceededException;


/**
 * A Cursor is used to fetch elements in a BTree and is returned by the
 * @see BTree#browse method. The cursor <strng>must</strong> be closed
 * when the user is done with it.
 * <p>
 *
 * @param <K> The type for the Key
 * @param <V> The type for the stored value
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface TupleCursor<K, V> extends Cursor<K>
{
    /**
     * Find the next key/value
     * 
     * @return A Tuple containing the found key and value
     * @throws IOException 
     * @throws EndOfFileExceededException 
     */
    Tuple<K, V> next() throws EndOfFileExceededException, IOException;


    /**
     * Find the previous key/value
     * 
     * @return A Tuple containing the found key and value
     * @throws IOException 
     * @throws EndOfFileExceededException 
     */
    Tuple<K, V> prev() throws EndOfFileExceededException, IOException;


    /**
     * @return The revision this cursor is based on
     */
    long getRevision();


    /**
     * @return The creation date for this cursor
     */
    long getCreationDate();


    /**
     * Moves the cursor to the next non-duplicate key.

     * If the BTree contains 
     * 
     *  <ul>
     *    <li><1,0></li>
     *    <li><1,1></li>
     *    <li><2,0></li>
     *    <li><2,1></li>
     *  </ul>
     *   
     *  and cursor is present at <1,0> then the cursor will move to <2,0>
     *  
     * @throws EndOfFileExceededException
     * @throws IOException
     */
    void moveToNextNonDuplicateKey() throws EndOfFileExceededException, IOException;


    /**
     * Moves the cursor to the previous non-duplicate key
     * If the BTree contains 
     * 
     *  <ul>
     *    <li><1,0></li>
     *    <li><1,1></li>
     *    <li><2,0></li>
     *    <li><2,1></li>
     *  </ul>
     *   
     *  and cursor is present at <2,1> then the cursor will move to <1,1>
     * 
     * @throws EndOfFileExceededException
     * @throws IOException
     */
    void moveToPrevNonDuplicateKey() throws EndOfFileExceededException, IOException;
}