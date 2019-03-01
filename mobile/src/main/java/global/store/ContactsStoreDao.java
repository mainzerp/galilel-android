package global.store;

import global.AddressLabel;

import java.util.List;

public interface ContactsStoreDao<T> extends AbstractDbDao<T> {

    AddressLabel getContact(String address);

    void delete(AddressLabel data);

    List<AddressLabel> getMyAddresses();

    List<AddressLabel> getContacts();
}
