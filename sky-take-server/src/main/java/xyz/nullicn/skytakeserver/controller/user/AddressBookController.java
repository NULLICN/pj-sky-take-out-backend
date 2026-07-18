package xyz.nullicn.skytakeserver.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.nullicn.context.BaseContext;
import xyz.nullicn.entity.AddressBook;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.AddressBookService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/addressBook")
@Tag(name = "C端-地址簿接口")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    @Operation(summary = "新增地址")
    public Result<String> add(@RequestBody @Valid AddressBook addressBook) {
        log.info("新增用户地址 id: {} 内容: {}", BaseContext.getCurrentId(), addressBook);
        addressBookService.add(addressBook);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "查询用户所有地址")
    public Result<List<AddressBook>> list() {
        log.info("查询用户所有地址 id: {}", BaseContext.getCurrentId());
        return Result.success(addressBookService.list());
    }

    @GetMapping("/default")
    @Operation(summary = "查询用户默认地址")
    public Result<AddressBook> defaultAddressBook() {
        log.info("查询用户默认地址 id: {}", BaseContext.getCurrentId());
        AddressBook address = addressBookService.getDefaultAddress();
        return Result.success(address);
    }

    @PutMapping
    @Operation(summary = "更新一条地址")
    public Result<String> update(@RequestBody @Valid AddressBook addressBook) {
        log.info("更新用户 {} 地址 {}",  BaseContext.getCurrentId(), addressBook);
        addressBookService.update(addressBook);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据地址id查询")
    public Result<AddressBook> get(@PathVariable("id") Long id) {
        log.info("查询id {} 地址", id);
        AddressBook address = addressBookService.getById(id);
        return Result.success(address);
    }

    @PutMapping("/default")
    @Operation(summary = "设定地址id为默认地址")
    public Result<String> setDefaultAddress(@RequestBody AddressBook addressBook) {
        log.info("更新默认地址 地址id {}", addressBook.getId());
        addressBookService.setDefaultAddress(addressBook.getId());
        return Result.success();
    }

    @DeleteMapping({"", "/"})
    @Operation(summary = "通过id删除地址")
    public Result<String> delete(@RequestParam Long id) {
        log.info("删除id {} 地址",  id);
        addressBookService.deleteById(id);
        return Result.success();
    }
}
