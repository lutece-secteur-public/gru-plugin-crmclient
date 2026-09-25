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
package fr.paris.lutece.plugins.crmclient.service.authenticator;

import fr.paris.lutece.util.signrequest.AbstractAuthenticator;
import fr.paris.lutece.util.signrequest.AbstractPrivateKeyAuthenticator;
import fr.paris.lutece.util.signrequest.RequestAuthenticator;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.eclipse.microprofile.config.ConfigProvider;

@ApplicationScoped
@Named( "crmclient.requestAuthenticatorService" )
public class AuthenticatorService implements IAuthenticatorService
{
    private static final String DEFAULT_AUTHENTICATOR_CODE = "default";
    private static final String PROPERTY_WEBAPP_CODES = "crmclient.requestAuthenticator.webappCodes";
    private static final String PREFIX_WS = "crmclient.requestAuthenticatorForWs.";
    private static final String PREFIX_URL = "crmclient.requestAuthenticatorForUrl.";
    @Inject
    @Named( "crmclient.requestAuthenticatorForWs" )
    private AbstractPrivateKeyAuthenticator _authenticatorForWs;
    @Inject
    @Named( "crmclient.requestAuthenticatorForUrl" )
    private AbstractPrivateKeyAuthenticator _authenticatorForUrl;
    @Inject
    private AuthenticatorProducer _authenticatorProducer;

    private Map<String, RequestAuthenticator> _mapRequestAuthenticatorForWs;
    private Map<String, AbstractAuthenticator> _mapRequestAuthenticatorForUrl;

    /**
     * Builds the authenticator maps: the default ones, then one per webapp code listed in crmclient.requestAuthenticator.webappCodes.
     */
    @PostConstruct
    public void init( )
    {
        _mapRequestAuthenticatorForWs = new HashMap<>( );
        _mapRequestAuthenticatorForWs.put( DEFAULT_AUTHENTICATOR_CODE, _authenticatorForWs );
        _mapRequestAuthenticatorForUrl = new HashMap<>( );
        _mapRequestAuthenticatorForUrl.put( DEFAULT_AUTHENTICATOR_CODE, _authenticatorForUrl );

        for ( String strCode : ConfigProvider.getConfig( ).getOptionalValues( PROPERTY_WEBAPP_CODES, String.class ).orElse( List.of( ) ) )
        {
            RequestAuthenticator authenticatorForWs = _authenticatorProducer.produceForPrefix( PREFIX_WS + strCode );
            if ( authenticatorForWs != null )
            {
                _mapRequestAuthenticatorForWs.put( strCode, authenticatorForWs );
            }
            if ( _authenticatorProducer.produceForPrefix( PREFIX_URL + strCode ) instanceof AbstractAuthenticator authenticatorForUrl )
            {
                _mapRequestAuthenticatorForUrl.put( strCode, authenticatorForUrl );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestAuthenticator getRequestAuthenticatorForWs( String strCrmWebbAppCode )
    {
        if ( StringUtils.isEmpty( strCrmWebbAppCode ) )
        {
            return _mapRequestAuthenticatorForWs.get( DEFAULT_AUTHENTICATOR_CODE );
        }

        return _mapRequestAuthenticatorForWs.containsKey( strCrmWebbAppCode ) ? _mapRequestAuthenticatorForWs.get( strCrmWebbAppCode )
                : _mapRequestAuthenticatorForWs.get( DEFAULT_AUTHENTICATOR_CODE );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractAuthenticator getRequestAuthenticatorForUrl( String strCrmWebbAppCode )
    {
        if ( StringUtils.isEmpty( strCrmWebbAppCode ) )
        {
            return _mapRequestAuthenticatorForUrl.get( DEFAULT_AUTHENTICATOR_CODE );
        }

        return _mapRequestAuthenticatorForUrl.containsKey( strCrmWebbAppCode ) ? _mapRequestAuthenticatorForUrl.get( strCrmWebbAppCode )
                : _mapRequestAuthenticatorForUrl.get( DEFAULT_AUTHENTICATOR_CODE );
    }
}
