/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.entity.mime.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.arvix.vrdata.been.Status;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MIME;
import org.apache.http.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Binary body part backed by a file.
 *
 * @see org.apache.http.entity.mime.MultipartEntityBuilder
 *
 * @since 4.0
 */
public class FileProgressBody extends AbstractContentBody {
	private static final Logger log = LoggerFactory
			.getLogger(FileProgressBody.class);
    private final File file;
    private final String filename;
    private Status status;

    /**
     * @since 4.1
     *
     * @deprecated (4.3) use {@link FileProgressBody#FileBody(File, ContentType, String)}
     *   or {@link org.apache.http.entity.mime.MultipartEntityBuilder}
     */
    @Deprecated
    public FileProgressBody(final File file,
                    final String filename,
                    final String mimeType,
                    final String charset) {
        this(file, ContentType.create(mimeType, charset), filename);
    }

    /**
     * @since 4.1
     *
     * @deprecated (4.3) use {@link FileProgressBody#FileBody(File, ContentType)}
     *   or {@link org.apache.http.entity.mime.MultipartEntityBuilder}
     */
    @Deprecated
    public FileProgressBody(final File file,
                    final String mimeType,
                    final String charset) {
        this(file, null, mimeType, charset);
    }

    /**
     * @deprecated (4.3) use {@link FileProgressBody#FileBody(File, ContentType)}
     *   or {@link org.apache.http.entity.mime.MultipartEntityBuilder}
     */
    @Deprecated
    public FileProgressBody(final File file, final String mimeType) {
        this(file, ContentType.create(mimeType), null);
    }

    public FileProgressBody(final File file) {
        this(file, ContentType.DEFAULT_BINARY, file != null ? file.getName() : null);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @since 4.3
     */
    public FileProgressBody(final File file, final ContentType contentType, final String filename) {
        super(contentType);
        Args.notNull(file, "File");
        this.file = file;
        this.filename = filename == null ? file.getName() : filename;
    }

    /**
     * @since 4.3
     */
    public FileProgressBody(final File file, final ContentType contentType) {
        this(file, contentType, file != null ? file.getName() : null);
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }

    @Override
    public void writeTo(final OutputStream out) throws IOException {
        Args.notNull(out, "Output stream");
        final InputStream in = new FileInputStream(this.file);
        long fileSize = this.file.length();
        try {
            final byte[] tmp = new byte[4096];
            int l;
            long uploaded = 0;
            while ((l = in.read(tmp)) != -1) {
                out.write(tmp, 0, l);
                uploaded = uploaded +l;
                if (status != null) {
                    status.addMessage("progress: " + uploaded + "/" + fileSize);
                }
                //UILog.getInstance().logProgress(fileSize, uploaded,filename);
            }
            out.flush();
        } finally {
            in.close();
        }
    }

    @Override
    public String getTransferEncoding() {
        return MIME.ENC_BINARY;
    }

    @Override
    public long getContentLength() {
        return this.file.length();
    }

    @Override
    public String getFilename() {
        return filename;
    }

    public File getFile() {
        return this.file;
    }

}
