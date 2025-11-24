package com.sky.service.impl;

import com.sky.constant.IsDefaultConstant;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Override
    public List<AddressBook> list() {
        Long currentId = BaseContext.getCurrentId();
        return addressBookMapper.list(currentId);
    }

    @Override
    public void insertAddressBook(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.insertAddressBook(addressBook);
    }

    @Override
    public void updateDefalut(Long id) {
        //先查询出当前有没有默认地址
        AddressBook defaultAddress = addressBookMapper.getDefault(IsDefaultConstant.DEFAULT_YES);
        if (defaultAddress != null){
            //如果有就先将当前的这个设为不是默认地址
            defaultAddress.setIsDefault(IsDefaultConstant.DEFAULT_NO);
            addressBookMapper.update(defaultAddress);
        }
        //根据id查询出该id的地址信息
        AddressBook adressBook = addressBookMapper.getAdressById(id);
        //将地址设为默认地址
        adressBook.setIsDefault(IsDefaultConstant.DEFAULT_YES);
        //进行修改操作
        addressBookMapper.update(adressBook);
    }

    @Override
    public AddressBook getDefault() {
        return addressBookMapper.getDefault(IsDefaultConstant.DEFAULT_YES);
    }

    @Override
    public AddressBook getAddressById(Long id) {
        return addressBookMapper.getAdressById(id);
    }

    @Override
    public void updateById(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    @Override
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }
}
