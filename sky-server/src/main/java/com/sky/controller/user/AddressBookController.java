package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "C端-地址薄接口")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;
    @GetMapping("/list")
    @ApiOperation("查询所有的地址信息")
    public Result<List<AddressBook>> list(){
        List<AddressBook> addressBooks = addressBookService.list();
        return Result.success(addressBooks);
    }

    @PostMapping
    @ApiOperation("新增地址信息")
    public Result insertAddressBook(@RequestBody AddressBook addressBook){
        addressBookService.insertAddressBook(addressBook);
        return Result.success();
    }


    @PutMapping("/default")
    @ApiOperation("设置默认的地址")
    public Result updateDefault(@RequestBody Map<String, Integer> map){
        Integer id = map.get("id");
        log.info("要修改的id为:{}", id);
        addressBookService.updateDefalut(id);
        return Result.success();
    }

    @GetMapping("/default")
    @ApiOperation("查看默认地址")
    public Result<AddressBook> getDefault(){
        AddressBook addressBook = addressBookService.getDefault();
        return Result.success(addressBook);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> getAddressById(@PathVariable Integer id){
        AddressBook addressBook = addressBookService.getAddressById(id);
        return Result.success(addressBook);
    }

    @PutMapping
    @ApiOperation("根据id修改地址")
    public Result updateById(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("根据id删除地址")
    public Result deleteById(Integer id){
        log.info("要删除的地址id为:{}", id);
        addressBookService.deleteById(id);
        return Result.success();
    }
}
