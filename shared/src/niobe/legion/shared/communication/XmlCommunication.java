/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (XmlCommunication.java) is part of Niobe Legion (module niobe-legion-shared).
 *
 *     Niobe Legion is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Niobe Legion is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Niobe Legion.  If not, see <http://www.gnu.org/licenses/>.
 */

package niobe.legion.shared.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import niobe.legion.shared.data.Stanza;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;

public class XmlCommunication implements ICommunication
{
    private Stanza currentStanza;

    private static final XMLInputFactory inputFactory = XMLInputFactory.newFactory();
    private XMLStreamReader reader;

    @Override
    public void initInputReader(DataInputStream in) throws CommunicationException
    {
        try
        {
            this.reader = this.inputFactory.createXMLStreamReader(in, "UTF-8");
        } catch(XMLStreamException e)
        {
            throw new CommunicationException(e);
        }
    }

    @Override
    public void write(DataOutputStream out, Stanza message) throws IOException
    {
        if(out != null)
        {
            switch(message.getEventType())
            {
                case START_ELEMENT:
                    out.write(("<" + message.getName()).getBytes("UTF-8"));

                    if(message.hasNoAttributes())
                    {
                        if(message.isEmptyElement())
                        {
                            out.write("/>".getBytes("UTF-8"));
                        } else
                        {
                            out.write(">".getBytes("UTF-8"));
                        }
                    } else
                    {
                        message.forEachAttribute((attrName, attribute) -> {
                            if(attribute != null)
                            {
                                if(!attribute.startsWith("\"") && !attribute.endsWith("\"") &&
                                        !attribute.startsWith("'") && !attribute.endsWith("'"))
                                {
                                    attribute = "\"" + attribute + "\"";
                                }
                                try
                                {
                                    out.write((" " + attrName + "=" + attribute).getBytes("UTF-8"));
                                } catch(IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        });

                        if(message.isEmptyElement())
                        {
                            out.write("/>".getBytes("UTF-8"));
                        } else
                        {
                            out.write(">".getBytes("UTF-8"));
                        }
                    }

                    break;
                case CHARACTERS:
                    out.write(("<" + message.getName()).getBytes("UTF-8"));

                    if(message.hasNoAttributes())
                    {
                        out.write(">".getBytes("UTF-8"));
                    } else
                    {
                        message.forEachAttribute((attrName, attribute) -> {
                            if(attribute != null)
                            {
                                try
                                {
                                    out.write((" " + attrName + "=\"" + attribute + "\"").getBytes("UTF-8"));
                                } catch(IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        });
                        out.write(">".getBytes("UTF-8"));
                    }

                    out.write(message.getValue().getBytes("UTF-8"));
                    // next to END_ELEMENT for closing tag
                case END_ELEMENT:
                    out.write(("</" + message.getName() + ">").getBytes("UTF-8"));
                    break;
            }

            out.flush();
        }

    }

    @Override
    public boolean hasNextStanza() throws CommunicationException
    {
        try
        {
            return reader != null && reader.hasNext();
        } catch(XMLStreamException e)
        {
            throw new CommunicationException(e);
        }
    }

    @Override
    public Stanza getNextStanza() throws CommunicationException
    {
        try
        {
            this.reader.next();
            switch(this.reader.getEventType())
            {
                case START_ELEMENT:
                    // create current stanza from reader
                    this.currentStanza = new Stanza();
                    this.currentStanza.setName(((this.reader.getName().getPrefix() != null) ? (this.reader.getName()
                            .getPrefix() + ":") : "") + this.reader.getName().getLocalPart());
                    this.currentStanza.setLocalName(this.reader.getLocalName());
                    this.currentStanza.setNameSpaceURI(this.reader.getName().getNamespaceURI());
                    this.currentStanza.setEventType(XMLStreamConstants.START_ELEMENT);

                    Logger.debug(LegionLogger.RECEIVED, "received START_ELEMENT : " + this.currentStanza.getName());

                    for(int i = 0; i < this.reader.getAttributeCount(); i++)
                    {
                        this.currentStanza
                                .putAttribute(this.reader.getAttributeLocalName(i), this.reader.getAttributeValue(i));
                        Logger.debug(LegionLogger.RECEIVED,
                                     "attribute " + this.reader.getAttributeLocalName(i) + " : " +
                                             this.reader.getAttributeValue(i));
                    }
                    return this.currentStanza;
                case CHARACTERS:
                    // set content of stanza
                    this.currentStanza.setValue(this.reader.getText());
                    Logger.debug(LegionLogger.RECEIVED,
                                 "received CHARACTERS : " + this.currentStanza.getName() + " value: " +
                                         this.reader.getText());
                    this.currentStanza.setEventType(XMLStreamConstants.CHARACTERS);
                    return this.currentStanza;
                case END_ELEMENT:
                    this.currentStanza = new Stanza(this.currentStanza);
                    this.currentStanza.setEventType(XMLStreamConstants.END_ELEMENT);
                    Logger.debug(LegionLogger.RECEIVED, "received END_ELEMENT : " + this.currentStanza.getName());
                    return this.currentStanza;
                default:
                    Logger.debug(LegionLogger.RECEIVED, "Unknown event type " + this.reader.getEventType());
                    break;
            }
        } catch(XMLStreamException e)
        {
            throw new CommunicationException(e);
        }
        return this.currentStanza;
    }

    @Override
    public void setCurrentStanza(Stanza currentStanza)
    {
        this.currentStanza = currentStanza;
    }
}
