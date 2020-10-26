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

package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.Converter;


/**
 * A special converter that prevents self-serialization. The serializing XStream instance adds a converter of this type
 * to prevent self-serialization and will throw an exception instead.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 * @deprecated As of 1.4.5 use {@link com.thoughtworks.xstream.core.util.SelfStreamingInstanceChecker}
 */
@Deprecated
public class SelfStreamingInstanceChecker extends com.thoughtworks.xstream.core.util.SelfStreamingInstanceChecker {

    public SelfStreamingInstanceChecker(final Converter defaultConverter, final Object xstream) {
        super(defaultConverter, xstream);
    }

}
