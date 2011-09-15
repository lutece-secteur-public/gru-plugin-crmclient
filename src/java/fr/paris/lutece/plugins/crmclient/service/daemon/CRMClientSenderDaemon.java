/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.crmclient.service.daemon;

import fr.paris.lutece.plugins.crmclient.business.ICRMItem;
import fr.paris.lutece.plugins.crmclient.service.CRMClientException;
import fr.paris.lutece.plugins.crmclient.service.CRMClientService;
import fr.paris.lutece.plugins.crmclient.service.queue.ICRMClientQueue;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.httpaccess.HttpAccessException;


/**
 *
 * CRMClientSenderDaemon
 *
 */
public class CRMClientSenderDaemon extends Daemon
{
    /**
     * Implements Runable interface
     */
    public synchronized void run(  )
    {
        ICRMClientQueue queue = CRMClientService.getService(  ).getQueue(  );

        if ( queue.size(  ) > 0 )
        {
            ICRMItem crmItem = null;

            do
            {
                crmItem = queue.consume(  );

                try
                {
                    CRMClientService.getService(  ).doProcessWS( crmItem );
                }
                catch ( HttpAccessException e )
                {
                    AppLogService.error( e );
                    // If connection failed, then put the item back to the file
                    queue.send( crmItem );

                    break;
                }
                catch ( CRMClientException e )
                {
                    AppLogService.error( e );
                    // Put the item back to the file
                    queue.send( crmItem );

                    break;
                }
            }
            while ( crmItem != null );
        }
    }
}
