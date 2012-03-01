/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.plugins.crmclient.business.MokeCRMItem;
import fr.paris.lutece.plugins.crmclient.util.http.MokeWebServiceCaller;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * CRMClientWebServiceTest
 *
 */
public class CRMClientWebServiceTest extends LuteceTestCase
{
    private static final String KEY1 = "Key1";
    private static final String KEY2 = "Key2";

    /**
     * Test of doProcess method of fr.paris.lutece.plugins.crmclient.service.CRMClientWebService
     */
    public void testDoProcess(  )
    {
        System.out.println( "doProcess" );

        CRMClientWebService webService = new CRMClientWebService(  );
        webService.setWebServiceCaller( new MokeWebServiceCaller(  ) );

        List<String> listElements = new ArrayList<String>(  );
        listElements.add( KEY1 );
        listElements.add( KEY2 );
        webService.setSignatureElements( listElements );

        try
        {
            webService.doProcess( new MokeCRMItem(  ) );
        }
        catch ( HttpAccessException e )
        {
            fail(  );
        }
        catch ( CRMClientException e )
        {
            fail(  );
        }
    }
}
