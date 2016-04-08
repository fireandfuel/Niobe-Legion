/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (CompressedCommunication.java) is part of Niobe Legion (module niobe-legion-shared).
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
 *     along with Niobe Legion. If not, see <http://www.gnu.org/licenses/>.
 */

package niobe.legion.shared.communication;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import niobe.legion.shared.data.Stanza;
import org.tukaani.xz.DeltaOptions;
import org.tukaani.xz.FilterOptions;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

public class CompressedCommunication implements ICommunication
{
    private final ICommunication parent;
    private final String algorithm;
    private InputStream in;
    private OutputStream out;

    public CompressedCommunication(ICommunication parent, InputStream in, OutputStream out, String algorithm) throws
                                                                                                              IOException
    {
        this.parent = parent;
        this.algorithm = algorithm;
        setCompressedInputStreams(in, out);
    }

    @Override
    public void initInputReader(InputStream in) throws CommunicationException
    {
        try
        {
            setCompressedInputStreams(in, null);
        } catch(IOException e)
        {
            throw new CommunicationException(e);
        }
        this.parent.initInputReader(this.in);
    }

    private void setCompressedInputStreams(InputStream in, OutputStream out) throws IOException
    {
        if(this.in == null && in != null)
        {
            switch(this.algorithm)
            {
                case "gzip":
                    this.in = new GZIPInputStream(in, 4096);
                    break;
                case "xz":
                    this.in = new BufferedInputStream(new XZInputStream(in));
                    break;
            }
        }

        if(this.out == null && out != null)
        {
            switch(this.algorithm)
            {
                case "gzip":
                    this.out = new GZIPOutputStream(out, 4096, true);
                    break;
                case "xz":
                    FilterOptions[] filters = {new DeltaOptions(), new LZMA2Options(8)};
                    this.out = new XZOutputStream(out, filters){
                        @Override
                        public void flush() throws IOException
                        {
                            super.flush();
                            super.finish();
                        }
                    };
                    break;
            }

        }
    }

    @Override
    public void write(OutputStream out, Stanza stanza) throws IOException
    {
        this.setCompressedInputStreams(null, out);

        switch(this.algorithm)
        {
            case "gzip":
                this.parent.write(this.out, stanza);
                break;
            case "xz":
                this.parent.write(this.out, stanza);
                this.out = null;
                break;
        }
    }

    @Override
    public boolean hasNextStanza() throws CommunicationException
    {
        return this.parent.hasNextStanza();
    }

    @Override
    public Stanza getNextStanza() throws CommunicationException
    {
        return this.parent.getNextStanza();
    }

    @Override
    public void setCurrentStanza(Stanza currentStanza)
    {
        this.parent.setCurrentStanza(currentStanza);
    }

    @Override
    public String toString()
    {
        return this.algorithm + " compressed " + this.parent.toString();
    }
}
