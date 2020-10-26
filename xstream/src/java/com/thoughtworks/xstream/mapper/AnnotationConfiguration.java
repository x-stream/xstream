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

package com.thoughtworks.xstream.mapper;

/**
 * A helper interface for the configuration part of the AnnotationMapper.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.3
 * @deprecated As of 1.4.5, minimal JDK version will be 1.7 for next major release and AnnotationMapper can be used
 *             directly
 */
@Deprecated
public interface AnnotationConfiguration {

    void autodetectAnnotations(boolean mode);

    void processAnnotations(Class<?>... types);

}
