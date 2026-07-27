/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.crmclient.business;

import fr.paris.lutece.plugins.crmclient.service.queue.ICRMClientQueue;
import fr.paris.lutece.test.LuteceTestCase;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

/**
 *
 * CRMItemQueueTest
 *
 */
public class CRMItemQueueTest extends LuteceTestCase
{
    @Inject
    @Named( ICRMClientQueue.BEAN_SERVICE )
    private ICRMClientQueue _clientQueue;
    @Inject
    private ICRMItemQueueDAO _dao;

    /**
     * Test business of class fr.paris.lutece.plugins.crmclient.business.CRMItemQueue
     */
    @Test
    public void testBusiness( )
    {
        // Initialize an object
        CRMItemQueue queue = new CRMItemQueue( );
        queue.setCRMItem( new MokeCRMItem( ) );

        // Test create
        _dao.insert( queue );

        CRMItemQueue queueStored = _clientQueue.getNextCRMItemQueue( );
        assertEquals( queue.getIdCRMItemQueue( ), queueStored.getIdCRMItemQueue( ) );

        for ( Entry<String, String> parameter : queue.getCRMItem( ).getParameters( ).entrySet( ) )
        {
            String strValueStored = queueStored.getCRMItem( ).getParameters( ).get( parameter.getKey( ) );
            assertEquals( parameter.getValue( ), strValueStored );
        }

        // Test finders
        _dao.getCountCRMItem( );

        // Test delete
        _dao.delete( queue.getIdCRMItemQueue( ) );
        queueStored = _clientQueue.getNextCRMItemQueue( );
        assertNull( queueStored );
    }
}
