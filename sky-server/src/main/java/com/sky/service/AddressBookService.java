package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    List<AddressBook> list();

    void insertAddressBook(AddressBook addressBook);

    void updateDefalut(Long id);

    AddressBook getDefault();

    AddressBook getAddressById(Long id);

    void updateById(AddressBook addressBook);

    void deleteById(Long id);
}
