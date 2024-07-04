package com.dperez.CarRegistry.controller.mapper;

import com.dperez.CarRegistry.controller.dtos.CarDTO;
import com.dperez.CarRegistry.service.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CarDTOMapper {

    CarDTOMapper INSTANCE = Mappers.getMapper(CarDTOMapper.class);

    @Mapping(source = "brand.name", target = "brand")
    CarDTO carToCarDTO(Car car);

    @Mapping(source = "brand", target = "brand.name")
    Car carDTOToCar(CarDTO carDTO);
}

