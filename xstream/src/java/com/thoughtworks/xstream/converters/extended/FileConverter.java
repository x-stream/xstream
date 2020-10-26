/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.converters.extended;

import java.io.File;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converts a {@link File}.
 * 
 * <p>This converter will take care of storing and retrieving {@link File} with either an absolute path OR a relative path
 * depending on how they were created.</p>
 * 
 * @author Joe Walnes
 */
public class FileConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == File.class;
    }

    @Override
    public Object fromString(final String str) {
        return new File(str);
    }

    @Override
    public String toString(final Object obj) {
        return ((File)obj).getPath();
    }

}
