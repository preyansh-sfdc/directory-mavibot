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
package org.apache.mavibot.btree;

/**
 * The result of an delete operation, when the key to delete is not present in the tree.
 * 
 * @param <K> The type for the Key
 * @param <V> The type for the stored value

 * @author <a href="mailto:labs@laps.apache.org">Mavibot labs Project</a>
 */
class NotPresentResult<K, V> implements DeleteResult<K, V>
{
    public static final NotPresentResult NOT_PRESENT = new NotPresentResult();
    
    private NotPresentResult()
    {
        // Do nothing
    }
}