package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    List<AddressBook> list();

    void insertAddressBook(AddressBook addressBook);

    void updateDefalut(Integer id);

    AddressBook getDefault();

    AddressBook getAddressById(Integer id);

    void updateById(AddressBook addressBook);

    void deleteById(Integer id);
}
