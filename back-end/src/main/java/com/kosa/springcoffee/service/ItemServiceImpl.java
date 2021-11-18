package com.kosa.springcoffee.service;

import com.kosa.springcoffee.dto.*;
import com.kosa.springcoffee.entity.Board;
import com.kosa.springcoffee.entity.Item;
import com.kosa.springcoffee.entity.ItemImg;
import com.kosa.springcoffee.repository.ItemImgRepository;
import com.kosa.springcoffee.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{


    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final ItemImgService itemImgService;
    private final FileHandler fileHandler;
    @Override
    public Long create(ItemDTO dto) {
        Item entity = dtoToEntity(dto);
        itemRepository.save(entity);

        return entity.getItemNo();
    }

    @Override
    public Long createWithImg(ItemDTO itemDTO, List<MultipartFile> itemImgFileList) throws Exception{
        Item item = new Item(itemDTO.getName(),itemDTO.getContent(),itemDTO.getStockQuantity(),itemDTO.getPrice(),itemDTO.getCategory());

        List<ItemImg> imgList = new ArrayList<>();
        imgList = fileHandler.parseFile(item,itemImgFileList);

        if (!imgList.isEmpty()){
            for (ItemImg img : imgList) {
                img.setItem(item);

                itemImgRepository.save(img);
            }
        }
        Item getItem = itemRepository.save(item);
        return getItem.getItemNo();
    }





    @Override
    public void modify(ItemDTO dto) {
        Long itemNo = dto.getItemNo();
        Optional<Item> result = itemRepository.findById(itemNo);

        if(result.isPresent()){
            Item item = result.get();
            item.changeName(dto.getName());
            item.changeContent(dto.getContent());
            item.changeStockQuantity(dto.getStockQuantity());
            item.changePrice(dto.getPrice());
            item.changeCategory(dto.getCategory());
            itemRepository.save(item);
        }
    }

    @Override
    public void modifyWithImg(Long itemNo, ItemDTO itemDTO, List<MultipartFile> files) throws Exception {
        Item item = itemRepository.findByItemNo(itemNo);

        List<ItemImg> imgList = fileHandler.parseFile(item,files);

        if (!imgList.isEmpty()){
            for (ItemImg img : imgList){
                itemImgRepository.save(img);
            }
        }

        item.updateItem(itemDTO);

        itemRepository.save(item);
    }


    @Override
    public void remove(Long itemNo) {
        itemRepository.deleteById(itemNo);
        itemImgRepository.deleteAllByItemNo(itemNo);
    }

    @Override
    public PageResultDTO<ItemDTO, Item> readAll(PageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("itemNo").descending());

        Page<Item> result = itemRepository.findAll(pageable);

        Function<Item, ItemDTO> fn = (entity -> entityToDto(entity));

        return new PageResultDTO<>(result, fn);
    }

    @Override
    public List<ItemReadDTO> readAllItem() {
        List<Item> itemList = itemRepository.findAll();
        List<ItemReadDTO> dtoList = new ArrayList<>();
        for (Item entity : itemList){
            ItemReadDTO dto = ItemReadDTO.builder()
                    .name(entity.getName())
                    .content(entity.getContent())
                    .itemNo(entity.getItemNo())
                    .category(entity.getCategory())
                    .price(entity.getPrice())
                    .stockQuantity(entity.getStockQuantity())
                    .fileId(entity.getItemImg().get(0).getItemImgNo())
                    .build();
            System.out.println(dto.getName());
            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public PageResultDTO<ItemDTO, Item> getCategory(CategoryPageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("itemNo").descending());

        Page<Item> result = itemRepository.findByCategory(requestDTO.getCategory(), pageable);

        Function<Item, ItemDTO> fn = (entity -> entityToDto(entity));

        return new PageResultDTO<>(result, fn);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponseDTO searchById(Long id, List<Long> fileId){
        Item entity = itemRepository.findByItemNo(id);


        return new ItemResponseDTO(entity, fileId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> searchAll() {
        return itemRepository.findAll();
    }

}