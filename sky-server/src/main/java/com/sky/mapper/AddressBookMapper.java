package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    List<AddressBook> list(Long currentId);

    void insertAddressBook(AddressBook addressBook);

    AddressBook getAdressById(Long id);

    void update(AddressBook adressBook);

    AddressBook getDefault(Integer isDefault);

    void deleteById(Long id);
}
