package xyz.nullicn.skytakeserver.service;

import xyz.nullicn.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    void add(AddressBook addressBook);

    List<AddressBook> list();

    AddressBook getDefaultAddress();

    void update(AddressBook addressBook);

    AddressBook getById(Long id);

    void setDefaultAddress(Long id);

    void deleteById(Long id);
}
