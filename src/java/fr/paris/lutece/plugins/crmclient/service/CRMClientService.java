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
package fr.paris.lutece.plugins.crmclient.service;

import fr.paris.lutece.plugins.crmclient.business.CRMItemTypeEnum;
import fr.paris.lutece.plugins.crmclient.business.ICRMItem;
import fr.paris.lutece.plugins.crmclient.business.ICRMItemFactory;
import fr.paris.lutece.plugins.crmclient.service.processor.ICRMClientProcessor;
import fr.paris.lutece.plugins.crmclient.service.queue.ICRMClientQueue;
import fr.paris.lutece.plugins.crmclient.util.CRMException;

import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 *
 * AbstractCRMClientService
 *
 */
@ApplicationScoped
@Named( ICRMClientService.BEAN_SERVICE )
public class CRMClientService implements ICRMClientService
{
    // CONSTANTS
    private static final String MEDIA_TYPE_JSON = "application/json";
    private static final String MEDIA_TYPE_XML = "application/xml";
    @Inject
    private ICRMClientQueue _crmClientQueue;
    @Inject
    private ICRMItemFactory _crmItemFactory;
    @Inject
    ICRMClientProcessor _crmClientProcessor;

    /**
     * {@inheritDoc}
     */
    @Override
    public ICRMClientQueue getQueue( )
    {
        return _crmClientQueue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notify( String strIdDemand, String strObject, String strMessage, String strSender )
    {
        notify( strIdDemand, strObject, strMessage, strSender, StringUtils.EMPTY );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notify( String strIdDemand, String strObject, String strMessage, String strSender, String strCRMWebAppCode )
    {
        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.NOTIFICATION.toString( ) );

        if ( StringUtils.isNotBlank( strCRMWebAppCode ) )
        {
            crmItem.setCRMWebAppCode( strCRMWebAppCode );
        }

        crmItem.putParameter( ICRMItem.ID_DEMAND, StringUtils.isNotBlank( strIdDemand ) ? strIdDemand : StringUtils.EMPTY );
        crmItem.putParameter( ICRMItem.NOTIFICATION_OBJECT, StringUtils.isNotBlank( strObject ) ? strObject : StringUtils.EMPTY );
        crmItem.putParameter( ICRMItem.NOTIFICATION_MESSAGE, StringUtils.isNotBlank( strMessage ) ? strMessage : StringUtils.EMPTY );
        crmItem.putParameter( ICRMItem.NOTIFICATION_SENDER, StringUtils.isNotBlank( strSender ) ? strSender : StringUtils.EMPTY );

        _crmClientQueue.send( crmItem );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendUpdateDemand( String strIdDemand, String strStatusText ) throws CRMException
    {
        sendUpdateDemand( strIdDemand, strStatusText, StringUtils.EMPTY );
    }

    /**
     * /** {@inheritDoc}
     */
    @Override
    public void sendUpdateDemand( String strIdDemand, String strStatusText, String strCRMWebAppCode ) throws CRMException
    {
        sendUpdateDemand( strIdDemand, strStatusText, strCRMWebAppCode, StringUtils.EMPTY, StringUtils.EMPTY );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendUpdateDemand( String strIdDemand, String strStatusText, String strCRMWebAppCode, String strIdStatusCRM ) throws CRMException
    {
        sendUpdateDemand( strIdDemand, strStatusText, strCRMWebAppCode, strIdStatusCRM, StringUtils.EMPTY );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendUpdateDemand( String strIdDemand, String strStatusText, String strCRMWebAppCode, String strIdStatusCRM, String strData )
            throws CRMException
    {
        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.DEMAND_UPDATE.toString( ) );

        if ( StringUtils.isNotBlank( strCRMWebAppCode ) )
        {
            crmItem.setCRMWebAppCode( strCRMWebAppCode );
        }

        if ( StringUtils.isNotBlank( strIdStatusCRM ) )
        {
            crmItem.putParameter( ICRMItem.ID_STATUS_CRM, strIdStatusCRM );
        }

        if ( StringUtils.isNotBlank( strData ) )
        {
            crmItem.putParameter( ICRMItem.DEMAND_DATA, strData );
        }

        crmItem.putParameter( ICRMItem.ID_DEMAND, StringUtils.isNotBlank( strIdDemand ) ? strIdDemand : StringUtils.EMPTY );
        crmItem.putParameter( ICRMItem.STATUS_TEXT, StringUtils.isNotBlank( strStatusText ) ? strStatusText : StringUtils.EMPTY );

        _crmClientProcessor.doProcess( crmItem );
    }

    @Override
    public String sendCreateDemandByUserGuid( String strIdDemandType, String strUserGuid, String strIdStatusCRM, String strStatusText, String strData )
            throws CRMException
    {
        return sendCreateDemandByUserGuid( strIdDemandType, strUserGuid, strIdStatusCRM, strStatusText, strData, StringUtils.EMPTY );
    }

    @Override
    public String sendCreateDemandByUserGuid( String strIdDemandType, String strUserGuid, String strIdStatusCRM, String strStatusText, String strData,
            String strCRMWebAppCode ) throws CRMException
    {

        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.DEMAND_CREATE_BY_USER_GUID.toString( ) );

        if ( StringUtils.isNotBlank( strCRMWebAppCode ) )
        {
            crmItem.setCRMWebAppCode( strCRMWebAppCode );
        }

        crmItem.putParameter( ICRMItem.ID_DEMAND_TYPE, StringUtils.isNotBlank( strIdDemandType ) ? strIdDemandType : StringUtils.EMPTY );

        crmItem.putParameter( ICRMItem.USER_GUID, StringUtils.isNotBlank( strUserGuid ) ? strUserGuid : StringUtils.EMPTY );

        crmItem.putParameter( ICRMItem.ID_STATUS_CRM, StringUtils.isNotBlank( strIdStatusCRM ) ? strIdStatusCRM : StringUtils.EMPTY );

        crmItem.putParameter( ICRMItem.STATUS_TEXT, StringUtils.isNotBlank( strStatusText ) ? strStatusText : StringUtils.EMPTY );

        crmItem.putParameter( ICRMItem.DEMAND_DATA, StringUtils.isNotBlank( strData ) ? strData : StringUtils.EMPTY );

        return _crmClientProcessor.doProcess( crmItem );
    }

    @Override
    @Deprecated
    public String sendCreateDemandByIdCRMUser( String strIdDemandType, String strIdCRMUser, String strIdStatusCRM, String strStatusText, String strData )
            throws CRMException
    {
        return sendCreateDemandByIdCRMUser( strIdDemandType, strIdCRMUser, strIdStatusCRM, strStatusText, strData, StringUtils.EMPTY );
    }

    @Override
    @Deprecated
    public String sendCreateDemandByIdCRMUser( String strIdDemandType, String strIdCRMUser, String strIdStatusCRM, String strStatusText, String strData,
            String strCRMWebAppCode ) throws CRMException
    {
        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.DEMAND_CREATE_BY_ID_CRM_USER.toString( ) );

        if ( StringUtils.isNotBlank( strCRMWebAppCode ) )
        {
            crmItem.setCRMWebAppCode( strCRMWebAppCode );
        }

        crmItem.putParameter( ICRMItem.ID_DEMAND_TYPE, StringUtils.isNotBlank( strIdDemandType ) ? strIdDemandType : StringUtils.EMPTY );

        crmItem.putParameter( ICRMItem.ID_CRM_USER, StringUtils.isNotBlank( strIdCRMUser ) ? strIdCRMUser : StringUtils.EMPTY );

        crmItem.putParameter( ICRMItem.ID_STATUS_CRM, StringUtils.isNotBlank( strIdStatusCRM ) ? strIdStatusCRM : StringUtils.EMPTY );

        crmItem.putParameter( ICRMItem.STATUS_TEXT, StringUtils.isNotBlank( strStatusText ) ? strStatusText : StringUtils.EMPTY );

        crmItem.putParameter( ICRMItem.DEMAND_DATA, StringUtils.isNotBlank( strData ) ? strData : StringUtils.EMPTY );

        return _crmClientProcessor.doProcess( crmItem );
    }

    @Override
    public void sendDeleteDemand( String strIdDemand ) throws CRMException
    {
        sendDeleteDemand( strIdDemand, StringUtils.EMPTY );
    }

    @Override
    public void sendDeleteDemand( String strIdDemand, String strCRMWebAppCode ) throws CRMException
    {
        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.DEMAND_DELETE.toString( ) );

        if ( StringUtils.isNotBlank( strCRMWebAppCode ) )
        {
            crmItem.setCRMWebAppCode( strCRMWebAppCode );
        }

        crmItem.putParameter( ICRMItem.ID_DEMAND, StringUtils.isNotBlank( strIdDemand ) ? strIdDemand : StringUtils.EMPTY );

        _crmClientProcessor.doProcess( crmItem );
    }

    @Override
    public String getUserGuidFromIdDemand( String strIdDemand ) throws CRMException
    {
        return getUserGuidFromIdDemand( strIdDemand, StringUtils.EMPTY );
    }

    @Override
    public String getUserGuidFromIdDemand( String strIdDemand, String strCRMWebAppCode ) throws CRMException
    {
        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.DEMAND_USER_GUID.toString( ) );

        if ( StringUtils.isNotBlank( strCRMWebAppCode ) )
        {
            crmItem.setCRMWebAppCode( strCRMWebAppCode );
        }

        crmItem.putParameter( ICRMItem.ID_DEMAND, StringUtils.isNotBlank( strIdDemand ) ? strIdDemand : StringUtils.EMPTY );

        return _crmClientProcessor.getProcess( crmItem );
    }

    @Override
    public String getDemandXml( String strIdDemand ) throws CRMException
    {
        return getDemandXml( strIdDemand, StringUtils.EMPTY );
    }

    @Override
    public String getDemandXml( String strIdDemand, String strCRMWebAppCode ) throws CRMException
    {
        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.DEMAND_DEMAND_XML.toString( ) );

        if ( StringUtils.isNotBlank( strCRMWebAppCode ) )
        {
            crmItem.setCRMWebAppCode( strCRMWebAppCode );
        }

        crmItem.putParameter( ICRMItem.ID_DEMAND, StringUtils.isNotBlank( strIdDemand ) ? strIdDemand : StringUtils.EMPTY );
        crmItem.putParameter( ICRMItem.MEDIA_TYPE, MEDIA_TYPE_JSON );

        return _crmClientProcessor.getProcess( crmItem );
    }

    @Override
    public String getDemandJson( String strIdDemand ) throws CRMException
    {
        return getDemandJson( strIdDemand, StringUtils.EMPTY );
    }

    @Override
    public String getDemandJson( String strIdDemand, String strCRMWebAppCode ) throws CRMException
    {
        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.DEMAND_DEMAND_JSON.toString( ) );

        if ( StringUtils.isNotBlank( strCRMWebAppCode ) )
        {
            crmItem.setCRMWebAppCode( strCRMWebAppCode );
        }

        crmItem.putParameter( ICRMItem.ID_DEMAND, StringUtils.isNotBlank( strIdDemand ) ? strIdDemand : StringUtils.EMPTY );
        crmItem.putParameter( ICRMItem.MEDIA_TYPE, MEDIA_TYPE_JSON );

        return _crmClientProcessor.getProcess( crmItem );
    }

    @Override
    @Deprecated
    public String getUserGuidFromIdCRMUser( String strIdCRMUser ) throws CRMException
    {
        // TODO Auto-generated method stub
        return getUserGuidFromIdCRMUser( strIdCRMUser, StringUtils.EMPTY );
    }

    @Override
    @Deprecated
    public String getUserGuidFromIdCRMUser( String strIdCRMUser, String strCRMWebAppCode ) throws CRMException
    {
        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.USER_GUID.toString( ) );

        if ( StringUtils.isNotBlank( strCRMWebAppCode ) )
        {
            crmItem.setCRMWebAppCode( strCRMWebAppCode );
        }

        crmItem.putParameter( ICRMItem.ID_CRM_USER, StringUtils.isNotBlank( strIdCRMUser ) ? strIdCRMUser : StringUtils.EMPTY );

        return _crmClientProcessor.getProcess( crmItem );
    }

    @Override
    public String getCRMUserAttribute( String strUserGuid, String strAttribute ) throws CRMException
    {
        return getCRMUserAttribute( strUserGuid, strAttribute, StringUtils.EMPTY );
    }

    @Override
    public String getCRMUserAttribute( String strUserGuid, String strAttribute, String strCRMWebAppCode ) throws CRMException
    {
        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.USER_ATTRIBUTE.toString( ) );

        if ( StringUtils.isNotBlank( strCRMWebAppCode ) )
        {
            crmItem.setCRMWebAppCode( strCRMWebAppCode );
        }

        crmItem.putParameter( ICRMItem.USER_GUID, StringUtils.isNotBlank( strUserGuid ) ? strUserGuid : StringUtils.EMPTY );

        crmItem.putParameter( ICRMItem.USER_ATTRIBUTE, StringUtils.isNotBlank( strAttribute ) ? strAttribute : StringUtils.EMPTY );

        return _crmClientProcessor.getProcess( crmItem );
    }

    @Override
    public String getCRMUserAttributesXml( String strUserGuid ) throws CRMException
    {
        // TODO Auto-generated method stub
        return getCRMUserAttributesXml( strUserGuid, StringUtils.EMPTY );
    }

    @Override
    public String getCRMUserAttributesXml( String strUserGuid, String strCRMWebAppCode ) throws CRMException
    {
        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.USER_ATTRIBUTES_XML.toString( ) );

        if ( StringUtils.isNotBlank( strCRMWebAppCode ) )
        {
            crmItem.setCRMWebAppCode( strCRMWebAppCode );
        }

        crmItem.putParameter( ICRMItem.USER_GUID, StringUtils.isNotBlank( strUserGuid ) ? strUserGuid : StringUtils.EMPTY );

        crmItem.putParameter( ICRMItem.MEDIA_TYPE, MEDIA_TYPE_XML );

        return _crmClientProcessor.getProcess( crmItem );
    }

    @Override
    public String getCRMUserAttributesJson( String strUserGuid ) throws CRMException
    {
        // TODO Auto-generated method stub
        return getCRMUserAttributesJson( strUserGuid, StringUtils.EMPTY );
    }

    @Override
    public String getCRMUserAttributesJson( String strUserGuid, String strCRMWebAppCode ) throws CRMException
    {
        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.USER_ATTRIBUTES_JSON.toString( ) );

        if ( StringUtils.isNotBlank( strCRMWebAppCode ) )
        {
            crmItem.setCRMWebAppCode( strCRMWebAppCode );
        }

        crmItem.putParameter( ICRMItem.USER_GUID, StringUtils.isNotBlank( strUserGuid ) ? strUserGuid : StringUtils.EMPTY );
        crmItem.putParameter( ICRMItem.MEDIA_TYPE, MEDIA_TYPE_JSON );

        return _crmClientProcessor.getProcess( crmItem );
    }

    @Override
    public String getDemandJsonV2( String strRemoteId, String strIdDemandType, String strCRMWebAppCode ) throws CRMException
    {
        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.DEMAND_DEMAND_JSON_V2.toString( ) );

        if ( StringUtils.isNotBlank( strCRMWebAppCode ) )
        {
            crmItem.setCRMWebAppCode( strCRMWebAppCode );
        }

        crmItem.putParameter( ICRMItem.REMOTE_ID, StringUtils.isNotBlank( strRemoteId ) ? strRemoteId : StringUtils.EMPTY );
        crmItem.putParameter( ICRMItem.ID_DEMAND_TYPE, StringUtils.isNotBlank( strIdDemandType ) ? strIdDemandType : StringUtils.EMPTY );

        crmItem.putParameter( ICRMItem.MEDIA_TYPE, MEDIA_TYPE_JSON );

        return _crmClientProcessor.getProcess( crmItem );
    }

    @Override
    public String getDemandXmlV2( String strRemoteId, String strIdDemandType, String strCRMWebAppCode ) throws CRMException
    {
        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.DEMAND_DEMAND_XML_V2.toString( ) );

        if ( StringUtils.isNotBlank( strCRMWebAppCode ) )
        {
            crmItem.setCRMWebAppCode( strCRMWebAppCode );
        }

        crmItem.putParameter( ICRMItem.REMOTE_ID, StringUtils.isNotBlank( strRemoteId ) ? strRemoteId : StringUtils.EMPTY );
        crmItem.putParameter( ICRMItem.ID_DEMAND_TYPE, StringUtils.isNotBlank( strIdDemandType ) ? strIdDemandType : StringUtils.EMPTY );
        crmItem.putParameter( ICRMItem.MEDIA_TYPE, MEDIA_TYPE_JSON );

        return _crmClientProcessor.getProcess( crmItem );
    }

    @Override
    public void notifyV2( String strRemoteId, String strIdDemandType, String strObject, String strMessage, String strSender, String strCRMWebAppCode )

    {

        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.NOTIFICATION_V2.toString( ) );

        if ( StringUtils.isNotBlank( strCRMWebAppCode ) )
        {
            crmItem.setCRMWebAppCode( strCRMWebAppCode );
        }

        crmItem.putParameter( ICRMItem.REMOTE_ID, StringUtils.isNotBlank( strRemoteId ) ? strRemoteId : StringUtils.EMPTY );
        crmItem.putParameter( ICRMItem.ID_DEMAND_TYPE, StringUtils.isNotBlank( strIdDemandType ) ? strIdDemandType : StringUtils.EMPTY );
        crmItem.putParameter( ICRMItem.NOTIFICATION_OBJECT, StringUtils.isNotBlank( strObject ) ? strObject : StringUtils.EMPTY );
        crmItem.putParameter( ICRMItem.NOTIFICATION_MESSAGE, StringUtils.isNotBlank( strMessage ) ? strMessage : StringUtils.EMPTY );
        crmItem.putParameter( ICRMItem.NOTIFICATION_SENDER, StringUtils.isNotBlank( strSender ) ? strSender : StringUtils.EMPTY );

        _crmClientQueue.send( crmItem );

    }

    @Override
    public String sendCreateDemandByUserGuidV2( String strRemoteId, String strIdDemandType, String strUserGuid, String strIdStatusCRM, String strStatusText,
            String strData, String strCRMWebAppCode ) throws CRMException
    {

        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.DEMAND_CREATE_BY_USER_GUID_V2.toString( ) );

        if ( StringUtils.isNotBlank( strCRMWebAppCode ) )
        {
            crmItem.setCRMWebAppCode( strCRMWebAppCode );
        }

        crmItem.putParameter( ICRMItem.REMOTE_ID, StringUtils.isNotBlank( strRemoteId ) ? strRemoteId : StringUtils.EMPTY );
        crmItem.putParameter( ICRMItem.ID_DEMAND_TYPE, StringUtils.isNotBlank( strIdDemandType ) ? strIdDemandType : StringUtils.EMPTY );

        crmItem.putParameter( ICRMItem.USER_GUID, StringUtils.isNotBlank( strUserGuid ) ? strUserGuid : StringUtils.EMPTY );

        crmItem.putParameter( ICRMItem.ID_STATUS_CRM, StringUtils.isNotBlank( strIdStatusCRM ) ? strIdStatusCRM : StringUtils.EMPTY );

        crmItem.putParameter( ICRMItem.STATUS_TEXT, StringUtils.isNotBlank( strStatusText ) ? strStatusText : StringUtils.EMPTY );

        crmItem.putParameter( ICRMItem.DEMAND_DATA, StringUtils.isNotBlank( strData ) ? strData : StringUtils.EMPTY );

        return _crmClientProcessor.doProcess( crmItem );
    }

    @Override
    public void sendDeleteDemandV2( String strRemoteId, String strIdDemandType, String strCRMWebAppCode ) throws CRMException
    {
        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.DEMAND_DELETE_V2.toString( ) );

        if ( StringUtils.isNotBlank( strCRMWebAppCode ) )
        {
            crmItem.setCRMWebAppCode( strCRMWebAppCode );
        }

        crmItem.putParameter( ICRMItem.REMOTE_ID, StringUtils.isNotBlank( strRemoteId ) ? strRemoteId : StringUtils.EMPTY );
        crmItem.putParameter( ICRMItem.ID_DEMAND_TYPE, StringUtils.isNotBlank( strIdDemandType ) ? strIdDemandType : StringUtils.EMPTY );

        _crmClientProcessor.doProcess( crmItem );

    }

    @Override
    public void sendUpdateDemandV2( String strRemoteId, String strIdDemandType, String strStatusText, String strCRMWebAppCode, String strIdStatusCRM,
            String strData ) throws CRMException
    {
        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.DEMAND_UPDATE_V2.toString( ) );

        if ( StringUtils.isNotBlank( strCRMWebAppCode ) )
        {
            crmItem.setCRMWebAppCode( strCRMWebAppCode );
        }

        if ( StringUtils.isNotBlank( strIdStatusCRM ) )
        {
            crmItem.putParameter( ICRMItem.ID_STATUS_CRM, strIdStatusCRM );
        }

        if ( StringUtils.isNotBlank( strData ) )
        {
            crmItem.putParameter( ICRMItem.DEMAND_DATA, strData );
        }

        crmItem.putParameter( ICRMItem.REMOTE_ID, StringUtils.isNotBlank( strRemoteId ) ? strRemoteId : StringUtils.EMPTY );
        crmItem.putParameter( ICRMItem.ID_DEMAND_TYPE, StringUtils.isNotBlank( strIdDemandType ) ? strIdDemandType : StringUtils.EMPTY );

        crmItem.putParameter( ICRMItem.STATUS_TEXT, StringUtils.isNotBlank( strStatusText ) ? strStatusText : StringUtils.EMPTY );

        _crmClientProcessor.doProcess( crmItem );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCRMDemandTypes( ) throws CRMException
    {
        ICRMItem crmItem = _crmItemFactory.newCRMItem( CRMItemTypeEnum.DEMAND_TYPES.toString( ) );

        return _crmClientProcessor.doProcess( crmItem );
    }
}
