/*
 * Copyright (c) 2002-2026, Mairie de Paris
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

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.crmclient.service.authenticator.IAuthenticatorService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.signrequest.AbstractPrivateKeyAuthenticator;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Tests the authenticators chosen per webapp code.
 */
public class AuthenticatorServiceTest extends LuteceTestCase
{
    private static final List<String> ELEMENTS = List.of( "1", "2" );

    @Inject
    @Named( "crmclient.requestAuthenticatorService" )
    private IAuthenticatorService _authenticatorService;

    /**
     * Declares a webapp code with its own keys before the service builds its maps.
     */
    @BeforeAll
    public static void declareProCode( )
    {
        System.setProperty( "crmclient.requestAuthenticator.webappCodes", "pro" );
        System.setProperty( "crmclient.requestAuthenticatorForWs.pro.name", "signrequest.HeaderHashAuthenticator" );
        System.setProperty( "crmclient.requestAuthenticatorForWs.pro.cfg.privateKey", "pro-key" );
        System.setProperty( "crmclient.requestAuthenticatorForUrl.pro.name", "signrequest.RequestHashAuthenticator" );
        System.setProperty( "crmclient.requestAuthenticatorForUrl.pro.cfg.privateKey", "pro-key" );
    }

    /**
     * A declared code signs with its own key, any other code with the default one.
     */
    @Test
    public void testAuthenticatorPerWebappCode( )
    {
        String strDefaultWs = sign( _authenticatorService.getRequestAuthenticatorForWs( null ) );
        String strDefaultUrl = sign( _authenticatorService.getRequestAuthenticatorForUrl( null ) );

        assertFalse( strDefaultWs.equals( sign( _authenticatorService.getRequestAuthenticatorForWs( "pro" ) ) ) );
        assertFalse( strDefaultUrl.equals( sign( _authenticatorService.getRequestAuthenticatorForUrl( "pro" ) ) ) );
        assertEquals( strDefaultWs, sign( _authenticatorService.getRequestAuthenticatorForWs( "part" ) ) );
        assertEquals( strDefaultUrl, sign( _authenticatorService.getRequestAuthenticatorForUrl( "part" ) ) );
    }

    /**
     * Signs fixed elements with an authenticator.
     *
     * @param authenticator
     *            the authenticator
     * @return the signature
     */
    private static String sign( Object authenticator )
    {
        return ( (AbstractPrivateKeyAuthenticator) authenticator ).buildSignature( ELEMENTS, "1" );
    }
}
