package com.thoughtworks.xstream.core;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

import java.util.ArrayList;
import java.util.List;

public class TreeUnmarshallerTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();
    }
	
	public void testUnmarshallingOfAliasedInterfaces() {		
		xstream.alias("addressBookInfo", AddressBookInfo.class, AddressBook.class);
		xstream.alias("addressInfo", AddressInfo.class, Address.class);
		AddressBookInfo initialObject = new AddressBook();
		String marshalledXML = xstream.toXML(initialObject);
		AddressBookInfo unmarshalledObject = (AddressBookInfo) xstream.fromXML(marshalledXML);
		assertEquals(marshalledXML, xstream.toXML(unmarshalledObject));
	}
	
	public interface AddressBookInfo {
		public List getAddresses();

		public void setAddresses(List address);
	}

	public static class AddressBook implements AddressBookInfo {
		private List addresses;

		public AddressBook() {
			addresses = new ArrayList();
			AddressInfo addr = new Address("Home", "Home");
			AddressInfo addr1 = new Address("Office", "Office");
			addresses.add(addr);
			addresses.add(addr1);
		}

		public List getAddresses() {
			return addresses;
		}

		public void setAddresses(List addresses) {
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
        
        private Address() {
        }

		public Address(String addr1, String addr2) {
			this.addr1 = addr1;
			this.addr2 = addr2;
		}

		public String getAddr1() {
			return addr1;
		}

		public String getAddr2() {
			return addr2;
		}

		public void setAddr1(String addr1) {
			this.addr1 = addr1;
		}

		public void setAddr2(String addr2) {
			this.addr2 = addr2;
		}
	}
}
