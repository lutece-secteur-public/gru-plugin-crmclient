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

import fr.paris.lutece.plugins.crmclient.service.CRMClientPlugin;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.DAOUtil;
import fr.paris.lutece.util.sql.Transaction;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * This class provides Data Access methods for CRMItemQueue objects
 *
 */
@ApplicationScoped
@Named( "crmclient.crmItemQueueDAO" )
public class CRMItemQueueDAO implements ICRMItemQueueDAO
{
    private static final String SQL_QUERY_NEW_PK = " SELECT max(id_crm_queue) FROM crm_client_crm_queue ";
    private static final String SQL_QUERY_SELECT_NEXT_CRM_ITEM_QUEUE_ID = " SELECT min(id_crm_queue) FROM crm_client_crm_queue WHERE is_locked = 0 ";
    private static final String SQL_QUERY_SELECT_COUNT = " SELECT COUNT(id_crm_queue) FROM crm_client_crm_queue ";
    private static final String SQL_QUERY_LOAD_CRM_ITEM = " SELECT id_crm_queue, crm_item FROM crm_client_crm_item WHERE id_crm_queue = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO crm_client_crm_queue( id_crm_queue ) VALUES( ? ) ";
    private static final String SQL_QUERY_INSERT_CRM_ITEM = " INSERT INTO crm_client_crm_item(id_crm_queue, crm_item) VALUES( ?,? ) ";
    private static final String SQL_QUERY_LOCK_CRM_ITEM = " UPDATE crm_client_crm_queue SET is_locked = 1 WHERE id_crm_queue = ? ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM crm_client_crm_queue WHERE id_crm_queue = ? ";
    private static final String SQL_QUERY_DELETE_CRM_ITEM = " DELETE FROM crm_client_crm_item WHERE id_crm_queue = ? ";

    /**
     * Generates a new primary key
     * 
     * @return The new primary key
     */
    private int newPrimaryKey( )
    {
        int nKey = 1;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, CRMClientPlugin.getPlugin( ) ) )
        {
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                nKey = daoUtil.getInt( 1 ) + 1;
            }
        }

        return nKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nextCRMItemQueueId( )
    {
        int nIdCRMItemQueue = -1;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_NEXT_CRM_ITEM_QUEUE_ID, CRMClientPlugin.getPlugin( ) ) )
        {
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                nIdCRMItemQueue = daoUtil.getInt( 1 );
            }
        }

        return nIdCRMItemQueue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void lockCRMItemQueue( int nIdCRMItemQueue )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_LOCK_CRM_ITEM, CRMClientPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdCRMItemQueue );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( CRMItemQueue crmItemQueue )
    {
        Transaction transaction = null;

        try
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream( );
            ObjectOutputStream objectOutputStream;
            objectOutputStream = new ObjectOutputStream( byteArrayOutputStream );
            objectOutputStream.writeObject( crmItemQueue.getCRMItem( ) );
            objectOutputStream.close( );
            byteArrayOutputStream.close( );

            transaction = new Transaction( CRMClientPlugin.getPlugin( ) );

            int nNewPrimaryKey = newPrimaryKey( );
            crmItemQueue.setIdCRMItemQueue( nNewPrimaryKey );
            transaction.prepareStatement( SQL_QUERY_INSERT );
            transaction.getStatement( ).setInt( 1, nNewPrimaryKey );
            transaction.executeStatement( );
            transaction.prepareStatement( SQL_QUERY_INSERT_CRM_ITEM );
            transaction.getStatement( ).setInt( 1, nNewPrimaryKey );
            transaction.getStatement( ).setBytes( 2, byteArrayOutputStream.toByteArray( ) );
            transaction.executeStatement( );

            transaction.commit( );
        }
        catch( Exception e )
        {
            if ( transaction != null )
            {
                transaction.rollback( e );
            }

            AppLogService.error( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CRMItemQueue load( int nIdCRMItemQueue )
    {
        CRMItemQueue crmItemQueue = null;
        ICRMItem crmItem = null;
        InputStream inputStream;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_LOAD_CRM_ITEM, CRMClientPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdCRMItemQueue );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                crmItemQueue = new CRMItemQueue( );
                crmItemQueue.setIdCRMItemQueue( daoUtil.getInt( 1 ) );
                inputStream = daoUtil.getBinaryStream( 2 );

                try
                {
                    ObjectInputStream objectInputStream = new ObjectInputStream( inputStream );
                    crmItem = (ICRMItem) objectInputStream.readObject( );
                    objectInputStream.close( );
                    inputStream.close( );
                }
                catch( IOException e )
                {
                    AppLogService.error( e );
                }
                catch( ClassNotFoundException e )
                {
                    AppLogService.error( e );
                }

                crmItemQueue.setCRMItem( crmItem );
            }
        }

        return crmItemQueue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdCRMItemQueue )
    {
        Transaction transaction = new Transaction( CRMClientPlugin.getPlugin( ) );

        try
        {
            transaction.prepareStatement( SQL_QUERY_DELETE_CRM_ITEM );
            transaction.getStatement( ).setInt( 1, nIdCRMItemQueue );
            transaction.executeStatement( );
            transaction.prepareStatement( SQL_QUERY_DELETE );
            transaction.getStatement( ).setInt( 1, nIdCRMItemQueue );
            transaction.executeStatement( );
            transaction.commit( );
        }

        catch( Exception e )
        {
            transaction.rollback( e );
            AppLogService.error( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCountCRMItem( )
    {
        int nCount = 0;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_COUNT, CRMClientPlugin.getPlugin( ) ) )
        {
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                nCount = daoUtil.getInt( 1 );
            }
        }

        return nCount;
    }
}
