package xyz.nullicn.skytakeserver.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import xyz.nullicn.constant.MessageConstant;
import xyz.nullicn.context.BaseContext;
import xyz.nullicn.entity.AddressBook;
import xyz.nullicn.exception.BaseException;
import xyz.nullicn.skytakeserver.mapper.AddressBookMapper;
import xyz.nullicn.skytakeserver.service.AddressBookService;

import java.util.List;

@Slf4j
@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Override
    public void add(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    @Override
    public List<AddressBook> list() {
        AddressBook address = AddressBook.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        return addressBookMapper.list(address);
    }

    @Override
    public AddressBook getDefaultAddress() {
        AddressBook address = AddressBook.builder()
                .userId(BaseContext.getCurrentId())
                .isDefault(1)
                .build();
        List<AddressBook> addressBooks = addressBookMapper.list(address);
        if(!addressBooks.isEmpty()){
            return addressBooks.getFirst();
        } else {
            return null;
        }
    }

    @Override
    public void update(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.update(addressBook);
    }

    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id, BaseContext.getCurrentId());
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long id) {

        AddressBook checkAddress = getById(id);
        if(checkAddress == null){
            throw new RuntimeException("不存在此条地址");
        }
        if(!checkAddress.getUserId().equals(BaseContext.getCurrentId())){
            throw new RuntimeException("越权操作");
        }

        AddressBook address = new AddressBook();
                address.setUserId(BaseContext.getCurrentId());
                address.setIsDefault(0);

        // 先把用户的所有地址设为非默认地址
        addressBookMapper.updateIsDefaultByUserId(address);

        address.setId(id);
        address.setIsDefault(1);
        // 把指定id的地址设为默认地址
        addressBookMapper.update(address);
    }

    @Override
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id, BaseContext.getCurrentId());
    }
}
