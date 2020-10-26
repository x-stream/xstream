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

package com.thoughtworks.xstream.core;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;


public class TreeUnmarshallerTest extends AbstractAcceptanceTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testUnmarshallingOfAliasedInterfaces() {
        xstream.alias("addressBookInfo", AddressBookInfo.class, AddressBook.class);
        xstream.alias("addressInfo", AddressInfo.class, Address.class);
        final AddressBookInfo initialObject = new AddressBook();
        final String marshalledXML = xstream.toXML(initialObject);
        final AddressBookInfo unmarshalledObject = (AddressBookInfo)xstream.fromXML(marshalledXML);
        assertEquals(marshalledXML, xstream.toXML(unmarshalledObject));
    }

    public interface AddressBookInfo {
        public List<AddressInfo> getAddresses();

        public void setAddresses(List<AddressInfo> address);
    }

    public static class AddressBook implements AddressBookInfo {
        private List<AddressInfo> addresses;

        public AddressBook() {
            addresses = new ArrayList<AddressInfo>();
            final AddressInfo addr = new Address("Home", "Home");
            final AddressInfo addr1 = new Address("Office", "Office");
            addresses.add(addr);
            addresses.add(addr1);
        }

        @Override
        public List<AddressInfo> getAddresses() {
            return addresses;
        }

        @Override
        public void setAddresses(final List<AddressInfo> addresses) {
            this.addresses = addresses;
        }
    }

    public interface AddressInfo {
        public String getAddr1();

        public String getAddr2();

        public void setAddr1(String addr1);

        public void setAddr2(String addr2);
    }

    public static class Address implements AddressInfo {
        private String addr1 = "addr1";

        private String addr2 = "addr2";

        @SuppressWarnings("unused")
        private Address() {
        }

        public Address(final String addr1, final String addr2) {
            this.addr1 = addr1;
            this.addr2 = addr2;
        }

        @Override
        public String getAddr1() {
            return addr1;
        }

        @Override
        public String getAddr2() {
            return addr2;
        }

        @Override
        public void setAddr1(final String addr1) {
            this.addr1 = addr1;
        }

        @Override
        public void setAddr2(final String addr2) {
            this.addr2 = addr2;
        }
    }
}
