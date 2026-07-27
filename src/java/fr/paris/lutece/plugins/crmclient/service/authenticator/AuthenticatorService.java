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
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ApplicationScoped
@Named( "crmclient.requestAuthenticatorService" )
public class AuthenticatorService implements IAuthenticatorService
{
    private static final String DEFAULT_AUTHENTICATOR_CODE = "default";
    @Inject
    @Named( "crmclient.requestAuthenticatorForWs" )
    private AbstractPrivateKeyAuthenticator _authenticatorForWs;
    @Inject
    @Named( "crmclient.requestAuthenticatorForUrl" )
    private AbstractPrivateKeyAuthenticator _authenticatorForUrl;
    private Map<String, RequestAuthenticator> _mapRequestAuthenticatorForWs;
    private Map<String, AbstractAuthenticator> _mapRequestAuthenticatorForUrl;

    /**
     * Build the authenticator maps from the injected authenticators.
     */
    @PostConstruct
    public void init( )
    {
        _mapRequestAuthenticatorForWs = new HashMap<>( );
        _mapRequestAuthenticatorForWs.put( DEFAULT_AUTHENTICATOR_CODE, _authenticatorForWs );
        _mapRequestAuthenticatorForUrl = new HashMap<>( );
        _mapRequestAuthenticatorForUrl.put( DEFAULT_AUTHENTICATOR_CODE, _authenticatorForUrl );
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
