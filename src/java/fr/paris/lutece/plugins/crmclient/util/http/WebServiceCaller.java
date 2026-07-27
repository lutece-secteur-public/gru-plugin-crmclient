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
package fr.paris.lutece.plugins.crmclient.util.http;

import fr.paris.lutece.plugins.crmclient.util.CRMException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;
import fr.paris.lutece.util.signrequest.HeaderHashAuthenticator;
import fr.paris.lutece.util.signrequest.NoSecurityAuthenticator;
import fr.paris.lutece.util.signrequest.RequestAuthenticator;
import fr.paris.lutece.util.signrequest.RequestHashAuthenticator;

import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * WebServiceCaller
 *
 */
@ApplicationScoped
@Named( "crmclient.webServiceCaller" )
public class WebServiceCaller implements IWebServiceCaller
{
    /**
     * {@inheritDoc}
     */
    @Override
    public String callWebService( String strUrl, Map<String, String> mapParameters, RequestAuthenticator authenticator, List<String> listElements,
            HttpMethodEnum httpMethod ) throws CRMException
    {
        String strResponse = StringUtils.EMPTY;

        if ( AppLogService.isDebugEnabled( ) )
        {
            AppLogService.debug( trace( strUrl, mapParameters, authenticator, listElements ) );
        }

        try
        {
            HttpAccess httpAccess = new HttpAccess( );

            if ( httpMethod == HttpMethodEnum.POST )
            {
                strResponse = httpAccess.doPost( strUrl, mapParameters, authenticator, listElements );
            }
            else
            {
                strResponse = httpAccess.doGet( strUrl, authenticator, listElements );
            }
        }
        catch( HttpAccessException e )
        {
            String strError = "Error connecting to '" + strUrl + "' : ";
            AppLogService.error( "{}{}", strError, e.getMessage( ), e );
            throw new CRMException( strError, e );
        }

        return strResponse;
    }

    /**
     * Trace the web service call
     * 
     * @param strUrl
     *            The WS URI
     * @param mapParameters
     *            The parameters
     * @param authenticator
     *            The Request Authenticator
     * @param listElements
     *            The list of elements to use to build the signature
     * @return The trace
     */
    protected String trace( String strUrl, Map<String, String> mapParameters, RequestAuthenticator authenticator, List<String> listElements )
    {
        StringBuilder sbTrace = new StringBuilder( );
        sbTrace.append( "\n ---------------------- CRM Client WebService Call -------------------" );
        sbTrace.append( "\nWebService URL : " ).append( strUrl );
        sbTrace.append( "\nParameters : " );

        for ( Entry<String, String> parameter : mapParameters.entrySet( ) )
        {
            sbTrace.append( "\n   " ).append( parameter.getKey( ) ).append( ":" ).append( parameter.getValue( ) );
        }

        sbTrace.append( "\nSecurity : " );
        sbTrace.append( "\nValues used for the request signature : " );

        for ( String strValue : listElements )
        {
            sbTrace.append( "\n   " ).append( strValue );
        }

        if ( authenticator instanceof RequestHashAuthenticator )
        {
            RequestHashAuthenticator auth = (RequestHashAuthenticator) authenticator;
            String strTimestamp = "" + new Date( ).getTime( );
            String strSignature = auth.buildSignature( listElements, strTimestamp );
            sbTrace.append( "\n Request Authenticator : RequestHashAuthenticator" );
            sbTrace.append( "\n Timestamp sample : " ).append( strTimestamp );
            sbTrace.append( "\n Signature for this timestamp : " ).append( strSignature );
        }
        else
            if ( authenticator instanceof HeaderHashAuthenticator )
            {
                HeaderHashAuthenticator auth = (HeaderHashAuthenticator) authenticator;
                String strTimestamp = Long.toString( new Date( ).getTime( ) );
                String strSignature = auth.buildSignature( listElements, strTimestamp );
                sbTrace.append( "\n Request Authenticator : HeaderHashAuthenticator" );
                sbTrace.append( "\n Timestamp sample : " ).append( strTimestamp );
                sbTrace.append( "\n Signature for this timestamp : " ).append( strSignature );
            }
            else
                if ( authenticator instanceof NoSecurityAuthenticator )
                {
                    sbTrace.append( "\n No request authentification" );
                }
                else
                {
                    sbTrace.append( "\n Unknown Request authenticator" );
                }

        sbTrace.append( "\n --------------------------------------------------------------------" );

        return sbTrace.toString( );
    }
}
